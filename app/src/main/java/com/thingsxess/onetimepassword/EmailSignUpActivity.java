package com.thingsxess.onetimepassword;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailSignUpActivity extends AppCompatActivity {
    EditText emailEt, passwordEt;
    String email, password;
    Button createAccountBtn, verifyBtn;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!=null){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);

        //Hook
        emailEt = findViewById(R.id.et_email);
        passwordEt = findViewById(R.id.et_password);
        createAccountBtn = findViewById(R.id.btn_sign_in);
        //verifyBtn = findViewById(R.id.btn_verify_email);

        mAuth = FirebaseAuth.getInstance();

        createAccountBtn.setOnClickListener(v -> {
            email = emailEt.getText().toString();
            password = passwordEt.getText().toString();
            if (email.length()!= 0 && password.length()!=0){
                mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(
                        authResult -> {
                            user = mAuth.getCurrentUser();
                            user.sendEmailVerification().addOnSuccessListener(aVoid -> {
                                Toast.makeText(EmailSignUpActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                                Toast.makeText(EmailSignUpActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                                //redirects to home page
                                Intent j = new Intent(EmailSignUpActivity.this, HomeActivity.class);
                                j.putExtra("user_id", mAuth.getCurrentUser().getUid());
                                j.putExtra("login-method", "e");
                                startActivity(j);

                            }).addOnFailureListener(e ->
                                    Toast.makeText(EmailSignUpActivity.this,
                                            e.getMessage(),
                                            Toast.LENGTH_SHORT).show());
                        }
                ).addOnFailureListener(e ->
                        Toast.makeText(EmailSignUpActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );

            }else {
                Toast.makeText(EmailSignUpActivity.this, "Invalid Input", Toast.LENGTH_LONG).show();
            }
        });
//        verifyBtn.setOnClickListener(v -> {
//            user = mAuth.getCurrentUser();
//            if (user!=null){
//                if (user.isEmailVerified()){
//                    Toast.makeText(EmailSignUp.this, "Verified Successfully", Toast.LENGTH_SHORT).show();
//                    Intent j = new Intent(EmailSignUp.this, HomeActivity.class);
//                    j.putExtra("user_id", mAuth.getCurrentUser().getUid());
//                    j.putExtra("login-method", "e");
//                    startActivity(j);
//                }else{
//                    Toast.makeText(EmailSignUp.this, ""+user.isEmailVerified(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }
}