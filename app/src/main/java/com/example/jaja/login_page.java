package com.example.jaja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login_page extends AppCompatActivity {

    private EditText et_username, et_password;
    private TextView tv_signUp;
    private Button btn_login;

    private FirebaseAuth fAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        
        setRef();
        clicklListener();
    }

    private void clicklListener() {

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(login_page.this, homepage.class);
//                startActivity(intent);

                String email = et_username.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                if (TextUtils.isEmpty(email))
                {
                    et_username.setError("Email is Required");
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    et_username.setError("Incorrect Email Format");
                }
                else if (TextUtils.isEmpty(password))
                {

                    et_password.setError("Password is Required");
                    return;
                }
                else
                {
                    fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(login_page.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent( login_page.this, homepage.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(login_page.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });

        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login_page.this, sign_up_page.class);
                startActivity(intent);
            }
        });

    }

    private void setRef() {

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        tv_signUp = findViewById(R.id.tv_signUp);
        btn_login = findViewById(R.id.btn_login);

        fAuth = FirebaseAuth.getInstance();
    }
}