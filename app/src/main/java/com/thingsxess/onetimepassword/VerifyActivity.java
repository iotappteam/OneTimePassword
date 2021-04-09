package com.thingsxess.onetimepassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {

    Button verifyBtn;
    PinView pinView;
    TextView resendTx;
    String pin, otpCode;
    String number;
    String TAG;

    //FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        Intent i = getIntent();
        number = i.getStringExtra("number");

        verifyBtn = findViewById(R.id.btn_verify);
        resendTx = findViewById(R.id.tx_resend);
        pinView = findViewById(R.id.pv_code);
        sendCode(number);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resendTx.setVisibility(View.VISIBLE);
            }
        },15000);


        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pin = pinView.getText().toString();
                finish_everything(pin);
            }
        });

        resendTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode(number);
            }
        });
        resendTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendTx.setText("Code Resent");
            }
        });
    }


    //Working OTP Code
    private void sendCode(String number){
        //Sending Code to User
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+92"+number, 60, TimeUnit.SECONDS, this, mCallback);
        Toast.makeText(this, "Code Sent", Toast.LENGTH_SHORT).show();
    }

    //Creating a Callback
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback= new PhoneAuthProvider
            .OnVerificationStateChangedCallbacks() {
        @Override
        //Saving the OTP sent from Firebase
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            otpCode = s;
        }

        @Override
        //If this is the same device run this code
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null){
                finish_everything(code);
            }
        }

        @Override
        //If verification failed run this code
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    //Sign in user
    private void finish_everything(String code) {
        pinView.setText(code);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpCode, code);
        signIn(credential);
    }

    //Method to sign in user
    private void signIn(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            //if login successful run this code
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(VerifyActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    String userId = mAuth.getCurrentUser().getUid();
                    Intent j = new Intent(VerifyActivity.this, HomeActivity.class);
                    TAG ="otp";
                    j.putExtra("login-method", TAG);
                    j.putExtra("user_id", userId);
                    //j.putExtra("user_id", userId);
                    startActivity(j);
                    finish();
                }else{
                    Toast.makeText(VerifyActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Auto fetch OPT code
    private void requestSMSPermission()
    {
        String permission = Manifest.permission.RECEIVE_SMS;

        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED)
        {
            String[] permission_list = new String[1];
            permission_list[0] = permission;

            ActivityCompat.requestPermissions(this, permission_list,1);
        }
    }
}