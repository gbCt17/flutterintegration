package com.example.clevertap_flutter_integration

import android.app.NotificationManager
import io.flutter.embedding.android.FlutterActivity
import android.content.Context
import android.os.Bundle
import com.clevertap.android.sdk.ActivityLifecycleCallback
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener
import io.flutter.app.FlutterApplication
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.PluginRegistrantCallback
//import io.flutter.plugins.firebase.messaging.FlutterFirebaseMessagingBackgroundService
import io.flutter.view.FlutterMain
import java.util.*
import android.util.Log

import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.clevertap.android.pushtemplates.PTConstants
import com.clevertap.android.sdk.CTInboxListener
import com.clevertap.android.sdk.CTInboxStyleConfig
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    //Code -----------------------
    private val CHANNEL = "your_channel_name"
    //Code -----------------------
//    var cleverTapDefaultInstance: CleverTapAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext)
    }

    //Code -----------------------
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "sendData") {
                val data = call.argument<String>("data")
                //Register Activity life cycle method From Clevertap SDK
                ActivityLifecycleCallback.register(application)
                saveFlagToSharedPreferences(this,"isLoggedIn",true)
                showToast(this, "This is a toast message--->"+data)
                // Process data here
                result.success(data)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun getDataFromNative(): String {
        // Perform platform-specific operations to fetch the data
        return "Data from Native"
    }

    fun saveFlagToSharedPreferences(context: Context, flagKey: String, flagValue: Boolean) {
        val sharedPreferences = context.getSharedPreferences("log_flag", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(flagKey, flagValue)
        editor.apply()
    }
    //Code -----------------------


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // CleverTapAPI.getDefaultInstance(this)?.pushNotificationClickedEvent(intent!!.extras)
//            cleverTapDefaultInstance?.pushNotificationClickedEvent(intent!!.extras)
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            NotificationUtils.dismissNotification(intent, applicationContext)
//        }

    }
}

object NotificationUtils {

    //Require to close notification on action button click
    fun dismissNotification(intent: Intent?, applicationContext: Context){
        intent?.extras?.apply {
            var autoCancel = true
            var notificationId = -1

            getString("actionId")?.let {
                Log.d("ACTION_ID", it)
                autoCancel = getBoolean("autoCancel", true)
                notificationId = getInt("notificationId", -1)
            }
            /**
             * If using InputBox template, add ptDismissOnClick flag to not dismiss notification
             * if pt_dismiss_on_click is false in InputBox template payload. Alternatively if normal
             * notification is raised then we dismiss notification.
             */
            val ptDismissOnClick = intent.extras!!.getString(PTConstants.PT_DISMISS_ON_CLICK,"")

            if (autoCancel && notificationId > -1 && ptDismissOnClick.isNullOrEmpty()) {
                val notifyMgr: NotificationManager =
                    applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notifyMgr.cancel(notificationId)
            }
        }
    }


}
