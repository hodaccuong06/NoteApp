package com.example.noteapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ReminderActivity extends AppCompatActivity {
    ImageView imageView;

    TextView title;

    TextView description;

    TextView timeAndData;

    Button closeButton;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        imageView = findViewById(R.id.imageView);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        timeAndData = findViewById(R.id.timeAndData);
        closeButton = findViewById(R.id.closeButton);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notification);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notification);
        mediaPlayer.start();

        if (getIntent().getExtras() != null) {
            title.setText(getIntent().getStringExtra("TITLE"));
            description.setText(getIntent().getStringExtra("DESC"));
            timeAndData.setText(getIntent().getStringExtra("DATE") + ", " + getIntent().getStringExtra("TIME"));
        }

        Glide.with(getApplicationContext()).load(R.drawable.alert).into(imageView);
        closeButton.setOnClickListener(view -> finish());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}