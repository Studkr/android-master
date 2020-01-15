package com.INT.apps.GpsspecialDevelopment.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import com.INT.apps.GpsspecialDevelopment.CrowdvoxApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;

/**
 * Creates resized images without exploding memory. Uses the method described in android
 * documentation concerning bitmap allocation, which is to subsample the image to a smaller size,
 * close to some expected size. This is required because the android standard library is unable to
 * create a reduced size image from an image file using memory comparable to the final size (and
 * loading a full sized multi-megapixel picture for processing may exceed application memory budget).
 *
 * implementation by user @hdante
 * http://stackoverflow.com/users/1797000/hdante
 */

public class DevicePicture {

    static int MAX_WIDTH = 600;
    static int MAX_HEIGHT = 800;
    Uri uri;
    ContentResolver resolver;
    String path;
    Matrix orientation;
    int storedHeight;
    int storedWidth;

    public DevicePicture(Uri uri, ContentResolver resolver)
    {
        this.uri = uri;
        this.resolver = resolver;
    }

    private boolean getInformation() throws IOException
    {
        if (getInformationFromMediaDatabase())
            return true;

        if (getInformationFromFileSystem())
            return true;

        return false;
    }

    /* Support for gallery apps and remote ("picasa") images */
    private boolean getInformationFromMediaDatabase()
    {
        String[] fields = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION};
        Cursor cursor = resolver.query(uri, fields, null, null, null);

        if (cursor == null)
            return false;

        cursor.moveToFirst();
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
        this.orientation = new Matrix();
        this.orientation.setRotate(orientation);
        cursor.close();

        return true;
    }

    /* Support for file managers and dropbox */
    private boolean getInformationFromFileSystem() throws IOException
    {
        path = uri.getPath();

        if (path == null)
            return false;

        ExifInterface exif = new ExifInterface(path);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        this.orientation = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                /* Identity matrix */
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                this.orientation.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                this.orientation.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                this.orientation.setScale(1, -1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                this.orientation.setRotate(90);
                this.orientation.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                this.orientation.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                this.orientation.setRotate(-90);
                this.orientation.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                this.orientation.setRotate(-90);
                break;
        }

        return true;
    }

    private boolean getStoredDimensions() throws IOException
    {
        InputStream input = resolver.openInputStream(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(resolver.openInputStream(uri), null, options);

        /* The input stream could be reset instead of closed and reopened if it were possible
           to reliably wrap the input stream on a buffered stream, but it's not possible because
           decodeStream() places an upper read limit of 1024 bytes for a reset to be made (it calls
           mark(1024) on the stream). */
        input.close();

        if (options.outHeight <= 0 || options.outWidth <= 0)
            return false;

        storedHeight = options.outHeight;
        storedWidth = options.outWidth;

        return true;
    }

    public Bitmap getBitmap() throws IOException
    {
        if (!getInformation())
            throw new FileNotFoundException();

        if (!getStoredDimensions())
            throw new InvalidObjectException(null);

        RectF rect = new RectF(0, 0, storedWidth, storedHeight);
        orientation.mapRect(rect);
        int width = (int) rect.width();
        int height = (int) rect.height();
        int subSample = 1;

        while (width > MAX_WIDTH || height > MAX_HEIGHT) {
            width /= 2;
            height /= 2;
            subSample *= 2;
        }

        if (width == 0 || height == 0)
            throw new InvalidObjectException(null);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = subSample;
        Bitmap subSampled = BitmapFactory.decodeStream(resolver.openInputStream(uri), null, options);

        Bitmap picture;
        if (!orientation.isIdentity()) {
            picture = Bitmap.createBitmap(subSampled, 0, 0, options.outWidth, options.outHeight,
                    orientation, false);
            subSampled.recycle();
        } else
            picture = subSampled;

        return picture;
    }

    //accets resolved path only. call FilePathResolver to resolve
    public static String compressImage(Uri imageUri) {

        String filePath = imageUri.getPath();
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 1200.0f;
        float maxWidth = 900.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }
    public static String getFilename() {
        File file = CrowdvoxApplication.getAppInstance().getDir("Images",0);
        String uriString = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriString;

    }
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}