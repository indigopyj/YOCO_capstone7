package com.example.carcarcarcar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


public class AddnewActivity extends AppCompatActivity {
    private static final String TAG = "MAIN";

    private TextView cameratextview,cartypetextview;
    private EditText carnumberEditText;
    private Button caridentifybtn;
    private Button camerabtn;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnew);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);


        caridentifybtn = findViewById(R.id.caridentifybutton); //caridentifybtn.setVisibility(View.VISIBLE);
        camerabtn = findViewById(R.id.camerabutton); camerabtn.setVisibility(View.INVISIBLE);
        //cameratextview = findViewById(R.id.textView7); cameratextview.setVisibility(View.INVISIBLE);
        carnumberEditText = findViewById(R.id.carnumber);
        cartypetextview=findViewById(R.id.cartypetextview);


        queue = Volley.newRequestQueue(this);

        caridentifybtn.setOnClickListener(new View.OnClickListener(){ //차량 정보 조회하기 누르면
            @Override
            public void onClick(View v) {

                String url = Config.getUrl("/findCar");
                JSONObject body = new JSONObject();
                try{
                    body.put("car_id",carnumberEditText.getText().toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final JsonObjectRequest jsonRequest1 = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Boolean result = response.getBoolean("result");

                            if (result) {

                                //차량 정보 받아옴
                                String cartype = response.getString("car_type");
                                cartypetextview.setTextColor(Color.BLUE);
                                cartypetextview.setText("차량 대여가 가능합니다.\n차량 종류 : "+cartype);

                                //차량 정보 확인 숨기고 카메라 버튼 visible
                                camerabtn.setVisibility(View.VISIBLE);
                                //cameratextview.setVisibility(View.VISIBLE);

                            }else{

                                if(Config.is_renting){
                                    cartypetextview.setTextColor(Color.RED);
                                    cartypetextview.setText("대여중인 차량이 존재합니다.");

                                    if(Config.upload_before){
                                        Intent intent=new Intent(AddnewActivity.this, HistoryActivity.class);
                                        startActivity(intent);

                                    } else if(Config.photos_before){
                                        Toast.makeText(getApplicationContext(),"차량 이용 전 사진을 전송하십시오",Toast.LENGTH_SHORT);
                                        Intent intent=new Intent(AddnewActivity.this, BeforePastHistory.class);
                                        startActivity(intent);

                                    }else{
                                        Toast.makeText(getApplicationContext(),"차량 이용 전 사진을 촬영하십시오",Toast.LENGTH_SHORT);
                                        Intent intent=new Intent(AddnewActivity.this,CameraActivity.class);
                                        intent.putExtra("state",0);
                                        startActivity(intent);
                                    }
                                }else{
                                    cartypetextview.setTextColor(Color.RED);
                                    cartypetextview.setText("선택하신 차량을 이용할 수 없습니다");
                                }
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
                jsonRequest1.setTag(TAG);
                queue.add(jsonRequest1);
            }
        });


        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userid = Config.user_id;
                String carid = carnumberEditText.getText().toString();
                JSONObject body=new JSONObject();
                try{
                    body.put("user_id",userid);
                    body.put("car_id",carid);
                }catch(JSONException e){
                    e.printStackTrace();
                }
                String url=Config.getUrl("/startRent");

                final JsonObjectRequest jsonRequest2=new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Boolean result = response.getBoolean("result");
                            if (result){
                                Intent intent = new Intent(AddnewActivity.this, CameraActivity.class);
                                intent.putExtra("state",0);
                                Config.car_id=carnumberEditText.getText().toString();
                                Config.rent_id=response.getString("rent_id");
                                Toast.makeText(getApplicationContext(),Config.printUserInfo(),Toast.LENGTH_LONG);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(), "차량을 대여할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) ;

                jsonRequest2.setTag(TAG);
                queue.add(jsonRequest2);


            }
        });


    }

}