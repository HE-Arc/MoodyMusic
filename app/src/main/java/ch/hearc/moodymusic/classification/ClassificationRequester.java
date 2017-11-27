package ch.hearc.moodymusic.classification;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by axel.rieben on 27.11.2017.
 */

public class ClassificationRequester extends AsyncTask<String, Integer, String> {

    public static final String TAG = "ClassificationRequester";

    private ProgressDialog progressDialog;
    private String error;
    private Context context;

    public ClassificationRequester(Context context) {
        progressDialog = new ProgressDialog(context);
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog.setMessage("Please wait during processing");
        progressDialog.setCancelable(true);
        progressDialog.show();

        error = "";
    }

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
