package com.vladyslav.offlinefilmtracker.Managers;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

/**
 * Background Async Task to download file
 */
public class DownloadHelper extends AsyncTask<String, String, String> {

    private ProgressDialog dialog;
    private Callable callable;
    private Callable stopper;

    public DownloadHelper(ProgressDialog dialog, Callable callable, Callable stopper) {
        this.dialog = dialog;
        this.callable = callable;
        this.stopper = stopper;
    }

    /**
     * Before starting background thread Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

    /**
     * Downloading file in background thread
     *
     * @return
     */
    @Override
    protected String doInBackground(String... f_url) {
        for (int i = 0; i < f_url.length - 1; ++i) {
            try {
                URL url = new URL(f_url[i]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();
                dialog.setMax(lengthOfFile / 1024 / 1024);

                InputStream inputStream = url.openStream();
                FileOutputStream outputStream = new FileOutputStream(f_url[i + 1]);

                byte data[] = new byte[1024];
                int count = 0;
                long total = 0;

                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) (total / 1024 / 1024));

                    // writing data to file
                    outputStream.write(data, 0, count);
                }

                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Updating progress bar
     */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        dialog.setProgress(Integer.parseInt(progress[0]));
    }

    /**
     * After completing background task Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
        dialog.hide();
        try {
            callable.call();
            stopper.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}