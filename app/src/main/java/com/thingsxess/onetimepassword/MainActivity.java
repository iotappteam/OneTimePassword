package com.thingsxess.onetimepassword;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button sendCodeBtn, emailBtn;
    EditText phone;
    String number;
    SignInButton signInGoogleBtn;
    GoogleSignInClient googleSignInClient;
    String TAG = "";
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Initializing the instance
        mAuth = FirebaseAuth.getInstance();

        //Hook
        sendCodeBtn = findViewById(R.id.btn_send_code);
        emailBtn = findViewById(R.id.btn_email);
        signInGoogleBtn = findViewById(R.id.btn_google);
        phone = findViewById(R.id.et_number);

        //Takes the user to OTP screen
        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().toString()!= null && phone.getText().toString().length()==10){
                    number = phone.getText().toString();
                    Intent i = new Intent(MainActivity.this, VerifyActivity.class);
                    i.putExtra("number", number);
                    startActivity(i);
                    finish();
                }else {
                    Toast.makeText(MainActivity.this, "Please Enter a Valid Number", Toast.LENGTH_LONG).show();
                }
            }
        });

        //register with email and password
        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(MainActivity.this, EmailSignUpActivity.class);
                startActivity(k);
                finish();
            }
        });

        //Google Client Initialization
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //Sign In User on click using Google
        signInGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //Handling Sign In Request
            try {
                GoogleSignInAccount acc = task.getResult(ApiException.class);
                Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_LONG).show();
                AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
                //Registering User
                mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent home = new Intent(MainActivity.this, HomeActivity.class);
                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                            //String userId = mAuth.getCurrentUser().getUid();
                            TAG = "g";
                            String userId = account.getDisplayName();
                            Uri userImgUri = account.getPhotoUrl();
                            //Toast.makeText(MainActivity.this, userImgUri.toString(), Toast.LENGTH_SHORT).show();
                            home.putExtra("user_id", userId);
                            home.putExtra("login-method", TAG);
                            home.putExtra("user_img", userImgUri.toString());
                            //Toast.makeText(MainActivity.this, account.getDisplayName(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(MainActivity.this, account.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(home);
                            finish();
                        }else{
                            Toast.makeText(MainActivity.this, "Task In Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }catch (ApiException e){
                Toast.makeText(this, "Signed In Failed", Toast.LENGTH_LONG).show();
            }
        }
    }
}