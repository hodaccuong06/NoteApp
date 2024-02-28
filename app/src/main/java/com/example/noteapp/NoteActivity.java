package com.example.noteapp;

import static jp.wasabeef.richeditor.Utils.getCurrentTime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.noteapp.Domain.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nvt.color.ColorPickerDialog;
import com.yahiaangelo.markdownedittext.MarkdownEditText;
import com.yahiaangelo.markdownedittext.MarkdownStylesBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {
    MarkdownStylesBar markdownStylesBar;
    MarkdownEditText editText;
    LinearLayout mBottom;
    FloatingActionButton floatingActionButton,floatingImageButton;
    RelativeLayout relativeLayout;
    ImageView btnsave,back;
    private DatabaseReference notesRef;
    EditText edittextnoteTilte;
    ColorPickerDialog colorPicker;
    SharedPreferences sharedPreferences;
    String phone="";
    String noteId="";
    CardView view1,view2;
    View overlay;



    private int color;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        editText = findViewById(R.id.edittextnote);
        markdownStylesBar = findViewById(R.id.styleBar);
        editText.setStylesBar(markdownStylesBar);
        mBottom = findViewById(R.id.bottomBar);
        edittextnoteTilte = findViewById(R.id.edittextnoteTilte);
        btnsave = findViewById(R.id.btnsave);
        back = findViewById(R.id.back);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        Log.d("TAG", "phone"  + phone);
        notesRef = FirebaseDatabase.getInstance().getReference().child("Notes");
        if(getIntent() !=null)
            noteId = getIntent().getStringExtra("noteId");
        Log.d("TAG", "orderId"  + noteId);
        if (noteId != null && !noteId.isEmpty()) {
            loadNote(noteId);
            btnsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateNote();
                    saveUserAndNavigateBack();

                }
            });
        } else {
            btnsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveNote();
                    saveUserAndNavigateBack();
                }
            });

        }




        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mBottom.setVisibility(View.VISIBLE);
                } else {
                    mBottom.setVisibility(View.GONE);
                }
            }
        });


        relativeLayout = findViewById(R.id.relativeLayout);
        floatingActionButton = findViewById(R.id.fabColorPick);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimBackground(1f);
                colorPicker = new ColorPickerDialog(
                        NoteActivity.this,
                        Color.BLACK,
                        true,
                        new ColorPickerDialog.OnColorPickerListener() {
                            @Override
                            public void onCancel(ColorPickerDialog dialog) {
                                removeOverlay(overlay);
                            }

                            @Override
                            public void onOk(ColorPickerDialog dialog, int colorPicker) {
                                view1.setCardBackgroundColor(colorPicker);
                                view2.setCardBackgroundColor(colorPicker);
                                color = colorPicker;
                                removeOverlay(overlay);

                            }
                        });
                colorPicker.show();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }
    public void dimBackground(float alpha) {
        overlay = new View(this);
        overlay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        overlay.setBackgroundColor(Color.parseColor("#80000000"));
        ViewGroup rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.addView(overlay);
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOverlay(overlay);
            }
        });
    }
    public void removeOverlay(View overlay) {
        ViewGroup rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.removeView(overlay);
    }
    private void loadNote(String noteId) {
        DatabaseReference specificAddress = notesRef.child(phone).child(noteId);
        specificAddress.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Note loadedNote = snapshot.getValue(Note.class);

                if (loadedNote != null) {
                    String markdown = loadedNote.getContent();
                    editText.renderMD(markdown);
                    edittextnoteTilte.setText(loadedNote.getTitle());
                    color = loadedNote.getColor();
                    if (color != 0) {
                        view1.setCardBackgroundColor(color);
                        view2.setCardBackgroundColor(color);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void SaveNote(){
        if (phone != null) {
            String noteId = notesRef.child(phone).push().getKey();
            if (noteId != null) {
                String noteTitle = edittextnoteTilte.getText().toString();
                String noteContent = editText.getMD();
                String noteDate = getCurrentDate();
                String noteTime = String.valueOf(getCurrentTime());


                Note note = new Note(
                        noteId,
                        noteTitle,
                        noteContent,
                        noteDate,
                        noteTime,
                        color

                );
                notesRef.child(phone).child(noteId).setValue(note);
            }
        } else {
        }
    }
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    public void updateNote(){
        String noteTitle = edittextnoteTilte.getText().toString();
        String noteContent = editText.getMD();

        if (noteId != null && !noteId.isEmpty()) {
            DatabaseReference specificNoteRef = notesRef.child(phone).child(noteId);

            specificNoteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        snapshot.getRef().child("title").setValue(noteTitle);
                        snapshot.getRef().child("content").setValue(noteContent);
                        snapshot.getRef().child("date").setValue(getCurrentDate());
                        snapshot.getRef().child("time").setValue(String.valueOf(getCurrentTime()));
                        snapshot.getRef().child("color").setValue(color);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
    private void saveUserAndNavigateBack() {

        Intent intent = new Intent(NoteActivity.this, MainActivity.class);
        intent.putExtra("navigateToFragment", true);
        startActivity(intent);
        finish();
    }

}