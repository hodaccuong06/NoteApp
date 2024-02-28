package com.example.noteapp.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.CreateTaskActivity;
import com.example.noteapp.Domain.Task;
import com.example.noteapp.Fragment.TaskFragment;
import com.example.noteapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{
    Context context;
    private List<Task> taskList;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy", Locale.US);
    public SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-M-yyyy", Locale.US);
    Date date = null;
    String outputDateString = null;
    private TaskFragment taskFragment;
    public View overlay;
    private List<Task> fullTasksList;

    public TaskAdapter(Context context, List<Task> taskList,TaskFragment taskFragment) {
        this.context = context;
        this.taskList = taskList;
        this.taskFragment = taskFragment;
    }

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.getTitle());
        holder.description.setText(task.getDescription());
        holder.time.setText(task.getTime());
        holder.status.setText(task.getCompleted());
        if (task.getCompleted().equals("Hoàn thành")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.green1));
        } else {
            holder.status.setTextColor(ContextCompat.getColor(context, in.aabhasjindal.otptextview.R.color.red));
        }
        holder.options.setOnClickListener(view -> showPopUpMenu(view, position));



        try {
            date = inputDateFormat.parse(task.getDate());
            outputDateString = dateFormat.format(date);

            String[] items1 = outputDateString.split(" ");

            String day = items1[0];
            if (day.equals("Mon")) {
                day = "Thứ Hai";
            } else if (day.equals("Tue")) {
                day = "Thứ Ba";
            } else if (day.equals("Wed")) {
                day = "Thứ Tư";
            } else if (day.equals("Thu")) {
                day = "Thứ Năm";
            } else if (day.equals("Fri")) {
                day = "Thứ Sáu";
            } else if (day.equals("Sat")) {
                day = "Thứ Bảy";
            } else if (day.equals("Sun")) {
                day = "Chủ Nhật";
            }
            String dd = items1[1];
            String month = items1[2];
            if (month.equals("Jan")) {
                month = "Tháng 1";
            } else if (month.equals("Feb")) {
                month = "Tháng 2";
            } else if (month.equals("Mar")) {
                month = "Tháng 3";
            } else if (month.equals("Apr")) {
                month = "Tháng 4";
            } else if (month.equals("May")) {
                month = "Tháng 5";
            } else if (month.equals("Jun")) {
                month = "Tháng 6";
            } else if (month.equals("Jul")) {
                month = "Tháng 7";
            } else if (month.equals("Aug")) {
                month = "Tháng 8";
            } else if (month.equals("Sep")) {
                month = "Tháng 9";
            } else if (month.equals("Oct")) {
                month = "Tháng 10";
            } else if (month.equals("Nov")) {
                month = "Tháng 11";
            } else if (month.equals("Dec")) {
                month = "Tháng 12";
            }
            String year = items1[3];
            holder.day.setText(day);
            holder.date.setText(dd);
            holder.month.setText(month);
            holder.year.setText(year);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Task selecteTask = taskList.get(position);
                    String taskId = selecteTask.getIdTask();
                    Intent intent = new Intent(context, CreateTaskActivity.class);
                    intent.putExtra("taskId", taskId);
                    Log.d("NoteFragment", "Number of noteId: " + taskId);
                    context.startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void showPopUpMenu(View view, int position) {
        final Task task = taskList.get(position);
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_task, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuDelete) {
                dimBackground(1f, (ViewGroup) view.getRootView());
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle(R.string.delete_confirmation)
                        .setMessage(R.string.sureToDelete)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteTaskFromId(task.getIdTask(), position);
                                removeOverlay(overlay,(ViewGroup) view.getRootView());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                removeOverlay(overlay,(ViewGroup) view.getRootView());
                            }
                        }) ;

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            } else if (item.getItemId() == R.id.menuComplete) {
                dimBackground(1f, (ViewGroup) view.getRootView());
                AlertDialog.Builder completeAlertDialog = new AlertDialog.Builder(context);
                completeAlertDialog.setTitle(R.string.confirmation).setMessage(R.string.sureToMarkAsComplete)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeOverlay(overlay,(ViewGroup) view.getRootView());
                                showCompleteDialog(task.getIdTask(), position, view );
                                dialog.dismiss();

                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                removeOverlay(overlay,(ViewGroup) view.getRootView());
                            }
                        });
                AlertDialog alertDialog = completeAlertDialog.create();
                alertDialog.show();
                return true;
            }


            return false;

        });
        removeOverlay(overlay,(ViewGroup) view.getRootView());
        popupMenu.show();

    }
    public void dimBackground(float alpha, ViewGroup rootView) {
        Log.d("NoteAdapter", "dimBackground - Start");
        overlay = new View(context);
        overlay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        overlay.setBackgroundColor(Color.parseColor("#80000000"));
        rootView.addView(overlay);
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("NoteAdapter", "Overlay Clicked");
                removeOverlay(overlay, rootView);
            }
        });
        Log.d("NoteAdapter", "dimBackground - End");
    }
    public void removeOverlay(View overlay, ViewGroup rootView) {
        rootView.removeView(overlay);
    }


    private void deleteTaskFromId(String taskId, int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");
        DatabaseReference noteRef = FirebaseDatabase.getInstance().getReference()
                .child("Tasks")
                .child(phone)
                .child(taskId);
        noteRef.removeValue();
    }
    public void showCompleteDialog(String taskId, int position, View view1) {
        dimBackground(1f, (ViewGroup) view1.getRootView());
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_completed_theme);
        Button close = dialog.findViewById(R.id.closeButton);
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference()
                .child("Tasks")
                .child(phone)
                .child(taskId).child("completed");
        taskRef.setValue("Hoàn thành");

        dialog.dismiss();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOverlay(overlay,(ViewGroup) view1.getRootView());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }


    public void filter(String text) {
        if (fullTasksList == null) {
            fullTasksList = new ArrayList<>(taskList);
        }
        taskList.clear();
        if (text.trim().isEmpty()) {
            taskList.addAll(fullTasksList);
        } else {
            String query = text.toLowerCase().trim();
            for (Task task : fullTasksList) {
                if (task.getTitle().toLowerCase().contains(query)) {
                    taskList.add(task);
                }else if (task.getDescription().toLowerCase().contains(query)){
                    taskList.add(task);
                }else if (task.getEvent().toLowerCase().contains(query)){
                    taskList.add(task);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        public TextView day;

        public TextView date;

        public TextView month;
        public TextView year;

        public TextView title;

        public TextView description;

        public TextView status;

        public ImageView options;

        public TextView time;
        public TaskViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            day = itemView.findViewById(R.id.day);
            date = itemView.findViewById(R.id.date);
            month = itemView.findViewById(R.id.month);
            year = itemView.findViewById(R.id.year);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            status = itemView.findViewById(R.id.status);
            options = itemView.findViewById(R.id.options);
            time = itemView.findViewById(R.id.time);

        }

    }
}
