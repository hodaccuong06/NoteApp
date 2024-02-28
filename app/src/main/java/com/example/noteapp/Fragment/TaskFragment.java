package com.example.noteapp.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.Adapter.TaskAdapter;
import com.example.noteapp.CreateTaskActivity;
import com.example.noteapp.Domain.Task;
import com.example.noteapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class TaskFragment extends Fragment {
    ImageView addtask;
    RecyclerView rv_task;
    TaskAdapter adapter;
    private DatabaseReference taskReference;
    SharedPreferences sharedPreferences;
    String phone="";
    List<Task> taskList;
    SearchView searchtask;
    ImageView calendar;

    public TaskFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        addtask = view.findViewById(R.id.addtask);
        rv_task = view.findViewById(R.id.rv_task);
        rv_task.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        sharedPreferences = requireActivity().getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        taskReference = FirebaseDatabase.getInstance().getReference().child("Tasks").child(phone);
        searchtask = view.findViewById(R.id.searchtask);

        addtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateTaskDialog();

            }
        });
        setupFirebase();
        searchtask.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    public void showCreateTaskDialog() {
        Intent intent = new Intent(getContext(), CreateTaskActivity.class);
        startActivity(intent);
    }
    private void setupFirebase(){
        taskList = new ArrayList<>();
        taskReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                        Log.d("TaskFragment", "Number of task: " + taskList.size());
                    }
                }if (isAdded()) {

                    adapter = new TaskAdapter(requireContext(), taskList);
                    rv_task.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}