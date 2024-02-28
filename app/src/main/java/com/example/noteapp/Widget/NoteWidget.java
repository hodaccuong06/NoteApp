package com.example.noteapp.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.example.noteapp.R;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidget extends AppWidgetProvider {
    public static final String EXTRA_ITEM = "com.example.noteapp.EXTRA_ITEM";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, NoteWidgetRemoteService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.note_widget);
            rv.setRemoteAdapter(R.id.notes_widget_list, intent);
            rv.setEmptyView(R.id.notes_widget_list, R.id.empty_view);
            appWidgetManager.updateAppWidget(appWidgetId, rv);


        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
    @Override
    public void onAppWidgetOptionsChanged(
            Context context,
            AppWidgetManager appWidgetManager,
            int appWidgetId,
            Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}