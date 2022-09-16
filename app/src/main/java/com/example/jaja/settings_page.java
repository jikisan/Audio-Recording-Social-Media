package com.example.jaja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class settings_page extends AppCompatActivity {

    private LinearLayout backBtn;
    private TextView tv_logout;
    private FloatingActionButton btn_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);

        setRef();
        clickListeners();
    }

    private void clickListeners() {
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(settings_page.this, homepage.class);
                startActivity(intent);
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), landing_page.class));
                finish();
            }
        });

    }

    private void setRef() {
        backBtn = findViewById(R.id.backBtn);
        tv_logout = findViewById(R.id.tv_logout);
        btn_home = findViewById(R.id.btn_home);
    }
}