package com.example.carcarcarcar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.net.Uri;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class BeforePastHistory extends AppCompatActivity {
    private File mImageFolder;
    private String carid,rentid,userid,rentdate;

    private Boolean result;

    private static final String TAG = "MAIN";

    private RequestQueue queue;


    String[] imagelist;

    TextView info;
    Button returnBtn;
    Button sendBtn;

    private ArrayList<Uri> arrayList;

    ApiService service;

    OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_past_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        info=findViewById(R.id.carnumtextview);
        returnBtn=findViewById(R.id.returnpicBtn);

        userid = Config.user_id;
        carid = Config.car_id;
        rentid = Config.rent_id;

        info.append("차량번호 : "+carid+"\n");
        if(carid.charAt(0)=='c') info.append("차량종류 : compact\n");
        if(carid.charAt(0)=='m') info.append("차량종류 : midsize\n");
        if(carid.charAt(0)=='f') info.append("차량종류 : fullsize\n");

        returnBtn.setEnabled(false);


        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        mImageFolder = new File(imageFile, "YOCO");
        sendBtn = findViewById(R.id.sendBtn);


        ImageView imageview_frontal1 = findViewById(R.id.ff_imageview_compare);
        ImageView imageview_frontal2 = findViewById(R.id.ft_imageview_compare);
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


            Uri uri1 = Uri.parse("file:///" + newPath + Config.rent_id +"_" + "ft_b.jpg");
            imageview_frontal1.setImageURI(uri1);
            arrayList.add(uri1);


            Uri uri2 = Uri.parse("file:///" + newPath + Config.rent_id +"_" + "ff_b.jpg");
            imageview_frontal2.setImageURI(uri2);
            arrayList.add(uri2);

            Uri uri3 = Uri.parse("file:///" + newPath + Config.rent_id +"_" + "rf_b.jpg");
            imageView_profile1.setImageURI(uri3);
            arrayList.add(uri3);

            Uri uri4 = Uri.parse("file:///" + newPath + Config.rent_id +"_" + "rb_b.jpg");
            imageView_profile2.setImageURI(uri4);
            arrayList.add(uri4);

            Uri uri5 = Uri.parse("file:///"+ newPath + Config.rent_id +"_" + "bt_b.jpg");
            imageView_profile3.setImageURI(uri5);
            arrayList.add(uri5);

            Uri uri6 = Uri.parse("file:///" + newPath + Config.rent_id +"_" + "bf_b.jpg");
            imageView_profile4.setImageURI(uri6);
            arrayList.add(uri6);

            Uri uri7 = Uri.parse("file:///" + newPath + Config.rent_id +"_" + "lb_b.jpg");
            imageView_back1.setImageURI(uri7);
            arrayList.add(uri7);

            Uri uri8 = Uri.parse("file:///" + newPath + Config.rent_id +"_" + "lf_b.jpg");
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



    public void onCameraButtonClicked(View v){  // 반납을 위한 사진찍기
        Intent intent2 = new Intent(this, CameraActivity.class);
        intent2.putExtra("state", 1);
        startActivity(intent2);

    }

    public void onSendButtonClicked(View v){    // 사진을 서버로 전송하는 버튼
        returnBtn.setEnabled(true);
        returnBtn.setBackgroundColor(getResources().getColor(R.color.newblue));
        sendBtn.setEnabled(false);
        sendBtn.setBackgroundColor(getResources().getColor(R.color.darkgrey));

        uploadImagesToServer();
        Toast.makeText(BeforePastHistory.this, "Send complete", Toast.LENGTH_SHORT);

    }


    private void uploadImagesToServer(){
        //Internet connection check
        if(((ConnectivityManager) Objects.requireNonNull(BeforePastHistory.this.getSystemService
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

            Call<ResponseBody> call = service.uploadMultiple(parts);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                    if(response.isSuccessful()){
                        Log.v("Upload", "success");
                        Toast.makeText(BeforePastHistory.this,
                                "Images successfully uploaded!", Toast.LENGTH_SHORT).show();

                        RequestBody rent_id = createPartFromString(rentid);
                        RequestBody state = createPartFromString("b");
                        RequestBody yolo_request = createPartFromString("true");
                        Call<ResponseBody> yolo_call = service.requestYOLO(rent_id, state, yolo_request);
                        Log.v("YOLO Request", "Request YOLO");

                        yolo_call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Log.v("YOLO server", "YOLO Complete");

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.v("YOLO Server", "YOLO failed");
                            }
                        });

                        sendBtn.setEnabled(false);
                        sendBtn.setBackgroundColor(getResources().getColor(R.color.darkgrey));
                    }
                    else {
                        sendBtn.setEnabled(true);
                        sendBtn.setBackgroundColor(getResources().getColor(R.color.newblue));
                        Snackbar.make(findViewById(android.R.id.content),
                                "Something went wrong.", Snackbar.LENGTH_LONG).show();
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