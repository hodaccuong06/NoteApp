package com.example.noteapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.goodiebag.pinview.Pinview;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PasswordBlockActivity extends AppCompatActivity {
    Pinview pinviewpassword;
    Button thempassadd;
    SharedPreferences sharedPreferences;
    String phone="";
    String noteId="";
    TextView quenmatkhau;
    public View overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_block);
        pinviewpassword = findViewById(R.id.pinviewpassword);
        sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        noteId = getIntent().getStringExtra("noteId");
        pinviewpassword.setPinBackgroundRes(R.drawable.sample_background);
        pinviewpassword.setInputType(Pinview.InputType.NUMBER);
        thempassadd = findViewById(R.id.thempassadd);
        quenmatkhau = findViewById(R.id.quenmatkhau);
        thempassadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference("User").child((phone));
                String enteredPassword = pinviewpassword.getValue();
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String blockPassword = dataSnapshot.child("blockPassword").getValue(String.class);

                            if (enteredPassword.equals(blockPassword)) {

                                Intent intent = getIntent();
                                if (intent.hasExtra("comingFromUserFragment")) {
                                    // Nếu intent được gửi từ UserFragment
                                    Intent blockIntent = new Intent(PasswordBlockActivity.this, BlockActivity.class);
                                    startActivity(blockIntent);
                                } else if (intent.hasExtra("comingFromNoteAdapter")) {
                                    DatabaseReference noteRef = database.getReference("Notes").child(phone).child(noteId);
                                    noteRef.child("block").setValue(false);
                                    Intent mainIntent = new Intent(PasswordBlockActivity.this, MainActivity.class);
                                    mainIntent.putExtra("navigateToUserFragment", true);
                                    startActivity(mainIntent);
                                } else {
                                    DatabaseReference noteRef = database.getReference("Notes").child(phone).child(noteId);
                                    noteRef.child("block").setValue(true);
                                    Intent mainIntent = new Intent(PasswordBlockActivity.this, MainActivity.class);
                                    mainIntent.putExtra("navigateToUserFragment", true);
                                    startActivity(mainIntent);
                                }
                                finish();

                            } else {
                                Toast.makeText(PasswordBlockActivity.this, "Mã PIN không khớp. Vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                    }
                });

            }
        });
        quenmatkhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimBackground(1f, (ViewGroup) getWindow().getDecorView().getRootView());
                openDialog(Gravity.CENTER);
            }
        });
    }
    private void openDialog(int gravity){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.quenmatkhaublock);

        Window window = dialog.getWindow();

        if (window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowLayoutParams = window.getAttributes();
        windowLayoutParams.gravity = gravity;
        window.setAttributes(windowLayoutParams);
        Button quayve = dialog.findViewById(R.id.quayve);
        EditText nhapmatkhaublock = dialog.findViewById(R.id.pass);
        TextInputLayout passlayout = dialog.findViewById(R.id.passlayout);
        Button xacnhan = dialog.findViewById(R.id.xacnhanmatkhau);
        nhapmatkhaublock = passlayout.getEditText();


        quayve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOverlay(overlay, (ViewGroup) getWindow().getDecorView().getRootView());
                dialog.dismiss();
            }
        });
        EditText finalNhapmatkhaublock = nhapmatkhaublock;
        xacnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference("User").child((phone));
                String enteredPassword1 = finalNhapmatkhaublock.getText().toString();
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String password = dataSnapshot.child("password").getValue(String.class);

                            if (enteredPassword1.equals(password)) {

                                Intent intent = new Intent(PasswordBlockActivity.this, AddPasswordBlockActivity.class);
                                startActivity(intent);

                            }else {
                                Toast.makeText(PasswordBlockActivity.this,"Sai mật khẩu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        dialog.show();
    }

    public void dimBackground(float alpha, ViewGroup rootView) {
        overlay = new View(this);
        overlay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        overlay.setBackgroundColor(Color.parseColor("#80000000"));
        rootView.addView(overlay);
    }
    public void removeOverlay(View overlay, ViewGroup rootView) {
        rootView.removeView(overlay);
    }


}