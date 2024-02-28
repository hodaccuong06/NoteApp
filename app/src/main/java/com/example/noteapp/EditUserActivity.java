package com.example.noteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.noteapp.Domain.User;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

public class EditUserActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST_CODE = 1;

    String phone = "";
    String image = "";
    FirebaseDatabase database;
    DatabaseReference user;

    Uri imageUri;
    ImageView themimageuser;
    EditText passcu, passnew, passnew2, name;

    Button chonanhuser;
    Button luuuser;
    Button quaylaiuser;
    Button luumatkhau;
    SharedPreferences sharedPreferences;


    TextInputLayout passculayout, passmoilayout, nhaplaipassmoilayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        database  = FirebaseDatabase.getInstance();
        user  = database.getReference().child("User");


        sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        getUser(phone);

        themimageuser = findViewById(R.id.luuimageuser);
        name = findViewById(R.id.luunameuser);
        passcu = findViewById(R.id.passcu);
        passnew = findViewById(R.id.passmoi);
        chonanhuser =findViewById(R.id.chonanhuser);
        quaylaiuser = findViewById(R.id.quaylaiuser);
        luuuser = findViewById(R.id.luuuser);
        passnew2 = findViewById(R.id.nhaplaipassmoi);
        luumatkhau = findViewById(R.id.luumatkhau);
        passculayout = findViewById(R.id.passculayout);
        passmoilayout =findViewById(R.id.passmoilayout);
        nhaplaipassmoilayout = findViewById(R.id.nhaplaipassmoilayout);
        passcu = passculayout.getEditText();
        passnew = passmoilayout.getEditText();
        passnew2 = nhaplaipassmoilayout.getEditText();



        chonanhuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Mở cửa sổ chọn ảnh từ thư viện
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            }
        });

        quaylaiuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        luuuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUser();
                saveUserAndNavigateBack();
            }
        });
        luumatkhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savepass();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Sử dụng Glide để hiển thị ảnh trong ImageView
            Glide.with(this)
                    .load(imageUri)
                    .into(themimageuser);

        }else {
            Toast.makeText(EditUserActivity.this, "Không chọn được hình ảnh", Toast.LENGTH_SHORT).show();
        }
    }


    private void getUser(String phone) {
        DatabaseReference userReference = user.child(phone);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        Log.d("Debug", "User name: " + user.getName());

                        // Check profile image URL
                        String imageUrl = user.getProfileImageUrl();
                        if (imageUrl != null) {
                            Log.d("Debug", "Profile image URL: " + imageUrl);

                            // Update UI components
                            name.setText(user.getName());
                            if (!isFinishing()) {
                                Glide.with(EditUserActivity.this)
                                        .load(imageUrl)
                                        .into(themimageuser);
                            }
                            image = imageUrl;
                        } else {
                            Log.d("Debug", "Profile image URL is null.");
                        }
                    }
                } else {
                    Log.d("Debug", "User data does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", "Error getting user data: " + error.getMessage());
            }
        });
    }
    private void savepass() {
        final String currentPassword = passcu.getText().toString().trim();
        final String newPassword = passnew.getText().toString().trim();
        final String newPasswordConfirmation = passnew2.getText().toString().trim();


        if (currentPassword.isEmpty()) {
            Toast.makeText(EditUserActivity.this, "Nhập mật khẩu cũ", Toast.LENGTH_SHORT).show();
            return ;
        }

        if (newPassword.isEmpty()) {
            Toast.makeText(EditUserActivity.this, "Nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
            return ;
        }
        if (newPasswordConfirmation.isEmpty()) {
            Toast.makeText(EditUserActivity.this, "Nhập lại mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userReference = database.getReference().child("User").child(phone);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getPassword().equals(currentPassword)) {
                        if (newPassword.equals(newPasswordConfirmation)) {
                            // Update password in the database
                            user.setPassword(newPassword);
                            userReference.setValue(user);


                            // Notify the user that the password has been updated
                            Log.d("Debug", "Password updated successfully.");
                            Toast.makeText(EditUserActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            passcu.setText("");
                            passnew.setText("");
                            passnew2.setText("");
                        } else {
                            // Notify the user that the new passwords do not match
                            Log.d("Debug", "New passwords do not match.");
                            Toast.makeText(EditUserActivity.this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("Debug", "Entered current password: " + currentPassword);
                        Log.d("Debug", "Stored user password: " + user.getPassword());
                        // Notify the user that the current password is incorrect
                        Log.d("Debug", "Incorrect current password.");
                        Toast.makeText(EditUserActivity.this, "Mật khâ cũ không đúng", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", "Error updating password: " + error.getMessage());
            }
        });
    }

    private void saveUser() {


        String newTitle = name.getText().toString();

        DatabaseReference newUserRef = user.child(phone);
        newUserRef.child("name").setValue(newTitle);

        if (image != null && !image.isEmpty()) {
            newUserRef.child("profileImageUrl").setValue(image);
            Upload();
        }

        Toast.makeText(EditUserActivity.this, "Thông tin đã được lưu", Toast.LENGTH_SHORT).show();
    }

    private void saveImageURLToDatabase(String imageURL) {
        HashMap<String, Object> updateData = new HashMap<>();
        updateData.put("profileImageUrl", imageURL);

        user.child(phone).updateChildren(updateData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditUserActivity.this, "URL ảnh đã được lưu", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditUserActivity.this, "Lỗi khi lưu URL ảnh", Toast.LENGTH_SHORT).show();
                });
    }

    private void Upload(){
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Profile");

            String imageName = UUID.randomUUID().toString();
            StorageReference imageRef = storageRef.child(imageName);

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageURL = uri.toString();
                            saveImageURLToDatabase(imageURL);
                        });
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(EditUserActivity.this, "Lỗi khi tải lên ảnh", Toast.LENGTH_SHORT).show();
                    });
        } else {

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigateToUserFragment", true);
        startActivity(intent);
        finish();
    }
    private void saveUserAndNavigateBack() {
        Intent intent = new Intent(EditUserActivity.this, MainActivity.class);
        intent.putExtra("navigateToFragment", true);
        startActivity(intent);
        finish();
    }
}