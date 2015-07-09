package com.mototime.motobat.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mototime.motobat.MyApp;
import com.mototime.motobat.utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by pavel on 09.07.15.
 */
public abstract class HTTPClient extends AsyncTask<Map<String, String>, Integer, JSONObject> {
    private final static String CHARSET   = "UTF-8";
    private final static String USERAGENT = "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36";
    private ProgressDialog dialog;
    Context                   context;
    MyApp                     myApp;
    AsyncTaskCompleteListener listener;
    Map<String, String>       post;

    @SafeVarargs
    @Override
    protected final JSONObject doInBackground(Map<String, String>... params) {
        return request(params[0]);
    }

    JSONObject request(Map<String, String> post) {
        if (!MyUtils.isOnline(context)) {
            try {
                JSONObject result = new JSONObject();
                result.put("error", "Интернет не доступен");
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
                return new JSONObject();
            }
        }
        URL url;
        String app, method;
        myApp = (MyApp) context.getApplicationContext();
        try {
            if (post.containsKey("app")) {
                app = post.get("app");
                post.remove("app");
            } else {
                app = myApp.getProps().get("default.app");
            }
            if (post.containsKey("method")) {
                method = post.get("method");
                post.remove("method");
            } else {
                method = "default";
            }
            url = createUrl(app, method, false);
            if (post.containsKey("hint")) {
                final String hint = post.get("hint");
                post.remove("hint");
                Runnable execute = new Runnable() {
                    @Override
                    public void run() {
                        dialog = new ProgressDialog(context);
                        dialog.setMessage("Обмен данными...\n" + hint);
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(true);
                        dialog.show();
                    }
                };
                ((Activity) context).runOnUiThread(execute);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();
        CustomTrustManager.allowAllSSL();
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Charset", CHARSET);
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);
            connection.setRequestProperty("Content-Language", "ru-RU");
            connection.setRequestProperty("User-Agent", USERAGENT);
            connection.setUseCaches(false);

            if (!post.isEmpty()) {
                String POST = makePOST(post);
                Log.d("POST", url.toString() + "?" + POST);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Length", Integer.toString((POST).getBytes().length));
                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                os.writeBytes(POST);
            }
            InputStream is;
            try {
                is = connection.getInputStream();
                if (connection.getContentEncoding() != null) {
                    is = new GZIPInputStream(is);
                }
                int responseCode = connection.getResponseCode();
                Log.d("JSON ERROR", String.valueOf(responseCode));
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(is));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                } else {
                    Log.d("JSON ERROR", String.valueOf(responseCode));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        JSONObject reader;
        try {
            reader = new JSONObject(response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                reader = new JSONObject(response.toString().replace("\\", "").replace("\"", ""));
            } catch (JSONException e1) {
                e1.printStackTrace();
                String fakeAnswer = "{ error : unknown }";
                try {
                    reader = new JSONObject(fakeAnswer);
                } catch (JSONException e2) {
                    //Абсолютно маловероятно
                    e2.printStackTrace();
                    reader = new JSONObject();
                }
            }
        }
        Log.d("JSON RESPONSE", reader.toString());
        return reader;
    }

    private String makePOST(Map<String, String> post) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (final String key : post.keySet())
            try {
                if (first)
                    first = false;
                else
                    result.append("&");
                if (post.get(key) == null) {
                    //TODO Caused by: java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
                    return "ERROR";
                }
                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(post.get(key), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        // Log.d("JSON POST", result.toString());
        return result.toString();
    }

    private URL createUrl(String app, String method, Boolean https) {
        String protocol;
        if (https) {
            protocol = "https";
        } else {
            protocol = "http";
        }
        String script;
        String defaultMethod = myApp.getProps().get("app." + app + ".json.method.default");
        String server = myApp.getProps().get("app." + app + ".json.server");
        if (myApp.getProps().containsKey("app." + app + ".json.method." + method)) {
            script = myApp.getProps().get("app." + app + ".json.method." + method);
        } else {
            script = defaultMethod;
        }
        try {
            return new URL(protocol + "://" + server + "/" + script);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void dismiss() {
        try {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            dialog = null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (error(result) && !result.has("error")) {
            String error = getError(result);
            result = new JSONObject();
            try {
                result.put("error", error);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (listener != null) {
            try {
                listener.onTaskComplete(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        dismiss();
    }

    protected abstract boolean error(JSONObject response);

    protected abstract String getError(JSONObject response);
}
