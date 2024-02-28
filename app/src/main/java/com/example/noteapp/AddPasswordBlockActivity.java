package com.example.noteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapp.Domain.User;
import com.goodiebag.pinview.Pinview;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddPasswordBlockActivity extends AppCompatActivity {
    Pinview pinView,pinview1;
    Button thempass;
    SharedPreferences sharedPreferences;
    String phone="";
    String noteId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password_block);
        pinView = findViewById(R.id.pinview);
        pinview1 = findViewById(R.id.pinview1);
        sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        noteId = getIntent().getStringExtra("noteId");
        pinView.setPinBackgroundRes(R.drawable.sample_background);
        pinView.setInputType(Pinview.InputType.NUMBER);
        pinview1.setPinBackgroundRes(R.drawable.sample_background);
        pinview1.setInputType(Pinview.InputType.NUMBER);

        thempass=findViewById(R.id.thempass);

        thempass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin1 = pinView.getValue();
                String pin2 = pinview1.getValue();
                if (!pin1.isEmpty() && !pin2.isEmpty()) {
                    if (!pin1.equals(pin2)) {
                        Toast.makeText(AddPasswordBlockActivity.this, "Mã PIN không khớp. Vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
                        pinview1.clearValue();
                    }else {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference userRef = database.getReference("User").child((phone));
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                String blockPassword = pinview1.getValue();
                                boolean isBlock = user.isBlock();
                                if(!isBlock){
                                    snapshot.getRef().child("block").setValue(true);
                                    snapshot.getRef().child("blockPassword").setValue(blockPassword);;
                                    FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                                    DatabaseReference noteRef = database1.getReference("Notes").child(phone).child(noteId);
                                    noteRef.child("block").setValue(true);
                                    saveUserAndNavigateBack();
                                }else{
                                    snapshot.getRef().child("blockPassword").setValue(blockPassword);
                                    Intent intent = new Intent(AddPasswordBlockActivity.this, PasswordBlockActivity.class);
                                    startActivity(intent);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                }

            }
        });
    }
    private void saveUserAndNavigateBack() {

        Intent intent = new Intent(AddPasswordBlockActivity.this, MainActivity.class);
        intent.putExtra("navigateToUserFragment", true);
        startActivity(intent);
        finish();
    }
}