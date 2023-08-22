package com.example.widgetsample

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class MyWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        // this is where we receive an intent broadcast
        val action = intent!!.action ?: ""

        if (context != null && action == "increase") {
            // update preferences value
            val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            prefs.edit().putString(
                "widgetText",
                ((prefs.getString("widgetText", "0") ?: "0").toInt() + 1).toString()
            ).apply()
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    // this is where we create such an intent
    private fun pendingIntent(
        context: Context?,
        action: String
    ): PendingIntent? {
        val  intent =  Intent(context, javaClass)
        intent.action = action

        // return the pending intent
        return  PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // get the widget text from shared preferences
        val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

        val widgetText = prefs.getString("widgetText", "0")
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.my_widget)
        views.setTextViewText(R.id.appwidget_text, widgetText)

        // launch a pending intent to increase the value saved in shared preference
        views.setOnClickPendingIntent(R.id.button, pendingIntent(context, "increase"))

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    // update all widgets
    private fun updateWidgets(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, javaClass))
        // update ever widget
        ids.forEach { id -> updateAppWidget(context, manager, id) }
    }

}


