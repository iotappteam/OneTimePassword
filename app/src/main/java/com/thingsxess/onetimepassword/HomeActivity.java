package com.thingsxess.onetimepassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
String userId;
TextView idTx;
ImageView img;
Button singOut;
FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent j = getIntent();

        idTx = findViewById(R.id.tx_user_id);
        img = findViewById(R.id.iv_img);
        singOut = findViewById(R.id.btn_logout);

        String loginMethod = j.getStringExtra("login-method");

        if (loginMethod.equalsIgnoreCase("g")){
            userId = j.getStringExtra("user_id");
            idTx.setText("USER ID: "+userId);
            Glide.with(this).load(j.getStringExtra("user_img")).into(img);
        }else if(loginMethod.equalsIgnoreCase("otp")){
            userId = j.getStringExtra("user_id");
            idTx.setText("User ID: "+userId);
            img.setVisibility(View.INVISIBLE);
        }else{
            userId = j.getStringExtra("user_id");
            idTx.setText("User ID: "+userId);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified()){
            Toast.makeText(this, "You are Verified", Toast.LENGTH_LONG).show();
        }
        singOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}