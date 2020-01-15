package com.INT.apps.GpsspecialDevelopment.io.tasks;

import android.os.AsyncTask;

/**
 * Created by shrey on 22/4/15.
 */
abstract class AsyncFragmentTask extends AsyncTask<Void, Integer, Void>
{
    protected abstract Void doInBackground(Void... params);
    AsyncFragment.AsyncCallbacks mAsyncCallbacks;

    final void setCallbacks(AsyncFragment.AsyncCallbacks asyncCallbacks)
    {
        mAsyncCallbacks = asyncCallbacks;
    }

    final void removeCallbacks()
    {
        mAsyncCallbacks = null;
    }
    @Override
    protected void onPreExecute()
    {
        if(mAsyncCallbacks != null)
        {
            mAsyncCallbacks.onPreExecute();
        }
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(mAsyncCallbacks != null)
        {
            mAsyncCallbacks.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(mAsyncCallbacks != null)
        {
            mAsyncCallbacks.onCancelled();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(mAsyncCallbacks != null)
        {
            mAsyncCallbacks.onPostExecute();
        }

    }
}

