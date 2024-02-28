package com.example.noteapp.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.noteapp.BroadcastReceiver.TaskService;
import com.example.noteapp.Domain.User;
import com.example.noteapp.EditUserActivity;
import com.example.noteapp.PasswordBlockActivity;
import com.example.noteapp.R;
import com.example.noteapp.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserFragment extends Fragment {

    String phone;
    Button logout, thietlaptaikhoan, block;
    TextView NameUser,donhang,shopingv;
    ImageView imageUser,shopingc;
    FirebaseUser users;
    DatabaseReference reference;

    SharedPreferences sharedPreferences;
    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user,container,false);

        logout = view.findViewById(R.id.dangxuat);
        NameUser = view.findViewById(R.id.NameUser);
        imageUser = view.findViewById(R.id.imageUser);
        block = view.findViewById(R.id.block);
        thietlaptaikhoan = view.findViewById(R.id.thietlaptaikhoan);
        sharedPreferences = requireActivity().getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getActivity(), TaskService.class);
                FirebaseAuth.getInstance().signOut();
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("TaiKhoan", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("phone");
                editor.apply();
                Intent intent = new Intent(requireActivity(), SignInActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireActivity().stopService(serviceIntent);
                } else {
                    requireActivity().stopService(serviceIntent);
                }
                startActivity(intent);
                requireActivity().finish();
            }
        });
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PasswordBlockActivity.class);
                intent.putExtra("comingFromUserFragment", true);
                startActivity(intent);
            }
        });
        thietlaptaikhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditUserActivity.class);
                startActivity(intent);
            }
        });
        reference = FirebaseDatabase.getInstance().getReference().child("User");
        reference.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                Glide.with(imageUser.getContext())
                        .load(userProfile.getProfileImageUrl())
                        .into(imageUser);

                NameUser.setText(userProfile.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}