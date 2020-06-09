package com.example.carcarcarcar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.Response;

import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.Interceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class AfterPastHistory extends AppCompatActivity {

    private static final String TAG = "MAIN";
    private RequestQueue queue;
    private File mImageFolder;

    private String rentid, userid, carid;

    String[] imagelist;
    private ArrayList<Uri> arrayList;
    OkHttpClient okHttpClient;


    private int state = 1;  // 0 : before  1 : after

    private Button compareBtn;
    private Button sendBtn;
    private TextView info;

    private boolean yolo_done = false;

    //private Intent serviceIntent_compare;

    ApiService service;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_past_history);

        rentid=Config.rent_id;
        userid = Config.user_id;
        carid = Config.car_id;

        compareBtn = findViewById(R.id.compareBtn);
        sendBtn = findViewById(R.id.sendBtn);
        info=findViewById(R.id.carnumtextview);

        compareBtn.setEnabled(false);
        compareBtn.setBackgroundColor(getResources().getColor(R.color.darkgrey));
        info.append("차량번호 : "+carid+"\n");
        if(carid.charAt(0)=='c') info.append("차량종류 : compact\n");
        if(carid.charAt(0)=='m') info.append("차량종류 : midsize\n");
        if(carid.charAt(0)=='f') info.append("차량종류 : fullsize\n");

        queue = Volley.newRequestQueue(this);


        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        mImageFolder = new File(imageFile, "YOCO");


        ImageView imageView_frontal1 = findViewById(R.id.ff_imageview_compare);
        ImageView imageView_frontal2 = findViewById(R.id.ft_imageview_compare);
        ImageView imageView_profile1 = findViewById(R.id.bf_imageview_compare);
        ImageView imageView_profile2 = findViewById(R.id.bt_imageview_compare);
        ImageView imageView_profile3 = findViewById(R.id.lf_imageview_compare);
        ImageView imageView_profile4 = findViewById(R.id.lb_imageview_compare);
        ImageView imageView_back1 = findViewById(R.id.rf_imageview_compare);
        ImageView imageView_back2 = findViewById(R.id.rb_imageview_compare);

        imagelist = new String[8];
        //이미지 넣기

        try { //uri 경로의 이미지 파일을 로드

            arrayList = new ArrayList<>();


            String newPath = String.valueOf(Paths.get(mImageFolder.getAbsolutePath())) + "/";


            Uri uri1 = Uri.parse("file:///" + newPath + Config.rent_id +"_" + "ft_a.jpg");
            System.out.println(uri1);
            imageView_frontal1.setImageURI(uri1);
            arrayList.add(uri1);

            Uri uri2 = Uri.parse("file:///" + newPath + Config.rent_id +"_" + "ff_a.jpg");
            imageView_frontal2.setImageURI(uri2);
            arrayList.add(uri2);

            Uri uri3 = Uri.parse("file:///" + newPath + Config.rent_id +"_" + "rf_a.jpg");
            imageView_profile1.setImageURI(uri3);
            arrayList.add(uri3);

            Uri uri4 = Uri.parse("file:///" + newPath + Config.rent_id +"_" + "rb_a.jpg");
            imageView_profile2.setImageURI(uri4);
            arrayList.add(uri4);

            Uri uri5 = Uri.parse("file:///"+ newPath + Config.rent_id +"_" + "bt_a.jpg");
            imageView_profile3.setImageURI(uri5);
            arrayList.add(uri5);

            Uri uri6 = Uri.parse("file:///" + newPath + Config.rent_id +"_" + "bf_a.jpg");
            imageView_profile4.setImageURI(uri6);
            arrayList.add(uri6);

            Uri uri7 = Uri.parse("file:///" + newPath + Config.rent_id +"_" + "lb_a.jpg");
            imageView_back1.setImageURI(uri7);
            arrayList.add(uri7);

            Uri uri8 = Uri.parse("file:///" + newPath + Config.rent_id +"_" + "lf_a.jpg");
            imageView_back2.setImageURI(uri8);
            arrayList.add(uri8);

        }catch (Exception e){
            e.printStackTrace();
        }



        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();





    }

    @Override
    protected void onNewIntent(Intent intent) {


        Log.d("intent","onNewIntent");
        setIntent(intent);
        sendBtn.setEnabled(false);
        sendBtn.setBackgroundColor(getResources().getColor(R.color.darkgrey));

        compareBtn.setEnabled(true);
        compareBtn.setBackgroundColor(getResources().getColor(R.color.newblue));
        super.onNewIntent(intent);

    }

    public void onSendButtonClicked(View v){
        sendBtn.setEnabled(false);
        sendBtn.setBackgroundColor(getResources().getColor(R.color.darkgrey));
        Intent serviceIntent_yolo = new Intent(this, YOLOService.class);
        serviceIntent_yolo.putExtra("YOLO_done", false); // send false
        startService(serviceIntent_yolo);   // 사용자에게 "YOLO 분석중"을 알림
        uploadImagesToServer(); // send photos


    }



    public void onCompareButtonClicked(View v) throws JSONException {

        JSONObject body=new JSONObject();
        body.put("rent_id",rentid);
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Config.getUrl("/python/compare"), body, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Boolean result=response.getBoolean("result");
                    if(result){
                        Intent intent2 = new Intent(AfterPastHistory.this, CompareActivity.class);
                        intent2.putExtra("rent_id", rentid);
                        Config.initUserInfo();
                        stopService(new Intent(AfterPastHistory.this, YOLOService.class));
                        startActivity(intent2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);

    }

    private void uploadImagesToServer(){
        //Internet connection check
        if(((ConnectivityManager) Objects.requireNonNull(AfterPastHistory.this.getSystemService
                (Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo() != null){



            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            List<MultipartBody.Part> parts = new ArrayList<>();

            service = retrofit.create(ApiService.class);

            if(arrayList != null){
                // create part for file
                for(int i=0; i < arrayList.size(); i++){
                    System.out.println(arrayList.get(i));
                    parts.add(prepareFilePart("image" + i, arrayList.get(i)));
                }
            }


            // execute the request 사진전송
            Call<ResponseBody> call = service.uploadMultiple(parts);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        Log.v("Upload", "success");
                        Toast.makeText(AfterPastHistory.this,
                                "Images successfully uploaded!", Toast.LENGTH_SHORT).show();

                        // create a map of data to pass along
                        RequestBody rent_id = createPartFromString(rentid);
                        RequestBody state = createPartFromString("a");
                        RequestBody yolo_request = createPartFromString("true");






                        JSONObject body=new JSONObject();
                        try {
                            body.put("rent_id",rentid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest request2=new JsonObjectRequest(Request.Method.POST, Config.getUrl("/completeReturn"), body, new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Boolean result=response.getBoolean("result");
                                    if(result){
                                        Toast.makeText(getApplicationContext(),"차량" +Config.car_id+" 반납되었습니다",Toast.LENGTH_SHORT);



                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.v("Volley", "VolleyError : " + error.toString());

                            }
                        });
                        request2.setRetryPolicy(
                                new DefaultRetryPolicy(
                                        500000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                )
                        );
                        queue.add(request2);


                        //  YOLO 요청 전송
                        Call<ResponseBody> yolo_call = service.requestYOLO(rent_id, state, yolo_request);
                        Log.v("YOLO Request", "Request YOLO");

                        Handler delayHandler = new Handler();
                        delayHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // TODO

                                stopService(new Intent(AfterPastHistory.this, YOLOService.class));
                                Intent serviceIntent_compare = new Intent(AfterPastHistory.this, YOLOService.class);
                                serviceIntent_compare.putExtra("YOLO_done", true); // send true
                                startService(serviceIntent_compare);    // 사용자에게 YOLO가 완료됐음을 알림



                                compareBtn.setEnabled(true);
                                compareBtn.setBackgroundColor(getResources().getColor(R.color.newblue));
                            }
                        }, 240000);



                        yolo_call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                                System.out.println("########response : " + response);
                                if(response.isSuccessful()){
                                    Log.v("YOLO server", "YOLO Complete");
                                    // background service 전송

                                }
                                else {
//                                    Snackbar.make(findViewById(android.R.id.content),
//                                            "Something went wrong with YOLO.", Snackbar.LENGTH_LONG).show();
                                    sendBtn.setEnabled(true);
                                    sendBtn.setBackgroundColor(getResources().getColor(R.color.newblue));
                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.e(TAG, t.toString());
                                if (t instanceof IOException) {
//                                    Toast.makeText(AfterPastHistory.this, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                                    // logging probably not necessary
                                }
                                else {
//                                    Toast.makeText(AfterPastHistory.this, "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
                                    // todo log to some central bug tracking service
                                }
                                Log.v("YOLO Server", "YOLO failed");
//                                Toast.makeText(AfterPastHistory.this,
//                                        "사진을 다시 전송해주세요", Toast.LENGTH_SHORT).show();
//                                sendBtn.setEnabled(true);
//                                sendBtn.setBackgroundColor(getResources().getColor(R.color.newblue));


                            }
                        });

                    }
                    else {
                        sendBtn.setEnabled(true);
                        sendBtn.setBackgroundColor(getResources().getColor(R.color.newblue));
                        Snackbar.make(findViewById(android.R.id.content),
                                "Something went wrong with Sending.", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "Image upload failed!", t);
                    Snackbar.make(findViewById(android.R.id.content),
                            "Image upload failed!", Snackbar.LENGTH_LONG).show();

                }
            });

        }
    }

    @NonNull
    private RequestBody createPartFromString(String string) {
        return RequestBody.create(MultipartBody.FORM, string);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_IMAGE), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }




}