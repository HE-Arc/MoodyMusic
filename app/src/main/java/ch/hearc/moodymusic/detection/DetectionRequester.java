package ch.hearc.moodymusic.detection;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
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

import ch.hearc.moodymusic.R;
import ch.hearc.moodymusic.ui.PlayerFragment;

import static ch.hearc.moodymusic.tools.Constants.SKY_API_KEY;
import static ch.hearc.moodymusic.tools.Constants.SKY_API_SEC;

/**
 * Created by axel.rieben on 12.11.2017.
 * Class executing asynchronously a HTTP request to SkyBiometry and showing the result in an Alertdialog.
 */

public class DetectionRequester extends AsyncTask<String, Integer, String> {
    public static final String TAG = "DetectionRequester";

    //Input
    private Context mContext;
    private PlayerFragment mPlayerFragment;

    //Dialog
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;

    //Tool
    private String mError;

    public DetectionRequester(Context context, PlayerFragment playerFragment) {
        mProgressDialog = new ProgressDialog(context);
        this.mContext = context;
        this.mPlayerFragment = playerFragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog.setMessage("Please wait during processing");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        mError = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAlertDialog = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog).create();
        } else {
            mAlertDialog = new AlertDialog.Builder(mContext).create();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        //Create HTTP Request
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
        httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpClient httpclient = new DefaultHttpClient(httpParams);
        HttpPost httppost = new HttpPost("http://api.skybiometry.com/fc/faces/detect.json?api_key=" + SKY_API_KEY + "&api_secret=" + SKY_API_SEC + "&attributes=mood");

        //Get the photo and put it in body of the request
        File file = new File(strings[0]);
        MultipartEntity mpEntity = new MultipartEntity();
        ContentBody cbFile = new FileBody(file, "image/jpeg");
        mpEntity.addPart("userfile", cbFile);

        httppost.setEntity(mpEntity);
        Log.w(TAG, "executing request " + httppost.getRequestLine());

        //Get the response
        HttpResponse response;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
            mError = e.getMessage();
            return null;
        }

        HttpEntity resEntity = response.getEntity();

        Log.w(TAG, response.getStatusLine().toString());
        String moodResult = null;
        try {
            if (resEntity != null) {
                String requestResult = EntityUtils.toString(resEntity);
                Log.w(TAG, requestResult);
                moodResult = decodeJson(requestResult);
            }

            if (resEntity != null) {
                resEntity.consumeContent();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            mError = e.getMessage();
        }

        httpclient.getConnectionManager().shutdown();
        return moodResult;
    }

    /**
     * Decode the JSON response
     *
     * @param result
     * @return
     * @throws Exception
     */
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

        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        showDialogMood(result);
    }

    private void showDialogMood(final String mood) {
        //Show result to the user in a dialog
        if (mood != null) {
            mAlertDialog.setTitle("Mood Detection");
            mAlertDialog.setMessage(mContext.getString(R.string.detection_result, mood));
            mAlertDialog.setCancelable(false);

            mAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mPlayerFragment.launchPlaylist(mood);
                        }
                    });

            mAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            mAlertDialog.setIcon(android.R.drawable.ic_dialog_info);

        } else {
            mAlertDialog.setTitle("Sorry");
            mAlertDialog.setMessage(mError + " !");

            mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Nothing
                        }
                    });

            mAlertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        }

        mAlertDialog.show();
    }
}
