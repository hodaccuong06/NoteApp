package com.example.noteapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.noteapp.BroadcastReceiver.TaskService;
import com.example.noteapp.Fragment.NoteFragment;
import com.example.noteapp.Fragment.TaskFragment;
import com.example.noteapp.Fragment.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView mBottomNAV;
    NoteFragment noteFragment = new NoteFragment();
    TaskFragment taskFragment = new TaskFragment();
    UserFragment userFragment = new UserFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBottomNAV = findViewById(R.id.bottom_nav);
        startForegroundService(new Intent(this, TaskService.class));
        getSupportFragmentManager().beginTransaction().replace(R.id.container,noteFragment).commit();

        mBottomNAV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.ghichu) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, noteFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.nhiemvu) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, taskFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.nguoidung) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, userFragment).commit();
                    return true;
                }

                return false;
            }
        });

        if (savedInstanceState != null) {
            int selectedTabId = savedInstanceState.getInt("SelectedTabId");
            mBottomNAV.setSelectedItemId(selectedTabId);
        } else {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        boolean navigateToUserFragment = intent.getBooleanExtra("navigateToUserFragment", false);
        boolean navigateToTaskFragment = intent.getBooleanExtra("navigateToTaskFragment", false);
        boolean navigateToFragment = intent.getBooleanExtra("navigateToFragment", false);
        boolean navigateToNoteFragment = intent.getBooleanExtra("navigateToNoteFragment", false);

        if (navigateToUserFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, userFragment).commit();
            mBottomNAV.getMenu().findItem(R.id.nguoidung).setChecked(true);
        } else if (navigateToTaskFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, taskFragment).commit();
            mBottomNAV.getMenu().findItem(R.id.nhiemvu).setChecked(true);
        } else if (navigateToFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, noteFragment).commit();
            mBottomNAV.getMenu().findItem(R.id.ghichu).setChecked(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SelectedTabId", mBottomNAV.getSelectedItemId());
    }
}
