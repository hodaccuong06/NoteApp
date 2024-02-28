package com.example.noteapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapp.BroadcastReceiver.TaskBroadcastReceiver;
import com.example.noteapp.Domain.Task;
import com.example.noteapp.Fragment.TaskFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class CreateTaskActivity extends AppCompatActivity {
    EditText addTaskTitle;

    EditText addTaskDescription;

    TextView taskDate;

    TextView taskTime;

    EditText taskEvent;
    String taskId = "";
    Task task;
    int mYear, mMonth, mDay;
    int mHour, mMinute;
    AlarmManager alarmManager;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    TaskFragment taskFragment;
    SharedPreferences sharedPreferences;
    DatabaseReference tasksRef;
    public static int count = 0;
    String phone="";
    ImageView back1, btnSave1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        tasksRef = FirebaseDatabase.getInstance().getReference().child("Tasks");
        Log.d("TAG", "phone"  + phone);
        btnSave1 = findViewById(R.id.btnsave1);
        if(getIntent() !=null)
            taskId = getIntent().getStringExtra("taskId");
        Log.d("TAG", "taskId"  + taskId);
        if (taskId != null && !taskId.isEmpty()) {
            loadTask(taskId);
            btnSave1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateFields()) {
                        updateTask();
                        saveTaskAndNavigateBack();
                    }


                }
            });
        } else {
            btnSave1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateFields()) {
                        createTask();
                        saveTaskAndNavigateBack();
                    }
                }
            });

        }

        addTaskTitle = findViewById(R.id.addTaskTitle);
        addTaskDescription = findViewById(R.id.addTaskDescription);
        taskDate = findViewById(R.id.taskDate);
        taskTime = findViewById(R.id.taskTime);
        taskEvent = findViewById(R.id.taskEvent);
        taskDate.setOnTouchListener((v, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(CreateTaskActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                        (view1, year, monthOfYear, dayOfMonth) -> {
                            taskDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            datePickerDialog.dismiss();
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                InputMethodManager imm = (InputMethodManager) CreateTaskActivity.this.getSystemService(CreateTaskActivity.this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(taskEvent.getWindowToken(), 0);
            }
            return true;
        });

        taskTime.setOnTouchListener((v, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(CreateTaskActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                        (view12, hourOfDay, minute) -> {
                            taskTime.setText(hourOfDay + ":" + minute);
                            timePickerDialog.dismiss();
                        }, mHour, mMinute, true);
                timePickerDialog.show();
                InputMethodManager imm = (InputMethodManager) CreateTaskActivity.this.getSystemService(CreateTaskActivity.this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(taskEvent.getWindowToken(), 0);
            }
            return true;
        });
        back1 = findViewById(R.id.back1);
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTaskAndNavigateBack();
            }
        });
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }
    private void loadTask(String taskId) {
        DatabaseReference specificAddress = tasksRef.child(phone).child(taskId);
        specificAddress.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Task loadedTask = snapshot.getValue(Task.class);

                if (loadedTask != null) {
                    addTaskTitle.setText(loadedTask.getTitle());
                    addTaskDescription.setText(loadedTask.getDescription());
                    taskTime.setText(loadedTask.getTime());
                    taskDate.setText(loadedTask.getDate());
                    taskEvent.setText(loadedTask.getEvent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void updateTask(){
        String taskTitle = addTaskTitle.getText().toString();
        String taskdescription = addTaskTitle.getText().toString();
        String date = taskDate.getText().toString();
        String time = taskTime.getText().toString();
        String event = taskEvent.getText().toString();

        if (taskId != null && !taskId.isEmpty()) {
            DatabaseReference specificNoteRef = tasksRef.child(phone).child(taskId);

            specificNoteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        snapshot.getRef().child("title").setValue(taskTitle);
                        snapshot.getRef().child("description").setValue(taskdescription);
                        snapshot.getRef().child("date").setValue(date);
                        snapshot.getRef().child("time").setValue(time);
                        snapshot.getRef().child("event").setValue(event);
                        createAnAlarm();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
    public boolean validateFields() {
        if(addTaskTitle.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
            return false;
        } else if(addTaskDescription.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
            return false;
        } else if(taskDate.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Vui lòng nhập ngày", Toast.LENGTH_SHORT).show();
            return false;
        } else if(taskTime.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Vui lòng nhập thời gian", Toast.LENGTH_SHORT).show();
            return false;
        } else if(taskEvent.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Vui lòng nhập một sự kiện", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void createTask() {
        @SuppressLint("StaticFieldLeak")
        class saveTaskInBackend extends AsyncTask<Void, Void, Void> {
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                if (phone != null) {
                    String taskId = tasksRef.child(phone).push().getKey();
                    if (taskId != null) {
                        String title = addTaskTitle.getText().toString();
                        String description = addTaskDescription.getText().toString();
                        String date = taskDate.getText().toString();
                        String time = taskTime.getText().toString();
                        String event = taskEvent.getText().toString();
                        String completion = "Chưa hoàn thành";


                        Task newTask = new Task(taskId, title,completion, description, date, time, event);

                        tasksRef.child(phone).child(taskId).setValue(newTask);
                    }
                } else {
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    createAnAlarm();
                }

            }
        }
        saveTaskInBackend st = new saveTaskInBackend();
        st.execute();
    }
    public void createAnAlarm() {
        try {
            String[] items1 = taskDate.getText().toString().split("-");
            String dd = items1[0];
            String month = items1[1];
            String year = items1[2];

            String[] itemTime = taskTime.getText().toString().split(":");
            String hour = itemTime[0];
            String min = itemTime[1];

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.parseInt(year));
            cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dd));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            cal.set(Calendar.MINUTE, Integer.parseInt(min));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Intent alarmIntent = new Intent(CreateTaskActivity.this, TaskBroadcastReceiver.class);
            alarmIntent.putExtra("TITLE", addTaskTitle.getText().toString());
            alarmIntent.putExtra("DESC", addTaskDescription.getText().toString());
            alarmIntent.putExtra("DATE", taskDate.getText().toString());
            alarmIntent.putExtra("TIME", taskTime.getText().toString());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(CreateTaskActivity.this, count, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void saveTaskAndNavigateBack() {

        Intent intent = new Intent(CreateTaskActivity.this, MainActivity.class);
        intent.putExtra("navigateToTaskFragment", true);
        startActivity(intent);
        finish();
    }
}