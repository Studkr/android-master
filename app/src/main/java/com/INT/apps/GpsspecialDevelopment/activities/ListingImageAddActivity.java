package com.INT.apps.GpsspecialDevelopment.activities;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.BuildConfig;
import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.assets.ListingImageUploadFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.assets.ListingImagesPostedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.assets.PostListingImagesEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.INT.apps.GpsspecialDevelopment.utils.FilePathResolver;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class ListingImageAddActivity extends BaseActivity {

    private static final int PERMISSION_STORAGE = 3;
    private static final String PHOTOS_PATH = "photos";
    private static int CODE_GALLERY_IMAGE = 101;
    private static int CODE_CAMERA_IMAGE = 102;
    private static int CODE_LOGIN = 103;
    public static String ARG_LISTING_ID = "listing_id";
    public static String CODE_SAVED_COUNT = "savedCount";

    @InjectView(R.id.image_grid)
    GridView imagesGrid;
    @InjectView(R.id.add_from_camera)
    Button addByCamera;
    @InjectView(R.id.upload_photo)
    Button mUploadButton;
    Uri mCurrentPhotoPath;
    private String listingId;
    HashMap<Uri, File> cameraFilesPaths = new HashMap<>();
    ProgressDialog mProgressDialog;
    private String mRequestKey;
    boolean mCheckSessionOnSave = false;

    private static final int PERMISSION_CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listingId = getIntent().getStringExtra(ARG_LISTING_ID);
        setContentView(R.layout.activity_listing_image_add);
        ButterKnife.inject(this);
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
            addByCamera.setVisibility(View.GONE);
        }
        ImageArrayAdapter arrayAdapter = new ImageArrayAdapter(this);
        imagesGrid.setAdapter(arrayAdapter);

        Toolbar toolbar = setToolbar();
        toolbar.setTitle(R.string.listing_image_activity_title);
        if (savedInstanceState != null) {
            ArrayList<String> uriList = savedInstanceState.getStringArrayList("uriImageList");
            if (uriList != null) {
                for (String uriString : uriList) {
                    try {
                        Uri uri = Uri.parse(uriString);
                        assignImage(uri);
                    } catch (NullPointerException e) {
                        Timber.tag("Exe").d(e);
                    }
                }
            }
        }
        if (savedInstanceState != null && savedInstanceState.getBoolean("wasLoggedIn", false) == true) {
            mCheckSessionOnSave = true;
        } else if (UserSession.getUserSession().isLoggedIn() == false) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, CODE_LOGIN);
        }
    }

    @OnClick(R.id.add_from_gallery)
    public void addFromGallery() {
        if (!checkAndRequestStoragePermission()) {
            return;
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), CODE_GALLERY_IMAGE);
    }

    private boolean checkAndRequestStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[] {READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
        return false;
    }

    @Override
    protected void onResume() {
        IOBus.getInstance().register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        IOBus.getInstance().unregister(this);
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions.length == 0) return;
        switch (requestCode) {
            case PERMISSION_CAMERA :
                if (grantResults[0] == PERMISSION_GRANTED) {
                    takePhoto();
                }
                break;
            case PERMISSION_STORAGE :
                if (grantResults[0] == PERMISSION_GRANTED) {
                    addFromGallery();
                }
                break;
        }
    }

    private boolean requestedCameraPermissions() {
        if (ActivityCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PERMISSION_CAMERA);
        return false;
    }

    @OnClick(R.id.add_from_camera)
    public void addFromCamera() {
        if (requestedCameraPermissions()) takePhoto();
    }

    private void takePhoto() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
            Timber.tag("camera not enabled").d("not enabled");
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, "Problem in opening camera " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (photoFile != null) {
                mCurrentPhotoPath = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                cameraFilesPaths.put(mCurrentPhotoPath, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoPath);
                startActivityForResult(takePictureIntent, CODE_CAMERA_IMAGE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_LOGIN) {
            if (UserSession.getUserSession().isLoggedIn() == false) {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
        if (requestCode == CODE_GALLERY_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            assignImage(imageUri);
        } else if (requestCode == CODE_CAMERA_IMAGE && resultCode == RESULT_OK && mCurrentPhotoPath != null) {
            assignImage(mCurrentPhotoPath);
        }
        if (requestCode == CODE_CAMERA_IMAGE && resultCode != RESULT_OK && mCurrentPhotoPath != null) {
            cameraFilesPaths.remove(mCurrentPhotoPath);
        }
    }

    private void assignImage(Uri imageUri) {
        ((ImageArrayAdapter) imagesGrid.getAdapter()).add(imageUri);
        ((ImageArrayAdapter) imagesGrid.getAdapter()).notifyDataSetChanged();
        mUploadButton.setVisibility(View.VISIBLE);
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(getExternalFilesDir(null), PHOTOS_PATH);
        if (!storageDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            storageDir.mkdir();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ArrayAdapter<Uri> imagesAdapeter = (ArrayAdapter<Uri>) imagesGrid.getAdapter();
        ArrayList<String> uriList = new ArrayList<>();
        for (int i = 0; i < imagesAdapeter.getCount(); i++) {
            uriList.add(imagesAdapeter.getItem(i).toString());
        }
        outState.putStringArrayList("uriImageList", uriList);
        if (UserSession.getUserSession().isLoggedIn()) {
            outState.putBoolean("wasLoggedIn", true);
        }
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.upload_photo)
    public void postToServer() {
        if (UserSession.getUserSession().isLoggedIn()) {
            mCheckSessionOnSave = false;
        } else if (mCheckSessionOnSave == true) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, CODE_LOGIN);
            return;
        }
        ArrayAdapter<Uri> imagesAdapeter = (ArrayAdapter<Uri>) imagesGrid.getAdapter();
        ArrayList<String> filesPath = new ArrayList<>();
        for (int i = 0; i < imagesAdapeter.getCount(); i++) {
            Uri uri = imagesAdapeter.getItem(i);
            if (cameraFilesPaths.containsKey(uri)) {
                filesPath.add(cameraFilesPaths.get(uri).getPath());
            } else {
                filesPath.add(getPath(uri));
            }
        }
        if (filesPath.size() > 0) {
            mProgressDialog = ProgressDialog.show(this, "Saving", "Uploading images", true);
            mRequestKey = UUID.randomUUID().toString();
            IOBus.getInstance().post(new PostListingImagesEvent(listingId, filesPath, mRequestKey));
        } else {
            new AlertDialog.Builder(this).setTitle(R.string.please_select_file_or_upload).show();
        }
    }

    public String getPath(Uri uri) {
        return FilePathResolver.getPath(this, uri);
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Subscribe
    public void onImagesUploaded(ListingImagesPostedEvent event) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog.hide();
            mProgressDialog = null;
        }
        Integer savedCount = event.getAssetAddResult().getImagesAddResult().getSavedCount();
        if (savedCount > 0) {
            Intent result = new Intent();
            result.putExtra(CODE_SAVED_COUNT, savedCount);
            setResult(RESULT_OK, result);
            String successMsg = getResources().getString(R.string.photos_uploaded_success, savedCount.toString());
            Toast.makeText(getApplicationContext(), successMsg, Toast.LENGTH_LONG).show();
        } else {
            String successMsg = getResources().getString(R.string.no_photo_uploaded);
            Toast.makeText(getApplicationContext(), successMsg, Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
        }
        finish();

    }

    @Subscribe
    public void OnUploadError(ListingImageUploadFailedEvent event) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog.hide();
            mProgressDialog = null;
        }

        new AlertDialog.Builder(this).
                setTitle(R.string.file_uploading_problem).
                setMessage(event.getError().getMessage()).show();
    }

    class ImageArrayAdapter extends ArrayAdapter<Uri> {
        ImageArrayAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_add_image_row, null);
            }
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            //ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(200, 200);
            //imageView.setPadding(10,10,10,10);
            // imageView.setLayoutParams(lp);
            Uri image = getItem(position);

            if (image != null) {
                Picasso.get().load(image).into(imageView);
            }
            return convertView;
        }
    }
}


