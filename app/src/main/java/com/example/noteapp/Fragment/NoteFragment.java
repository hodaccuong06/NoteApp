package com.example.noteapp.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.noteapp.Adapter.NoteAdapter;
import com.example.noteapp.Domain.Note;
import com.example.noteapp.NoteActivity;
import com.example.noteapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class NoteFragment extends Fragment {
    ImageView addNoteButton;
    RecyclerView rv_note;
    NoteAdapter adapter;
    private DatabaseReference noteReference;
    SharedPreferences sharedPreferences;
    String phone="";
    SearchView searchView;
    private List<Note> notesList;



    public NoteFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        addNoteButton = view.findViewById(R.id.addnote);
        rv_note = view.findViewById(R.id.rv_note);
        rv_note.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        searchView = view.findViewById(R.id.searchnote);



        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NoteActivity.class);
                startActivity(intent);
            }
        });
        sharedPreferences = requireActivity().getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        noteReference = FirebaseDatabase.getInstance().getReference().child("Notes").child(phone);

        setupFirebase();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        return view;

    }


    private void setupFirebase(){
        notesList = new ArrayList<>();
        noteReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note note = snapshot.getValue(Note.class);
                    if (note != null) {
                        boolean isNoteBlock = note.isBlock();
                        if (!isNoteBlock) {
                            notesList.add(note);
                        }
                    }
                }
                if (isAdded()) {
                    adapter = new NoteAdapter(requireContext(), notesList);
                    rv_note.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}