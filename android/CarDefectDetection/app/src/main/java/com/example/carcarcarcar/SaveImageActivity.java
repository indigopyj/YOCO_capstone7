package com.example.carcarcarcar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaveImageActivity extends Activity {
    ImageView imageView;
    private String imagepath = "";
    Button backButton;
    Button nextButton;
    private File mImageFolder;
    private int index = 0;
    Bitmap bitmap;
    int done=0;

    String newPath = "";

//    private RequestQueue queue = Volley.newRequestQueue(this);
    private Boolean result;
    private String rentid,userid,carid;
    private int state;  // 0 : before, 1 : after


    BackgroundTask task;
    LoadingDialog loadingDialog;
    Intent cameraintent;


    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        Intent intent = getIntent();

        userid = Config.user_id;
        carid = Config.car_id;
        rentid = Config.rent_id;
        state = getIntent().getIntExtra("state", 0);


        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_saveimage);
        createImageFolder();
        imageView = (ImageView) findViewById(R.id.imageView);
        backButton = (Button) findViewById(R.id.backButton);
        nextButton = (Button) findViewById(R.id.nextButton);

        if (intent.getAction().equals("load temp image")) {

            imagepath = intent.getStringExtra("temppath");
            index = intent.getIntExtra("index", 0);
            bitmap = BitmapFactory.decodeFile(imagepath);
            imageView.setImageBitmap(bitmap);
        }



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                task = new BackgroundTask();
                task.execute(bitmap);
                int number = index + 1;
                Toast.makeText(getApplicationContext(), "사진이 저장되었습니다. (" + number +"/8)" , Toast.LENGTH_SHORT).show();

                index++; // count the number of pics. All pics should be 8 pictures.
                if (index == 8) {
                    if  (state == 0){
                        Config.photos_before = true;
                        cameraintent = new Intent(getApplicationContext(), BeforePastHistory.class);
                    }

                    else
                        cameraintent = new Intent(getApplicationContext(), AfterPastHistory.class);


                } else {

                    cameraintent = new Intent(getApplicationContext(), CameraActivity.class);
                    //cameraintent.putExtra("file path", f.getAbsolutePath());
                    cameraintent.putExtra("index", index);
                    cameraintent.putExtra("state", state);



                    startActivity(cameraintent);
                }


            }
        });



    }
    class BackgroundTask extends AsyncTask<Bitmap, Void, Void>{


        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            Intent cameraintent;
            String position;

            if(index == 0) position = "ft";
            else if(index == 1) position = "ff";
            else if(index == 2) position = "rf";
            else if(index == 3)position = "rb";
            else if(index == 4) position = "bt";
            else if(index == 5) position = "bf";
            else if(index == 6) position = "lb";
            else position = "lf";

            String car_state;
            if(state == 0)
                car_state = "b";
            else
                car_state = "a";

            String rentid=Config.rent_id;
            String newname = Config.rent_id + "_"+ position + "_" + car_state +  ".jpg";
            newPath = String.valueOf(Paths.get(mImageFolder.getAbsolutePath(),newname));
            final File newFile = new File(newPath);



            FileOutputStream fileOutputStream = null;

            try {//사진 저장

                fileOutputStream = new FileOutputStream(newFile);
                bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();


            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                Intent mediaStoreUpdateIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaStoreUpdateIntent.setData(Uri.fromFile(newFile));
                sendBroadcast(mediaStoreUpdateIntent);
                System.out.println("#################" + newPath);

                if (fileOutputStream != null) {

                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }


            return null;
        }
        // task초기화단계
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        // 해당 task에서 수행되던 작업 종료되었을때
        @Override
        protected void onPostExecute(Void aVoid) {

            if(index==8){
                startActivity(cameraintent);
            }

        }
        //UI 관련 작업
        @Override
        protected void onProgressUpdate(Void... values) {


        }


        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void createImageFolder() {
        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        mImageFolder = new File(imageFile, "YOCO");
        if (!mImageFolder.exists()) {
            mImageFolder.mkdirs();
        }


    }

    @Override
    public void onBackPressed() {
        Intent back_intent = new Intent(getApplicationContext(), CameraActivity.class);
        back_intent.putExtra("back", true);
        startActivity(back_intent);
    }


}

