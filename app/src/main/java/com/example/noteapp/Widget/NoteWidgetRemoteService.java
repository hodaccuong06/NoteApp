package com.example.noteapp.Widget;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.core.content.res.ResourcesCompat;

import com.example.noteapp.Domain.Note;
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

public class NoteWidgetRemoteService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new NoteRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    public static class NoteRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private List<Note> notesList;
        private Markwon markwon;
        DatabaseReference noteReference;
        SharedPreferences sharedPreferences;
        String phone = "";

        public NoteRemoteViewsFactory(Context context, Intent intent) {
            this.context = context;
        }

        @Override
        public void onCreate() {
            sharedPreferences = context.getSharedPreferences("TaiKhoan", MODE_PRIVATE);
            phone = sharedPreferences.getString("phone", "");
            Log.d("Widget", "PhoneWidgetFactory" + phone);
            noteReference = FirebaseDatabase.getInstance().getReference().child("Notes").child(phone);
            markwon = Markwon.builder(context)
                    .usePlugin(StrikethroughPlugin.create())
                    .usePlugin(TaskListPlugin.create(
                            ResourcesCompat.getColor(context.getResources(), R.color.primary, context.getTheme()),
                            ResourcesCompat.getColor(context.getResources(), R.color.primary, context.getTheme()),
                            ResourcesCompat.getColor(context.getResources(), R.color.background, context.getTheme())
                    ))
                    .usePlugin(new AbstractMarkwonPlugin() {
                        @Override
                        public void configureVisitor(MarkwonVisitor.Builder builder) {
                            super.configureVisitor(builder);
                            builder.on(SoftLineBreak.class, (visitor, _node) -> visitor.forceNewLine());
                        }
                    })
                    .build();
            notesList = new ArrayList<>();
            noteReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
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
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, NoteWidget.class));
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.notes_widget_list);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("Widget", "loadNote:onCancelled", databaseError.toException());
                }
            });
        }

        @Override
        public void onDataSetChanged() {

        }


        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return notesList.size();
        }
        @Override
        public RemoteViews getViewAt(int position) {

                Note note = notesList.get(position);

                CharSequence body = markwon.toMarkdown(note.getContent());

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notes_widget_item);
                remoteViews.setTextViewText(R.id.widget_note_title, note.getTitle());
                remoteViews.setTextViewText(R.id.widget_note_body, body);

                Intent fillInIntent = new Intent();
                fillInIntent.putExtra(NoteWidget.EXTRA_ITEM, note.getNoteId());
                Log.d("fillInIntent", "fillInIntent"   +fillInIntent);
                remoteViews.setOnClickFillInIntent(R.id.widget_note_bg, fillInIntent);
                return remoteViews;
        }

        @SuppressLint("RemoteViewLayout")
        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(context.getPackageName(), R.layout.notes_widget_item);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if (position >= 0 && position < notesList.size()) {
                String noteId = notesList.get(position).getNoteId();
                return noteId.hashCode();
            }

            return position;

        }


        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
