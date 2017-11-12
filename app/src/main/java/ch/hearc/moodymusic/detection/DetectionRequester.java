package ch.hearc.moodymusic.detection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
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
    private Activity activity;

    public DetectionRequester(Activity activity) {
        this.activity = activity;
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Please wait during processing");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

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
        String moodResult = "";
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
        }

        httpclient.getConnectionManager().shutdown();
        Log.v(TAG, "Result : " + moodResult);
        return moodResult;
    }

    private String decodeJson(String result) {
        String error = "";
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
                        error = "No face has been detected";
                    }

                } else {
                    error = "Error the web service is not available for now";
                }
            } else {
                error = "Error impossible to reach the web service";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v(TAG, " " + error);
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(String result) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
