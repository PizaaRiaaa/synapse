package com.example.synapse.screen.util.notifications;

import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.synapse.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender  {

    String userFcmToken;
    String title;
    String body;
    //Activity activity;
    String tag;
    String key;
    Context context;

    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey ="AAAAJkoER6Y:APA91bFpC1ZKGAuVwUfHZx6YPVpGxsq1IRxymDWbM3ZtaY4_wJAvORhcoO9uadjlOWtNBk0tapXPFljd_60wkq1pmtFfFFpJI-OJeBfXjl557zYXBAk1zuD1BgoBMVo-lqpBgVZYYUJF";

    public FcmNotificationsSender(String userFcmToken,String title, String body, String tag, Context context) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.tag = tag;
        this.key = key;
        this.context = context;
    }

    public void SendNotifications() {

        requestQueue = Volley.newRequestQueue(context);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", body);
            notiObject.put("tag", tag);
            notiObject.put("icon", R.drawable.ic_clock_notif); // enter icon that exists in drawable only

            mainObj.put("notification", notiObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // code run is got response
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // code run is got error
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
