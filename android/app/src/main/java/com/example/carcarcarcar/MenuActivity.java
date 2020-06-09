package com.example.carcarcarcar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class MenuActivity extends AppCompatActivity {

    private Button prev, create;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        Intent getIntent = getIntent();
        Bundle bundle = getIntent().getExtras();
        userid = getIntent.getStringExtra("user_id");

        prev = findViewById(R.id.prevlistBtn);
        create = findViewById(R.id.addnewBtn);

        //조건에 따라 버튼 색 변경, 비활성화?
        //create.setBackgroundColor(ContextCompat.getColor(this, R.color.darkgrey));
        //prev.setBackgroundColor(ContextCompat.getColor(this, R.color.newblue));


    }
    public void onaddnewButtonClicked(View v) {
        Intent intent = new Intent(this, AddnewActivity.class);
        intent.putExtra("user_id",userid);
        startActivity(intent);
    }
    public void onprevlistButtonClicked(View v) {
        Intent intent = new Intent(this, HistoryActivity.class);
        intent.putExtra("user_id",userid);
        startActivity(intent);
    }
}
