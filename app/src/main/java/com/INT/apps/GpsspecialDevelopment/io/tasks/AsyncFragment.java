package com.INT.apps.GpsspecialDevelopment.io.tasks;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

/**
 * Fragment for async tasks which keep running during
 * configuration changes also.
 */
public class AsyncFragment extends Fragment {
;
    public static final String CALLER_ACTIVITY = "caller_activity";
    public static final String CALLER_FRAGMENT = "caller_fragment";
    private AsyncCallbacks mAsyncCallbacks;
    private AsyncFragmentTask mAsyncTask;
    // TODO: Rename and change types of parameters
    private String mCallerType;


    public AsyncFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mAsyncTask.execute();

    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if(mCallerType == CALLER_ACTIVITY)
        {
            mAsyncCallbacks = (AsyncCallbacks)activity;
        }else
        {
            mAsyncCallbacks = (AsyncCallbacks)getTargetFragment();
        }
        mAsyncTask.setCallbacks(mAsyncCallbacks);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mAsyncTask.removeCallbacks();
    }
    interface AsyncCallbacks
    {
        void onPreExecute();
        void onProgressUpdate(int percent);
        void onCancelled();
        void onPostExecute();
    }
}
