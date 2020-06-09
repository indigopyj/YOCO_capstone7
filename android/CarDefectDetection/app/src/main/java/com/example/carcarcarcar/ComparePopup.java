package com.example.carcarcarcar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class ComparePopup extends AppCompatActivity {
    private static final String TAG = "MAIN";
    private RequestQueue queue;

    private String rentid,part,selectedpart,parttotext;
    private ImageView beforeimage, afterimage;
    private TextView parttxt,beforetxt,aftertxt;

    private Integer topx, topy, btmx, btmy;
    private File mImageFolder;
    private Integer dent_count = 0, scratch_count = 0, glass_count = 0;

    private Bitmap before_bt, after_bt;



    protected void onDrawRectangle(Bitmap bm, int x1, int y1, int x2, int y2, String part, Integer color) {
        Paint p = new Paint();
        p.setColor(color);
        p.setStyle(Paint.Style.FILL);

        p.setStrokeWidth(5);
        p.setTextSize(100);

        Canvas c = new Canvas(bm);

        c.drawText(part, x1-20, y1-20, p);

        p.setStrokeWidth(20);
        p.setStyle(Paint.Style.STROKE);
        c.drawRect(x1, y1, x2, y2, p);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_popup);
        //getSupportActionBar().hide();

        beforeimage = findViewById(R.id.before);
        afterimage = findViewById(R.id.after);
        parttxt = findViewById(R.id.partextView);
        beforetxt = findViewById(R.id.beforetextView);
        aftertxt = findViewById(R.id.aftertextView);

        Intent intent = getIntent();
        selectedpart= intent.getStringExtra("part");
        parttotext = intent.getStringExtra("parttext");
        rentid = intent.getStringExtra("rentid");
        //rentid=Config.rent_id;

        parttxt.setText(parttotext);

        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        mImageFolder = new File(imageFile, "YOCO");
        String newPath = (Paths.get(mImageFolder.getAbsolutePath())).toString() + "/";


        Uri uri_after = Uri.parse("file:///" + newPath + rentid + "_" + selectedpart +"_a.jpg");
        try {
            after_bt = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri_after).copy(Bitmap.Config.ARGB_8888, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        afterimage.setImageBitmap(after_bt);


        Uri uri_before = Uri.parse("file:///" + newPath + rentid + "_" + selectedpart +"_b.jpg");
        try {
            before_bt = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri_before).copy(Bitmap.Config.ARGB_8888, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        beforeimage.setImageBitmap(before_bt);


        queue = Volley.newRequestQueue(this);
        String url = Config.getUrl("/showResults/yolo");
        JSONObject body = new JSONObject();

        try {
            body.put("rent_id", rentid);
            body.put("before_after","b");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest jsonRequest1 = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    Boolean result = response.getBoolean("result");
                    Log.i("result", result.toString());
                    if (result) {

                        JSONArray newPredictionssJSONArray = response.getJSONArray("detections");
                        ArrayList<Predictions> newPredictionsList = new ArrayList<Predictions>();
                        Log.i("predictionsJSONARRAY",newPredictionssJSONArray.toString());

                        for (int i = 0; i < 8; i++) {
                            ArrayList<Defect> defectList = new ArrayList<Defect>();
                            JSONObject newDefectObject = (JSONObject) newPredictionssJSONArray.get(i);
                            part = newDefectObject.getString("part");
                            JSONArray defectsJSONArray = newDefectObject.getJSONArray("defects");

                            for (int j = 0; j < defectsJSONArray.length(); j++) {

                                JSONObject defectsJSONObject = defectsJSONArray.getJSONObject(j);

                                Defect defect = new Defect(defectsJSONObject.getString("label"),defectsJSONObject.getString("topx")
                                        ,defectsJSONObject.getString("topy"),defectsJSONObject.getString("btmx"),defectsJSONObject.getString("btmy"));

                                defectList.add(defect);

                            }

                            Predictions predictions = new Predictions(part, defectList);
                            newPredictionsList.add(predictions);

                        }
                        //사진에 drawrect *8
                        for (int j = 0; j < 8; j++) {


                            //before
                            if (newPredictionsList.get(j).getPart().equals(selectedpart)) {

                                for (int k = 0; k < newPredictionsList.get(j).getDefectArrayList().size(); k++) {
                                    if (newPredictionsList.get(j).getDefectArrayList().get(k).getLabel().equals("dent")) {
                                        dent_count++;
                                        topx = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(before_bt, topx, topy, btmx, btmy,"dent", Color.RED);

                                    }

                                    if (newPredictionsList.get(j).getDefectArrayList().get(k).getLabel().equals("glass")) {
                                        glass_count++;
                                        topx = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(before_bt, topx, topy, btmx, btmy,"glass",Color.GREEN);

                                    }

                                    if (newPredictionsList.get(j).getDefectArrayList().get(k).getLabel().equals("scratch")) {
                                        scratch_count++;
                                        topx = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(before_bt, topx, topy, btmx, btmy,"scratch",Color.BLUE);

                                    }
                                }

                                if (dent_count == 0 && glass_count == 0 && scratch_count == 0) {
                                    beforetxt.setText("탐지된 결함이 없습니다.");
                                } else {
                                    beforetxt.setText("찌그러짐 : " + dent_count + "개, 스크래치 : " + scratch_count + "개, 유리 파손 : " + glass_count + "개가 새로 탐지되었습니다.");
                                    dent_count = 0;
                                    scratch_count = 0;
                                    glass_count = 0;
                                }
                            }


                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        jsonRequest1.setTag(TAG);
        queue.add(jsonRequest1);

        dent_count = 0;
        glass_count=0;
        scratch_count=0;


//after
        try {
            body.put("rent_id", rentid);
            body.put("before_after","a");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest jsonRequest2 = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    Boolean result = response.getBoolean("result");
                    if (result) {
                        JSONArray newPredictionssJSONArray = response.getJSONArray("detections");
                        ArrayList<Predictions> newPredictionsList = new ArrayList<Predictions>();

                        for (int i = 0; i < 8; i++) {
                            ArrayList<Defect> defectList = new ArrayList<Defect>();
                            JSONObject newDefectObject = (JSONObject) newPredictionssJSONArray.get(i);
                            part = newDefectObject.getString("part");
                            JSONArray defectsJSONArray = newDefectObject.getJSONArray("defects");

                            for (int j = 0; j < defectsJSONArray.length(); j++) {

                                JSONObject defectsJSONObject = defectsJSONArray.getJSONObject(j);

                                Defect defect = new Defect(defectsJSONObject.getString("label"),defectsJSONObject.getString("topx")
                                        ,defectsJSONObject.getString("topy"),defectsJSONObject.getString("btmx"),defectsJSONObject.getString("btmy"));

                                defectList.add(defect);

                            }

                            Predictions predictions = new Predictions(part, defectList);
                            newPredictionsList.add(predictions);

                        }
                        //사진에 drawrect *8
                        for (int j = 0; j < 8; j++) {


                            //before
                            if (newPredictionsList.get(j).getPart().equals(selectedpart)) {

                                for (int k = 0; k < newPredictionsList.get(j).getDefectArrayList().size(); k++) {
                                    if (newPredictionsList.get(j).getDefectArrayList().get(k).getLabel().equals("dent")) {
                                        dent_count++;
                                        topx = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(after_bt, topx, topy, btmx, btmy,"dent", Color.RED);


                                    }

                                    if (newPredictionsList.get(j).getDefectArrayList().get(k).getLabel().equals("glass")) {
                                        glass_count++;
                                        topx = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(after_bt, topx, topy, btmx, btmy,"glass",Color.GREEN);

                                    }

                                    if (newPredictionsList.get(j).getDefectArrayList().get(k).getLabel().equals("scratch")) {
                                        scratch_count++;
                                        topx = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newPredictionsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(after_bt, topx, topy, btmx, btmy,"scratch",Color.BLUE);

                                    }
                                }

                                if (dent_count == 0 && glass_count == 0 && scratch_count == 0) {
                                    aftertxt.setText("탐지된 결함이 없습니다.");
                                } else {
                                    aftertxt.setText("찌그러짐 : " + dent_count + "개, 스크래치 : " + scratch_count + "개, 유리 파손 : " + glass_count + "개가 새로 탐지되었습니다.");
                                    dent_count = 0;
                                    scratch_count = 0;
                                    glass_count = 0;
                                }
                            }


                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        jsonRequest2.setTag(TAG);
        queue.add(jsonRequest2);


    }

    //확인 버튼 클릭릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
}