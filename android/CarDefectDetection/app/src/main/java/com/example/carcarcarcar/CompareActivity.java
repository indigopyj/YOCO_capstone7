package com.example.carcarcarcar;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class CompareActivity extends AppCompatActivity {

    private static final String TAG = "MAIN";
    private RequestQueue queue;

    private String rentid;
    private Boolean result;

    private File mImageFolder;
    private Bitmap bt_ft, bt_ff, bt_rf, bt_rb, bt_bt, bt_bf, bt_lb, bt_lf;

    private String label, part;
    private Integer topx, topy, btmx, btmy;
    private JSONArray newDefectsJSONArray, defects;

    private Integer dent_count = 0, scratch_count = 0, glass_count = 0;

    private TextView ft_compare_yolo, ff_compare_yolo, rf_compare_yolo, rb_compare_yolo, bt_compare_yolo, bf_compare_yolo, lb_compare_yolo, lf_compare_yolo;

    int index;

    ArrayList<NewDefect> newDefectsList = new ArrayList<NewDefect>();
    ArrayList<Defect> defectList = new ArrayList<Defect>();

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
        setContentView(R.layout.activity_compare);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        ImageView imageView_ff = findViewById(R.id.ff_imageview_compare);
        ImageView imageView_ft = findViewById(R.id.ft_imageview_compare);
        ImageView imageView_bf = findViewById(R.id.bf_imageview_compare);
        ImageView imageView_bt = findViewById(R.id.bt_imageview_compare);
        ImageView imageView_lf = findViewById(R.id.lf_imageview_compare);
        ImageView imageView_lb = findViewById(R.id.lb_imageview_compare);
        ImageView imageView_rf = findViewById(R.id.rf_imageview_compare);
        ImageView imageView_rb = findViewById(R.id.rb_imageview_compare);

        ft_compare_yolo = findViewById(R.id.ft_textview_compare_yolo);
        ff_compare_yolo = findViewById(R.id.ff_textview_compare_yolo);
        rf_compare_yolo= findViewById(R.id.rf_textview_compare_yolo);
        rb_compare_yolo=findViewById(R.id.rb_textview_compare_yolo);
        bt_compare_yolo=findViewById(R.id.bt_textview_compare_yolo);
        bf_compare_yolo=findViewById(R.id.bf_textview_compare_yolo);
        lb_compare_yolo=findViewById(R.id.lb_textview_compare_yolo);
        lf_compare_yolo=findViewById(R.id.lf_textview_compare_yolo);

        final TextView ft_o = findViewById(R.id.ft_roundicon);
        final TextView ff_o = findViewById(R.id.ff_roundicon);
        final TextView rf_o = findViewById(R.id.ff_roundicon);
        final TextView rb_o = findViewById(R.id.ff_roundicon);
        final TextView bt_o = findViewById(R.id.ff_roundicon);
        final TextView bf_o = findViewById(R.id.ff_roundicon);
        final TextView lb_o = findViewById(R.id.ff_roundicon);
        final TextView lf_o = findViewById(R.id.ff_roundicon);

        ft_o.setVisibility(View.INVISIBLE);
        ff_o.setVisibility(View.INVISIBLE);
        rf_o.setVisibility(View.INVISIBLE);
        rb_o.setVisibility(View.INVISIBLE);
        bt_o.setVisibility(View.INVISIBLE);
        bf_o.setVisibility(View.INVISIBLE);
        lb_o.setVisibility(View.INVISIBLE);
        lf_o.setVisibility(View.INVISIBLE);



        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        mImageFolder = new File(imageFile, "YOCO");
        String newPath = (Paths.get(mImageFolder.getAbsolutePath())).toString() + "/";

        rentid = getIntent().getStringExtra("rent_id");
        //rentid = "14";

        Uri uri_ft = Uri.parse("file:///" + newPath + rentid + "_" + "ft_a.jpg");
        try {
            bt_ft = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri_ft).copy(Bitmap.Config.ARGB_8888, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView_ft.setImageBitmap(bt_ft);

        Uri uri_ff = Uri.parse("file:///" + newPath + rentid + "_" + "ff_a.jpg");
        try {
            bt_ff = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri_ff).copy(Bitmap.Config.ARGB_8888, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView_ff.setImageBitmap(bt_ff);

        Uri uri_rf = Uri.parse("file:///" + newPath + rentid + "_" + "rf_a.jpg");

        try {
            bt_rf = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri_rf).copy(Bitmap.Config.ARGB_8888, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView_rf.setImageBitmap(bt_rf);


        Uri uri_rb = Uri.parse("file:///" + newPath + rentid + "_" + "rb_a.jpg");
        try {
            bt_rb = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri_rb).copy(Bitmap.Config.ARGB_8888, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView_rb.setImageBitmap(bt_rb);


        Uri uri_bt = Uri.parse("file:///" + newPath + rentid + "_" + "bt_a.jpg");
        try {
            bt_bt = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri_bt).copy(Bitmap.Config.ARGB_8888, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView_bt.setImageBitmap(bt_bt);


        Uri uri_bf = Uri.parse("file:///" + newPath + rentid + "_" + "bf_a.jpg");
        try {
            bt_bf = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri_bf).copy(Bitmap.Config.ARGB_8888, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView_bf.setImageBitmap(bt_bf);


        Uri uri_lb = Uri.parse("file:///" + newPath + rentid + "_" + "lb_a.jpg");

        try {
            bt_lb = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri_lb).copy(Bitmap.Config.ARGB_8888, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView_lb.setImageBitmap(bt_lb);


        Uri uri_lf = Uri.parse("file:///" + newPath + rentid + "_" + "lf_a.jpg");

        try {
            bt_lf = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri_lf).copy(Bitmap.Config.ARGB_8888, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView_lf.setImageBitmap(bt_lf);


        queue = Volley.newRequestQueue(this);
        String url = Config.getUrl("/showResults/compare");
        JSONObject body = new JSONObject();

        try {
            body.put("rent_id", rentid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest jsonRequest1 = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    Boolean result = response.getBoolean("result");
                    if (result) {

                        newDefectsJSONArray = response.getJSONArray("new_defects");

                        for (int i = 0; i < 8; i++) {
                            ArrayList<Defect> defectList = new ArrayList<Defect>();
                            JSONObject newDefectObject = (JSONObject) newDefectsJSONArray.get(i);
                            part = newDefectObject.getString("part");
                            JSONArray defectsJSONArray = newDefectObject.getJSONArray("defects");

                            for (int j = 0; j < defectsJSONArray.length(); j++) {

                                JSONObject defectsJSONObject = defectsJSONArray.getJSONObject(j);

                                Defect defect = new Defect(defectsJSONObject.getString("label"),defectsJSONObject.getString("topx")
                                        ,defectsJSONObject.getString("topy"),defectsJSONObject.getString("btmx"),defectsJSONObject.getString("btmy"));

                                defectList.add(defect);

                            }

                            NewDefect newdefect = new NewDefect(part, defectList);

                            newDefectsList.add(newdefect);

                        }
                        //사진에 drawrect *8
                        for (int j = 0; j < 8; j++) {


                            //ft
                            if (newDefectsList.get(j).getPart().equals("ft")) {
                                Log.i("newdefectlistgetsize", String.valueOf(newDefectsList.get(j).getDefectArrayList().size()));
                                for (int k = 0; k < newDefectsList.get(j).getDefectArrayList().size(); k++) {
                                    Log.i("getlabel",newDefectsList.get(j).getDefectArrayList().get(k).getLabel());
                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("dent")) {
                                        dent_count++;

                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_ft, topx, topy, btmx, btmy,"dent", Color.MAGENTA);

                                        Log.i("dentxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("dentcount", String.valueOf(dent_count));
                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("glass")) {
                                        glass_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_ft, topx, topy, btmx, btmy,"glass",Color.GREEN);
                                        Log.i("glassxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("glasscount", String.valueOf(glass_count));
                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("scratch")) {
                                        scratch_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_ft, topx, topy, btmx, btmy,"scratch",Color.BLUE);
                                        Log.i("scratchxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("scratchcount", String.valueOf(scratch_count));
                                    }
                                }

                                if (dent_count == 0 && glass_count == 0 && scratch_count == 0) {
                                    ft_o.setVisibility(View.INVISIBLE);
                                    ft_compare_yolo.setText("새로 탐지된 결함이 없습니다.");
                                } else {
                                    ft_o.setVisibility(View.VISIBLE);
                                    ft_compare_yolo.setText("찌그러짐 : " + dent_count + "개, 스크래치 : " + scratch_count + "개, 유리 파손 : " + glass_count + "개가 새로 탐지되었습니다.");
                                    ft_compare_yolo.setTextColor(Color.RED);
                                    dent_count = 0;
                                    scratch_count = 0;
                                    glass_count = 0;
                                }
                            }




                            //ff
                            if (newDefectsList.get(j).getPart().equals("ff")) {
                                Log.i("newdefectlistgetsize", String.valueOf(newDefectsList.get(j).getDefectArrayList().size()));
                                for (int k = 0; k < newDefectsList.get(j).getDefectArrayList().size(); k++) {
                                    Log.i("getlabel",newDefectsList.get(j).getDefectArrayList().get(k).getLabel());
                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("dent")) {
                                        dent_count++;

                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_ff, topx, topy, btmx, btmy,"dent", Color.MAGENTA);

                                        Log.i("dentxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("dentcount", String.valueOf(dent_count));
                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("glass")) {
                                        glass_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_ff, topx, topy, btmx, btmy,"glass",Color.GREEN);
                                        Log.i("glassxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("glasscount", String.valueOf(glass_count));
                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("scratch")) {
                                        scratch_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_ff, topx, topy, btmx, btmy,"scratch",Color.BLUE);
                                        Log.i("scratchxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("scratchcount", String.valueOf(scratch_count));
                                    }
                                }

                                if (dent_count == 0 && glass_count == 0 && scratch_count == 0) {
                                    ff_o.setVisibility(View.INVISIBLE);
                                    ff_compare_yolo.setText("새로 탐지된 결함이 없습니다.");
                                } else {
                                    ff_o.setVisibility(View.VISIBLE);
                                    ff_compare_yolo.setText("찌그러짐 : " + dent_count + "개, 스크래치 : " + scratch_count + "개, 유리 파손 : " + glass_count + "개가 새로 탐지되었습니다.");
                                    ff_compare_yolo.setTextColor(Color.RED);
                                    dent_count = 0;
                                    scratch_count = 0;
                                    glass_count = 0;
                                }
                            }






                            //rf
                            if (newDefectsList.get(j).getPart().equals("rf")) {
                                Log.i("newdefectlistgetsize", String.valueOf(newDefectsList.get(j).getDefectArrayList().size()));
                                for (int k = 0; k < newDefectsList.get(j).getDefectArrayList().size(); k++) {
                                    Log.i("getlabel",newDefectsList.get(j).getDefectArrayList().get(k).getLabel());
                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("dent")) {
                                        dent_count++;

                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_rf, topx, topy, btmx, btmy,"dent", Color.MAGENTA);

                                        Log.i("dentxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("dentcount", String.valueOf(dent_count));
                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("glass")) {
                                        glass_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_rf, topx, topy, btmx, btmy,"glass",Color.GREEN);
                                        Log.i("glassxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("glasscount", String.valueOf(glass_count));
                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("scratch")) {
                                        scratch_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_rf, topx, topy, btmx, btmy,"scratch",Color.BLUE);
                                        Log.i("scratchxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("scratchcount", String.valueOf(scratch_count));
                                    }
                                }

                                if (dent_count == 0 && glass_count == 0 && scratch_count == 0) {
                                    rf_o.setVisibility(View.INVISIBLE);
                                    rf_compare_yolo.setText("새로 탐지된 결함이 없습니다.");
                                } else {
                                    rf_o.setVisibility(View.VISIBLE);
                                    rf_compare_yolo.setText("찌그러짐 : " + dent_count + "개, 스크래치 : " + scratch_count + "개, 유리 파손 : " + glass_count + "개가 새로 탐지되었습니다.");
                                    rf_compare_yolo.setTextColor(Color.RED);
                                    dent_count = 0;
                                    scratch_count = 0;
                                    glass_count = 0;
                                }
                            }




                            //rb
                            if (newDefectsList.get(j).getPart().equals("rb")) {
                                Log.i("newdefectlistgetsize", String.valueOf(newDefectsList.get(j).getDefectArrayList().size()));
                                for (int k = 0; k < newDefectsList.get(j).getDefectArrayList().size(); k++) {
                                    Log.i("getlabel",newDefectsList.get(j).getDefectArrayList().get(k).getLabel());
                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("dent")) {
                                        dent_count++;

                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_rb, topx, topy, btmx, btmy,"dent", Color.MAGENTA);

                                        Log.i("dentxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("dentcount", String.valueOf(dent_count));
                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("glass")) {
                                        glass_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_rb, topx, topy, btmx, btmy,"glass",Color.GREEN);
                                        Log.i("glassxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("glasscount", String.valueOf(glass_count));
                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("scratch")) {
                                        scratch_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_rb, topx, topy, btmx, btmy,"scratch",Color.BLUE);
                                        Log.i("scratchxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("scratchcount", String.valueOf(scratch_count));
                                    }
                                }

                                if (dent_count == 0 && glass_count == 0 && scratch_count == 0) {
                                    rb_o.setVisibility(View.INVISIBLE);
                                    rb_compare_yolo.setText("새로 탐지된 결함이 없습니다.");
                                } else {
                                    rb_o.setVisibility(View.VISIBLE);
                                    rb_compare_yolo.setText("찌그러짐 : " + dent_count + "개, 스크래치 : " + scratch_count + "개, 유리 파손 : " + glass_count + "개가 새로 탐지되었습니다.");
                                    rb_compare_yolo.setTextColor(Color.RED);
                                    dent_count = 0;
                                    scratch_count = 0;
                                    glass_count = 0;
                                }
                            }





                            //bt
                            if (newDefectsList.get(j).getPart().equals("bt")) {
                                Log.i("newdefectlistgetsize", String.valueOf(newDefectsList.get(j).getDefectArrayList().size()));
                                for (int k = 0; k < newDefectsList.get(j).getDefectArrayList().size(); k++) {
                                    Log.i("getlabel",newDefectsList.get(j).getDefectArrayList().get(k).getLabel());
                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("dent")) {
                                        dent_count++;

                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_bt, topx, topy, btmx, btmy,"dent", Color.MAGENTA);

                                        Log.i("dentxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("dentcount", String.valueOf(dent_count));
                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("glass")) {
                                        glass_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_bt, topx, topy, btmx, btmy,"glass",Color.GREEN);
                                        Log.i("glassxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("glasscount", String.valueOf(glass_count));
                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("scratch")) {
                                        scratch_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_bt, topx, topy, btmx, btmy,"scratch",Color.BLUE);
                                        Log.i("scratchxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("scratchcount", String.valueOf(scratch_count));
                                    }
                                }

                                if (dent_count == 0 && glass_count == 0 && scratch_count == 0) {
                                    bt_o.setVisibility(View.INVISIBLE);
                                    bt_compare_yolo.setText("새로 탐지된 결함이 없습니다.");
                                } else {
                                    bt_o.setVisibility(View.VISIBLE);
                                    bt_compare_yolo.setText("찌그러짐 : " + dent_count + "개, 스크래치 : " + scratch_count + "개, 유리 파손 : " + glass_count + "개가 새로 탐지되었습니다.");
                                    bt_compare_yolo.setTextColor(Color.RED);
                                    dent_count = 0;
                                    scratch_count = 0;
                                    glass_count = 0;
                                }
                            }





                            //bf
                            if (newDefectsList.get(j).getPart().equals("bf")) {
                                Log.i("newdefectlistgetsize", String.valueOf(newDefectsList.get(j).getDefectArrayList().size()));
                                for (int k = 0; k < newDefectsList.get(j).getDefectArrayList().size(); k++) {
                                    Log.i("getlabel",newDefectsList.get(j).getDefectArrayList().get(k).getLabel());
                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("dent")) {
                                        dent_count++;

                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_bf, topx, topy, btmx, btmy,"dent", Color.MAGENTA);

                                        Log.i("dentxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("dentcount", String.valueOf(dent_count));
                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("glass")) {
                                        glass_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_bf, topx, topy, btmx, btmy,"glass",Color.GREEN);
                                        Log.i("glassxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("glasscount", String.valueOf(glass_count));
                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("scratch")) {
                                        scratch_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_bf, topx, topy, btmx, btmy,"scratch",Color.BLUE);
                                        Log.i("scratchxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("scratchcount", String.valueOf(scratch_count));
                                    }
                                }

                                if (dent_count == 0 && glass_count == 0 && scratch_count == 0) {
                                    bf_o.setVisibility(View.INVISIBLE);
                                    bf_compare_yolo.setText("새로 탐지된 결함이 없습니다.");
                                } else {
                                    bf_o.setVisibility(View.VISIBLE);
                                    bf_compare_yolo.setText("찌그러짐 : " + dent_count + "개, 스크래치 : " + scratch_count + "개, 유리 파손 : " + glass_count + "개가 새로 탐지되었습니다.");
                                    bf_compare_yolo.setTextColor(Color.RED);
                                    dent_count = 0;
                                    scratch_count = 0;
                                    glass_count = 0;
                                }
                            }







                            //lb
                            if (newDefectsList.get(j).getPart().equals("lb")) {
                                Log.i("newdefectlistgetsize", String.valueOf(newDefectsList.get(j).getDefectArrayList().size()));
                                for (int k = 0; k < newDefectsList.get(j).getDefectArrayList().size(); k++) {
                                    Log.i("getlabel",newDefectsList.get(j).getDefectArrayList().get(k).getLabel());
                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("dent")) {
                                        dent_count++;

                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_lb, topx, topy, btmx, btmy,"dent", Color.MAGENTA);

                                        Log.i("dentxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("dentcount", String.valueOf(dent_count));
                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("glass")) {
                                        glass_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_lb, topx, topy, btmx, btmy,"glass",Color.GREEN);
                                        Log.i("glassxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("glasscount", String.valueOf(glass_count));
                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("scratch")) {
                                        scratch_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_lb, topx, topy, btmx, btmy,"scratch",Color.BLUE);
                                        Log.i("scratchxy",topx+"   "+topy+"   "+btmx+"   "+btmy);
                                        Log.i("scratchcount", String.valueOf(scratch_count));
                                    }
                                }

                                if (dent_count == 0 && glass_count == 0 && scratch_count == 0) {
                                    lb_o.setVisibility(View.INVISIBLE);
                                    lb_compare_yolo.setText("새로 탐지된 결함이 없습니다.");
                                } else {
                                    lb_o.setVisibility(View.VISIBLE);
                                    lb_compare_yolo.setText("찌그러짐 : " + dent_count + "개, 스크래치 : " + scratch_count + "개, 유리 파손 : " + glass_count + "개가 새로 탐지되었습니다.");
                                    lb_compare_yolo.setTextColor(Color.RED);
                                    dent_count = 0;
                                    scratch_count = 0;
                                    glass_count = 0;
                                }
                            }





                            //lf
                            if (newDefectsList.get(j).getPart().equals("lf")) {
                                Log.i("newdefectlistgetsize", String.valueOf(newDefectsList.get(j).getDefectArrayList().size()));
                                for (int k = 0; k < newDefectsList.get(j).getDefectArrayList().size(); k++) {
                                    Log.i("getlabel",newDefectsList.get(j).getDefectArrayList().get(k).getLabel());
                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("dent")) {
                                        dent_count++;

                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_lf, topx, topy, btmx, btmy,"dent", Color.MAGENTA);

                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("glass")) {
                                        glass_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_lf, topx, topy, btmx, btmy,"glass",Color.GREEN);

                                    }

                                    if (newDefectsList.get(j).getDefectArrayList().get(k).getLabel().equals("scratch")) {
                                        scratch_count++;
                                        topx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopx());
                                        topy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getTopy());
                                        btmx = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmx());
                                        btmy = parseInt(newDefectsList.get(j).getDefectArrayList().get(k).getBtmy());
                                        //draw rectangle on bitmap
                                        onDrawRectangle(bt_lf, topx, topy, btmx, btmy,"scratch",Color.BLUE);

                                    }
                                }

                                if (dent_count == 0 && glass_count == 0 && scratch_count == 0) {
                                    lf_o.setVisibility(View.INVISIBLE);
                                    lf_compare_yolo.setText("새로 탐지된 결함이 없습니다.");
                                } else {
                                    lf_o.setVisibility(View.VISIBLE);
                                    lf_compare_yolo.setText("찌그러짐 : " + dent_count + "개, 스크래치 : " + scratch_count + "개, 유리 파손 : " + glass_count + "개가 새로 탐지되었습니다.");
                                    lf_compare_yolo.setTextColor(Color.RED);
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

    }


    //상세 내역 확인 버튼 클릭하면 part별로 팝업 호출

    public void onftButtonClicked(View v) {
        Intent ffIntent = new Intent(this, ComparePopup.class);
        ffIntent.putExtra("rentid",rentid);
        ffIntent.putExtra("part","ft");
        ffIntent.putExtra("parttext","전면 상단");
        startActivity(ffIntent);
    }

    public void onffButtonClicked(View v) {
        Intent ffIntent = new Intent(this, ComparePopup.class);
        ffIntent.putExtra("rentid",rentid);
        ffIntent.putExtra("part","ff");
        ffIntent.putExtra("parttext","전면");
        startActivity(ffIntent);
    }

    public void onrfButtonClicked(View v) {
        Intent ffIntent = new Intent(this, ComparePopup.class);
        ffIntent.putExtra("rentid",rentid);
        ffIntent.putExtra("part","rf");
        ffIntent.putExtra("parttext","운전자석 앞면");
        startActivity(ffIntent);
    }

    public void onrbButtonClicked(View v) {
        Intent ffIntent = new Intent(this, ComparePopup.class);
        ffIntent.putExtra("rentid",rentid);
        ffIntent.putExtra("part","rb");
        ffIntent.putExtra("parttext","운전자석 뒷면");
        startActivity(ffIntent);
    }



    public void onbtButtonClicked(View v) {
        Intent ffIntent = new Intent(this, ComparePopup.class);
        ffIntent.putExtra("rentid",rentid);
        ffIntent.putExtra("part","bt");
        ffIntent.putExtra("parttext","보조석 뒷면");
        startActivity(ffIntent);
    }

    public void onbfButtonClicked(View v) {
        Intent ffIntent = new Intent(this, ComparePopup.class);
        ffIntent.putExtra("rentid",rentid);
        ffIntent.putExtra("part","bf");
        ffIntent.putExtra("parttext","보조석 앞면");
        startActivity(ffIntent);
    }

    public void onlbButtonClicked(View v) {
        Intent ffIntent = new Intent(this, ComparePopup.class);
        ffIntent.putExtra("rentid",rentid);
        ffIntent.putExtra("part","lb");
        ffIntent.putExtra("parttext","후면 상단");
        startActivity(ffIntent);
    }

    public void onlfButtonClicked(View v) {
        Intent ffIntent = new Intent(this, ComparePopup.class);
        ffIntent.putExtra("rentid",rentid);
        ffIntent.putExtra("part","lf");
        ffIntent.putExtra("parttext","후면");
        startActivity(ffIntent);
    }


}