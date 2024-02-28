package com.example.noteapp.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.AddPasswordBlockActivity;
import com.example.noteapp.Domain.Note;
import com.example.noteapp.Domain.User;
import com.example.noteapp.Interface.ItemClickListener;
import com.example.noteapp.NoteActivity;
import com.example.noteapp.PasswordBlockActivity;
import com.example.noteapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.commonmark.node.SoftLineBreak;

import java.util.ArrayList;
import java.util.List;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notesList;
    private Context context;
    private List<Note> fullNotesList;
    public View overlay;

    public NoteAdapter(Context context, List<Note> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, parent, false);
        return new NoteViewHolder(view, context);

    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Note note = notesList.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.markwon.setMarkdown(holder.noteTextView, note.getContent());
        holder.dateTextView.setText(note.getDate());
        int noteColor = note.getColor();
        if (noteColor != 0) {
            holder.notelayout.setCardBackgroundColor(noteColor);
        }
        holder.optinenote.setOnClickListener(view -> showPopUpMenu1(view, position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note selectedNote = notesList.get(position);
                String noteId = selectedNote.getNoteId();
                Intent intent = new Intent(context, NoteActivity.class);
                intent.putExtra("noteId", noteId);
                Log.d("NoteFragment", "Number of noteId: " + noteId);
                context.startActivity(intent);
            }
        });
    }
    public void showPopUpMenu1(View view, int position ) {
        final Note note = notesList.get(position);
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_note, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuDeleteNotes) {
                dimBackground(1f, (ViewGroup) view.getRootView());
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.deletenote)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNoteFromId(note.getNoteId(), position);
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
            } else if (item.getItemId() == R.id.menuBlock) {
                AlertDialog.Builder completeAlertDialog = new AlertDialog.Builder(context);
                dimBackground(1f, (ViewGroup) view.getRootView());
                completeAlertDialog.setTitle(R.string.block_confirmation).setMessage(R.string.sureToBlock)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showBlockDialog(note.getNoteId(), position);
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

                AlertDialog alertDialog = completeAlertDialog.create();
                alertDialog.show();
                return true;

            }
            return false;
        });
        popupMenu.show();
    }
    private void deleteNoteFromId(String noteId, int position) {
        notesList.remove(position);
        notifyItemRemoved(position);
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");
        DatabaseReference noteRef = FirebaseDatabase.getInstance().getReference()
                .child("Notes")
                .child(phone)
                .child(noteId);

        noteRef.removeValue();
    }

    private void showBlockDialog(String noteId, int position) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference table_user = database.getReference("User");
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(phone).exists()) {
                    User user = snapshot.child(phone).getValue(User.class);
                    boolean isUserBlock = user.isBlock();

                    DatabaseReference notesRef = database.getReference("Notes").child(phone);
                    notesRef.child(noteId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot noteSnapshot) {
                            if (noteSnapshot.exists()) {
                                boolean isNoteBlock = noteSnapshot.child("block").getValue(Boolean.class);


                                if (!isUserBlock && !isNoteBlock) {
                                    Intent intent;
                                    intent = new Intent(context, AddPasswordBlockActivity.class);
                                    intent.putExtra("noteId", noteId);
                                    intent.putExtra("position", position);
                                    context.startActivity(intent);
                                } else {
                                    Intent intent1;
                                    intent1 = new Intent(context, PasswordBlockActivity.class);
                                    intent1.putExtra("noteId", noteId);
                                    intent1.putExtra("position", position);
                                    context.startActivity(intent1);
                                }

                            } else {
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý lỗi khi đọc dữ liệu
                        }
                    });
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi đọc dữ liệu
            }
        });


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

    @Override
    public int getItemCount() {
        return notesList.size();
    }
    public void filter(String text) {
        if (fullNotesList == null) {
            fullNotesList = new ArrayList<>(notesList);
        }
        notesList.clear();
        if (text.trim().isEmpty()) {
            notesList.addAll(fullNotesList);
        } else {
            String query = text.toLowerCase().trim();
            for (Note note : fullNotesList) {
                if (note.getTitle().toLowerCase().contains(query)) {
                    notesList.add(note);
                }else if (note.getContent().toLowerCase().contains(query)) {
                    notesList.add(note);
                }
            }
        }
        notifyDataSetChanged();
    }
    public static class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView titleTextView;
        public TextView noteTextView;
        public TextView dateTextView;
        public CardView notelayout;
        private Context mContext;

        public Markwon markwon;
        private ItemClickListener itemClickListener;
        public ImageView optinenote;
        public NoteViewHolder(View itemView, Context context) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.textviewtitlenote);
            noteTextView = itemView.findViewById(R.id.textviewnote);
            dateTextView = itemView.findViewById(R.id.datenote);
            notelayout = itemView.findViewById(R.id.notelayout);
            optinenote = itemView.findViewById(R.id.optionnote);
            mContext = context;
            markwon = Markwon.builder(context)
                    .usePlugin(StrikethroughPlugin.create())
                    .usePlugin(TaskListPlugin.create(
                            ResourcesCompat.getColor(context.getResources(), R.color.primary, context.getTheme()),
                            ResourcesCompat.getColor(context.getResources(), R.color.primary, context.getTheme()),
                            ResourcesCompat.getColor(context.getResources(), R.color.background, context.getTheme())
                    ))
                    .usePlugin(new AbstractMarkwonPlugin() {
                        @Override
                        public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
                            super.configureVisitor(builder);
                            builder.on(SoftLineBreak.class, (visitor, _node) -> visitor.forceNewLine());
                        }
                    })
                    .build();
            itemView.setOnClickListener(this);
        }
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onlick(v, getAdapterPosition(), false);
            }
        }
    }

}

