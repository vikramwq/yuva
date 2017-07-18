package com.multitv.yuv.gmaillogin;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.View;

import com.multitv.yuv.R;
import com.multitv.yuv.utilities.Tracer;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


class GmailLoginAsyncTask extends AsyncTask<Void, Void, Object> {

    private Context mContext;
    private OnGmailLoginAsyncTaskListener mOnGmailLoginAsyncTaskListener;
    private String mScope;
    private String mEmail;
    private Dialog mDialog;
    private String mAccessToken, mRefreshToken;

    public GmailLoginAsyncTask(Context context, OnGmailLoginAsyncTaskListener onGmailLoginAsyncTaskListener, String email, String scope) {
        mContext = context;
        mOnGmailLoginAsyncTaskListener = onGmailLoginAsyncTaskListener;
        mScope = scope;
        mEmail = email;
        mDialog = new Dialog(mContext);
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.progress);
        try {
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            Tracer.error("MKR", "GmailLoginAsyncTask.GmailLoginAsyncTask(mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT))) " + e.getMessage());
        }
        try {
            int divierId = mDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = mDialog.findViewById(divierId);
            divider.setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {
            Tracer.error("MKR", "GmailLoginAsyncTask.GmailLoginAsyncTask(\"android:id/titleDivider\") " + e.getMessage());
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        try {
            mDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result != null && mOnGmailLoginAsyncTaskListener != null) {
            mOnGmailLoginAsyncTaskListener.onGmailLoginAsyncTaskListenerUserData(result);
        }
    }

    @Override
    protected Object doInBackground(Void... params) {
        try {
            return fetchInfoFromProfileServer();
        } catch (IOException e) {
            return e;
        } catch (JSONException e) {
            return e;
        }
    }

    /**
     * Get a authentication token if one is not available. If the error is not
     * recoverable then it displays the error message on parent activity right
     * away.
     */
    private Object fetchToken() throws IOException {
        try {
            String code = GoogleAuthUtil.getToken(mContext, mEmail, mScope);
            String URL = "https://www.googleapis.com/oauth2/v3/token";
            JSONObject jsonObject = requestServerPOST(URL, code);
            if (jsonObject.has("access_token")) {
                mAccessToken = jsonObject.getString("access_token");
            }
            if (jsonObject.has("refresh_token")) {
                mRefreshToken = jsonObject.getString("refresh_token");
            }
            GoogleAuthUtil.clearToken(mContext, code);
            return "";
        } catch (UserRecoverableAuthException e) {
            return e;
        } catch (GoogleAuthException e) {
            return e;
        } catch (Exception e) {
            return e;
        }
    }

    ;

    private Object fetchInfoFromProfileServer() throws IOException, JSONException {
        Object fetchToken = fetchToken();
        Tracer.error("MKR", "fetchInfoFromProfileServer: 1 " + fetchToken);
        if (!(fetchToken instanceof String)) {
            return fetchToken;
        }
        Tracer.error("MKR", "fetchInfoFromProfileServer: 2 " + fetchToken);
        URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + mAccessToken);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        int responseCode = httpURLConnection.getResponseCode();
        if (responseCode == 200) {
            InputStream inputStream = httpURLConnection.getInputStream();
            ProfilePojo profilePojo = getProfile(readResponse(inputStream), mAccessToken, mRefreshToken);
            return profilePojo;
        } else if (responseCode == 401) {
            try {
                GoogleAuthUtil.clearToken(mContext, mAccessToken);
                return new MKRException("Server auth error: " + readResponse(httpURLConnection.getErrorStream()));
            } catch (Exception e) {
                return new MKRException("Server auth error, please try again.");
            }
        } else {
            return new MKRException("Server returned the following error code: " + responseCode);
        }
    }

/*	private void userEditProfile(final String strURL,final String code){

		StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
				"https://www.googleapis.com/oauth2/v3/token", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Tracer.error("api_responce---", response.toString());
				try {

				*//*	JSONObject mObj = new JSONObject(response);
                    if (mObj.optInt("code") == 1) {
						MultitvCipher mcipher = new MultitvCipher();
						String str = new String(mcipher.decryptmyapi(mObj.optString("result")));
						Tracer.error("***user_data***", str);
						try {
							JSONObject newObj = new JSONObject(str);
							Tracer.error("***userResponces***",""+ newObj.toString());
						} catch (JSONException e) {
							Tracer.debug("***edit fail***", "JSONException");
							ExceptionUtils.printStacktrace(e);
						}
						}*//*

				} catch (Exception e) {
					ExceptionUtils.printStacktrace(e);
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Tracer.error("user_edit_fail" ,""+ error.getMessage());
				// customProgressDialog.dismiss(this);
				//mProgressbar_top.setVisibility(View.GONE);
			}
		}) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
                String data = "code=" + code.trim() + "&client_id=" + GmailLogin.SERVER_CLIENT_ID + "&client_secret=" + GmailLogin.SERVER_SECREAT + "&redirect_uri=urn:ietf:wg:oauth:2.0:oob&grant_type=authorization_code";
                params.put("", data);
				return params;
			}

		};


		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjReq);
	}*/


    /**
     * Method to request to server<br>
     * This method must called from back thread.
     *
     * @param strURL
     * @return
     */
    public static JSONObject requestServerPOST(String strURL, String code) {
        try {
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 100000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 100000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost("https://www.googleapis.com/oauth2/v3/token");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            String data = "code=" + code.trim() + "&client_id=" + GmailLogin.SERVER_CLIENT_ID + "&client_secret=" + GmailLogin.SERVER_SECREAT + "&redirect_uri=urn:ietf:wg:oauth:2.0:oob&grant_type=authorization_code";
            httpPost.setEntity(new StringEntity(data, "UTF8"));
            HttpResponse response = null;
            response = httpclient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            switch (statusLine.getStatusCode()) {
                case HttpStatus.SC_OK:
                case HttpStatus.SC_ACCEPTED:
                case HttpStatus.SC_CREATED:
                case HttpStatus.SC_NO_CONTENT:
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    String resp = out.toString();
                    out.close();
                    return new JSONObject(resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Tracer.error("MKR", "FETCH ACCESS TOKEN AND REFRESH TOKEN ERROR " + e.getMessage());
        }
        return new JSONObject();
    }

    /**
     * Reads the response from the input stream and returns it as a string.
     */
    private String readResponse(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len = 0;
        while ((len = is.read(data, 0, data.length)) >= 0) {
            bos.write(data, 0, len);
        }
        return new String(bos.toByteArray(), "UTF-8");
    }

    /**
     * Parses the response and returns the first name of the user.
     *
     * @throws JSONException if the response is not JSON or if first name does not exist
     *                       in response
     */
    private ProfilePojo getProfile(String jsonResponse, String accessToken, String refreshToken) throws JSONException {
        JSONObject profileData = new JSONObject(jsonResponse);
        ProfilePojo profilePojo = new ProfilePojo();
        if (profileData.has("picture")) {
            String userImageUrl = profileData.getString("picture");
            profilePojo.setPicUrl(userImageUrl);
        }
        if (profileData.has("name")) {
            String textName = profileData.getString("name");
            profilePojo.setName(textName);
        }
        if (profileData.has("gender")) {
            String textGender = profileData.getString("gender");
            profilePojo.setGender(textGender);
        }
        if (profileData.has("birthday")) {
            String textBirthday = profileData.getString("birthday");
            profilePojo.setBday(textBirthday);
        }
        profilePojo.setEmail(mEmail);
        profilePojo.setGmailToken(accessToken);
        profilePojo.setGmailRefreshToken(refreshToken);
        return profilePojo;
    }
}
