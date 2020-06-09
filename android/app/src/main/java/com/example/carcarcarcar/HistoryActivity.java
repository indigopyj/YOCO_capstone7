package com.example.carcarcarcar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "MAIN";
    private int cnt = 0;

    private String url;
    private RequestQueue queue;
    private Boolean result;

    RecyclerView recyclerView;
    PersonAdapter adapter;

    private Button returnbutton;

    private String currentrentId, userId, currentcarId, currentrentDate, selectedrentId;
    private JSONArray historyJSONArray, newDefects;

    private TextView currentinfo;

    ArrayList<History> historyList = new ArrayList<History>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager1);
        recyclerView.addItemDecoration( new DividerItemDecoration(getApplicationContext(),new LinearLayoutManager(this).getOrientation()));
        adapter = new PersonAdapter();

        returnbutton = findViewById(R.id.returnBtn);
        returnbutton.setVisibility(View.INVISIBLE);
        currentinfo = findViewById(R.id.currentcarinfotextview);
        userId=Config.user_id;

        queue = Volley.newRequestQueue(this);
        String url = Config.getUrl("/viewHistory");
        JSONObject body = new JSONObject();

        try {
            body.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest jsonRequest1 = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    Boolean result = response.getBoolean("result");
                    if (result) {
                        historyJSONArray = response.getJSONArray("history");
                        //historyList : 사용자에게 보여지는 history 내역 arraylist (jsonarray파싱)

                        for (int i = 0; i < historyJSONArray.length(); i++) {

                            JSONObject historyObject = (JSONObject) historyJSONArray.get(i);

                            Log.i("history object", historyObject.toString());

                            History history = new History(historyObject.getString("car_id"),historyObject.getString("rent_id"),
                                    historyObject.getString("rent_date"),historyObject.getString("return_date"),historyObject.getBoolean("returned"));
                            //history.setCar_id(historyObject.getString("car_id"));

                            Log.i("history object string carid ", history.getCar_id());

                            //history.setRent_date((String) historyObject.get("rent_date"));

                            Log.i("history object setrentdate", history.getRent_date() + "");

                            //history.setRent_id(historyObject.getString("rent_id"));
                            //history.setReturn_date(historyObject.getString("return_date"));
                            //history.setReturned(historyObject.getBoolean("returned"));

                            historyList.add(history);
                            Log.i("history object add", history.toString());
                        }

                        Log.i("historylist", historyList.toString());

                        //현재 이용중인 내역과 완료된 내역 구분
                        //returned 값 false인거(반납x) 찾아서 current_에 저장하고 historyList에서 삭제
                        for (int i = 0; i < historyList.size(); i++) {
                            if (historyList.get(i).getReturned() == false) {
                                returnbutton.setVisibility(View.VISIBLE);
                                currentrentId = historyList.get(i).getRent_id();
                                currentcarId = historyList.get(i).getCar_id();
                                currentrentDate = historyList.get(i).getRent_date();
                                historyList.remove(i);
                                Log.i("currentinfo", historyList.toString());
                                //현재 이용중인 내역 텍스트 표시
                                currentinfo.setText("대여 시작 날짜 : " + currentrentDate + "\n" +
                                        "대여 차량 번호 : " + currentcarId);
                                cnt = 0;
                            } else cnt++;
                        }
                        Log.i("historylistsize",historyList.toString()+"");
                        for(int i = 0; i<historyList.size();i++){
                            adapter.addItem(new History(historyList.get(i).getCar_id(),historyList.get(i).getRent_id(),historyList.get(i).getRent_date().toString(),historyList.get(i).getReturn_date().toString(),historyList.get(i).getReturned()));
                            Log.i("addItemToadapter",i+" done");
                        }
                        recyclerView.setAdapter(adapter);
                        if (cnt == historyList.size()) {
                            currentinfo.setText("대여중인 차량이 존재하지 않습니다.");
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

/*
        for(int i = 0; i<historyList.size();i++){
            adapter.addItem(new History(historyList.get(i).getCar_id(),historyList.get(i).getRent_id(),historyList.get(i).getRent_date().toString(),historyList.get(i).getReturn_date().toString(),historyList.get(i).getReturned()));
            Log.i("addItemToadapter",i+" done");
        }
*/


        adapter.setOnItemClicklistener(new OnPersonItemClickListener() {
            @Override
            public void onItemClick(PersonAdapter.ViewHolder holder, View view, int position) {

                History item = adapter.getItem(position);

                Intent intent = new Intent(HistoryActivity.this, CompareActivity.class);
                intent.putExtra("rent_id",item.getRent_id());
                 startActivity(intent);

                //Toast.makeText(getApplicationContext(), "차량 선택 " + item.getCar_id(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onCameraButtonClicked(View v) {
        Intent intent = new Intent(HistoryActivity.this, CameraActivity.class);
        intent.putExtra("rent_id", currentrentId);
        intent.putExtra("user_id", userId);
        intent.putExtra("car_id", currentcarId);
        intent.putExtra("state", 1);
        startActivity(intent);
    }


}
