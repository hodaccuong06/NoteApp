package com.example.noteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.noteapp.Adapter.NoteBlockAdapter;
import com.example.noteapp.Domain.Note;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BlockActivity extends AppCompatActivity {
    RecyclerView rv_notelock;
    NoteBlockAdapter adapter;
    private DatabaseReference noteReference;
    SharedPreferences sharedPreferences;
    String phone = "";
    SearchView searchViewlock;
    private List<Note> notesList;
    ImageView backblock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        rv_notelock = findViewById(R.id.rv_notelock);
        rv_notelock.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        searchViewlock = findViewById(R.id.searchnotelock);
        backblock = findViewById(R.id.backblock);
        sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        noteReference = FirebaseDatabase.getInstance().getReference().child("Notes").child(phone);

        setupFirebase();

        searchViewlock.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.filter(newText);
                }
                return true;
            }
        });
        backblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(BlockActivity.this, MainActivity.class);
                mainIntent.putExtra("navigateToFragment", true);
                startActivity(mainIntent);
            }
        });
    }

    private void setupFirebase() {
        notesList = new ArrayList<>();
        noteReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note note = snapshot.getValue(Note.class);
                    if (note != null) {
                        boolean isNoteBlock = note.isBlock();
                        if (isNoteBlock) {
                            notesList.add(note);
                        }
                    }
                }
                if (rv_notelock != null) {
                    adapter = new NoteBlockAdapter(BlockActivity.this, notesList);
                    rv_notelock.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}