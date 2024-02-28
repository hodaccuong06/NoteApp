package com.example.noteapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OtpTextView;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button btn_search, btn_verify, btn_password;
    EditText phonecode, phonenumber, password, repass;
    TextView btn_resend;
    String verificationID;
    ScrollView searchpassword;
    LinearLayout otp, verifyPassword;
    OtpTextView otp_view;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    PhoneAuthCredential credential;
    String phone="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        btn_search = findViewById(R.id.btn_search);
        btn_verify = findViewById(R.id.btn_verify);
        btn_password = findViewById(R.id.btn_pass);
        phonecode = findViewById(R.id.phonecode);
        phonenumber = findViewById(R.id.phonenumber);
        password = findViewById(R.id.password);
        repass = findViewById(R.id.repassword);
        btn_resend = findViewById(R.id.btn_resend);
        searchpassword = findViewById(R.id.searchphone);
        otp = findViewById(R.id.otp);
        verifyPassword = findViewById(R.id.re_pass);
        otp_view = findViewById(R.id.otp_view);
        mAuth = FirebaseAuth.getInstance();

        // Thiết lập sự kiện khi nhấn nút "Tìm kiếm"
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPhoneNumber();
            }
        });

        btn_resend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendVerificationCode(phone);
                Toast.makeText(ForgotPasswordActivity.this, "Đã gửi lại code", Toast.LENGTH_SHORT).show();
            }
        });
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otps = otp_view.getOTP();
                if(otps.isEmpty()){
                    Toast.makeText(ForgotPasswordActivity.this, "Nhập mã otp",Toast.LENGTH_SHORT).show();
                }else{
                    verifyCode(otps);

                }
            }
        });
        btn_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password1 = repass.getText().toString().trim();
                changePassword(credential, password1);
            }
        });

    }

    private void verifyCode(String otps) {
        credential = PhoneAuthProvider.getCredential(verificationID, otps);
        verifyPassword.setVisibility(View.VISIBLE);
        otp.setVisibility(View.GONE);
    }
    private void sendVerificationCode(String phone) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        String code = phoneAuthCredential.getSmsCode();
                        if(code != null){
                            otp_view.setOTP(code);
                            verifyCode(code);
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        searchpassword.setVisibility(View.GONE);
                        otp.setVisibility(View.VISIBLE);

                        verificationID = s;

                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    private void searchPhoneNumber() {
        phone = phonecode.getText().toString()+phonenumber.getText().toString() ;
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User");
        userRef.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    sendVerificationCode(phone);
                } else {
                    Log.d("TAG", "phone"+phone);
                    Toast.makeText(ForgotPasswordActivity.this, "Số điện thoại không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ForgotPasswordActivity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePassword(PhoneAuthCredential credential, String password1) {
        String oldPassword = password.getText().toString().trim();
        if (TextUtils.isEmpty(password1)) {
            Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("User").child((phone));

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(password1.equals(oldPassword)){
                    snapshot.getRef().child("password").setValue(password1);;
                    Toast.makeText(ForgotPasswordActivity.this, "Đã đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(ForgotPasswordActivity.this, "Sai nhập lại mật khẩu", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}