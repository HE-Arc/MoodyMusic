package ch.hearc.moodymusic.detection;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static ch.hearc.moodymusic.tools.Constants.SKY_API_KEY;
import static ch.hearc.moodymusic.tools.Constants.SKY_API_SEC;

/**
 * Created by axel.rieben on 12.11.2017.
 */

public class DetectionRequester extends AsyncTask<String, Integer, String> {

    public static final String TAG = "DetectionRequester";

    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private String error;
    private Context context;

    public DetectionRequester(Context context) {
        progressDialog = new ProgressDialog(context);
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog.setMessage("Please wait during processing");
        progressDialog.setCancelable(false);
        progressDialog.show();

        error = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog).create();
        } else {
            alertDialog = new AlertDialog.Builder(context).create();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
        httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        HttpClient httpclient = new DefaultHttpClient(httpParams);

        HttpPost httppost = new HttpPost("http://api.skybiometry.com/fc/faces/detect.json?api_key=" + SKY_API_KEY + "&api_secret=" + SKY_API_SEC + "&attributes=mood");
        File file = new File(strings[0]);

        MultipartEntity mpEntity = new MultipartEntity();
        ContentBody cbFile = new FileBody(file, "image/jpeg");
        mpEntity.addPart("userfile", cbFile);

        httppost.setEntity(mpEntity);
        System.out.println("executing request " + httppost.getRequestLine());

        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity resEntity = response.getEntity();

        System.out.println(response.getStatusLine());
        String moodResult = null;
        try {
            if (resEntity != null) {
                String requestResult = EntityUtils.toString(resEntity);
                System.out.println(requestResult);
                moodResult = decodeJson(requestResult);
            }

            if (resEntity != null) {
                resEntity.consumeContent();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
        }

        httpclient.getConnectionManager().shutdown();
        return moodResult;
    }

    private String decodeJson(String result) throws Exception {
        try {
            JSONObject jsonResponse = new JSONObject(result);
            String status = jsonResponse.getString("status");

            if (status.equals("success")) {
                JSONObject usageObject = jsonResponse.getJSONObject("usage");
                Integer remainingCalls = usageObject.getInt("remaining");

                if (remainingCalls > 5) {
                    JSONArray photos = jsonResponse.getJSONArray("photos");
                    JSONObject number = photos.getJSONObject(0);
                    JSONArray tags = number.getJSONArray("tags");

                    if (tags.length() == 1) {
                        JSONObject attributes = tags.getJSONObject(0).getJSONObject("attributes");
                        return attributes.getJSONObject("mood").getString("value");
                    } else {
                        throw new Exception("No face has been detected");
                    }

                } else {
                    throw new Exception("Error the web service is not available for now");
                }
            } else {
                throw new Exception("Error impossible to reach the web service");
            }
        } catch (JSONException e) {
            throw new Exception("An error occured the web service may not be available for now");
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        showDialogMood(result);
    }

    private void showDialogMood(String mood) {
        if (mood != null) {
            alertDialog.setTitle("MoodEnum Detection");
            alertDialog.setMessage("You seem to be " + mood + " !\n\n" + "Would you like to play some music that fit your current mood ?");

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            alertDialog.setIcon(android.R.drawable.ic_dialog_info);

        } else {
            alertDialog.setTitle("Sorry");
            alertDialog.setMessage(error + " !");

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Nothing
                        }
                    });

            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        }

        alertDialog.show();
    }

}
