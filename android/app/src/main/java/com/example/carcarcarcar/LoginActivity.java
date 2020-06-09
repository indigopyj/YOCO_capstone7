package com.example.carcarcarcar;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MAIN";

    private RequestQueue queue;
    private Boolean result;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginBtn);
        textView=findViewById(R.id.textView);
        loginButton = findViewById(R.id.loginBtn);


        queue = Volley.newRequestQueue(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = Config.getUrl("/login");
                JSONObject body = new JSONObject();

                try {
                    body.put("user_id", usernameEditText.getText().toString());
                    body.put("user_password", passwordEditText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            result = response.getBoolean("result");

                            if (result) {
                                textView.setText("로그인 성공");
                                Toast.makeText(getApplicationContext(), usernameEditText.getText().toString()+"님 환영합니다", Toast.LENGTH_SHORT).show();
                                Config.user_id=usernameEditText.getText().toString();

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
                                                JSONObject photos_state=user_info.getJSONObject("photos_state");

                                                Config.rent_id=user_info.getString("current_rent_id");
                                                Config.car_id=user_info.getString("current_car_id");
                                                Config.is_renting=user_info.getBoolean("renting");
                                                Config.upload_before=photos_state.getBoolean("before");

                                                //textView.setText(Config.printUserInfo());
                                                //textView.setText(Config.rent_id);

                                                if(Config.is_renting){ // 차를 대여중일 경우 history activity로

                                                    if(Config.upload_before){
                                                        Intent intent = new Intent(LoginActivity.this, HistoryActivity.class);
                                                        startActivity(intent);
                                                    }else if(Config.photos_before){
                                                        Intent intent = new Intent(LoginActivity.this, BeforePastHistory.class);
                                                        startActivity(intent);
                                                    }else{
                                                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                                        intent.putExtra("state",0);
                                                        startActivity(intent);
                                                    }
                                                } else{ // 차를 대여중이지 않을 경우 메뉴화면으로
                                                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                                    startActivity(intent);
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

                                queue.add(jsonRequest2);


                            } else {
                                textView.setText("로그인 실패, 아이디와 비밀번호를 다시 입력하세요");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("RVA", "error:" + error);

                        if (error instanceof TimeoutError) {
                            Log.e(TAG,"Update Location Request timed out.");
                        }else if (error instanceof NoConnectionError){
                            Log.e(TAG,"Update Lo    cation no connection.");
                        } else if (error instanceof AuthFailureError) {
                            Log.e(TAG,"Auth failure");
                        } else if (error instanceof ServerError) {
                            Log.e(TAG,"Server Error");
                        } else if (error instanceof NetworkError) {
                            Log.e(TAG,"Network Error");
                        } else if (error instanceof ParseError) {
                            Log.e(TAG,"Parse Error");
                        }
                    }
                });

                jsonRequest.setRetryPolicy(new RetryPolicy() {
                                               @Override
                                               public int getCurrentTimeout() {
                                                   return 50000;
                                               }

                                               @Override
                                               public int getCurrentRetryCount() {
                                                   return 50000;
                                               }

                                               @Override
                                               public void retry(VolleyError error) throws VolleyError {
                                               }
                                           }
                );

                jsonRequest.setTag(TAG);
                queue.add(jsonRequest);
            }
        });


        //퍼미션
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {

            }
        }



    }


    //이 아래로는 모두 퍼미션 관련 코드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.INTERNET };

    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED) {
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }

    //퍼미션
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PERMISSIONS_REQUEST_CODE:

                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;
                    boolean diskPermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted || !diskPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 접근권한을 허용하셔야합니다.");
                    else {
                        Intent mainIntent = new Intent(this, LoginActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                }
                break;
        }
    }

    //퍼미션
    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }


}