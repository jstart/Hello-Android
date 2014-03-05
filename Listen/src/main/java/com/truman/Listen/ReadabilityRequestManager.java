package com.truman.Listen;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ctruman on 3/4/14.
 */
public class ReadabilityRequestManager {
    RequestQueue queue;
    Activity activity;

    public ReadabilityRequestManager(RequestQueue queue, Activity activity){
        this.queue = queue;
        this.activity = activity;
    }

    public void loadURL(String url) throws JSONException {
        String urlString = "https://www.readability.com/api/content/v1/parser?" + "token=a14cf32527d3837c4385d8c39f080bc1927b58ee&url=" + url + "";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null,  new ResponseListener(), new ErrorListener());
        queue.add(request);
        queue.start();
    }

    private class ResponseListener implements Response.Listener<JSONObject>{
        @Override
        public void onResponse(JSONObject response) {
            Log.d("com.truman.Listen",response.toString());
        }

    }

    private class ErrorListener implements Response.ErrorListener{
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }
}
