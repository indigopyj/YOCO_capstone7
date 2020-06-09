package com.example.carcarcarcar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Config {

    public static String user_id;
    public static String rent_id="19";
    public static String car_id;
    public static Boolean is_renting;
    public static Boolean photos_before = false; // before photos taken
    public static Boolean upload_before; // before photos uploaded
    public static String url="http://yoco.ap.ngrok.io";

    public static String getUrl(String path){
        return url+path;
    }

    public static void initUserInfo(){ // 차량 반남시 사용
        Config.rent_id=null;
        Config.car_id=null;
        Config.is_renting=false;
        Config.photos_before=false;
        Config.upload_before=false;
    }

    public static void updateUserInfo(RequestQueue queue){
        JSONObject body = new JSONObject();
        try{
            body.put("user_id",Config.user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest jsonRequest2 = new JsonObjectRequest(Request.Method.POST, Config.getUrl("/checkUserInfo"), body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    Boolean result = response.getBoolean("result");

                    if (result) {

                        JSONObject user_info=response.getJSONObject("user_info");

                        Config.user_id=user_info.getString("user_id");
                        Config.rent_id=user_info.getString("current_rent_id");
                        Config.car_id=user_info.getString("current_car_id");
                        Config.is_renting=user_info.getBoolean("renting");
                        Config.upload_before=user_info.getJSONObject("photos_state").getBoolean("before");

                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) ;

        queue.add(jsonRequest2);
    }

    public static String printUserInfo(){
        StringBuilder sb=new StringBuilder();
        sb.append("user_id:"+Config.user_id+"\n");
        sb.append("rent_id:"+Config.rent_id+"\n");
        sb.append("car_id:"+Config.car_id+"\n");
        sb.append("renting:"+Config.is_renting.toString()+"\n");
        sb.append("photos.taken:"+Config.photos_before.toString()+"\n");
        sb.append("photos_uploaded:"+Config.upload_before.toString());

        return sb.toString();
    }

}