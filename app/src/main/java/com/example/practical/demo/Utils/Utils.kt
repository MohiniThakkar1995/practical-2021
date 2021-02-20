package com.example.demomvvm.Utils

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.util.SparseIntArray
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.EditText
import com.example.practical.R

import org.json.JSONObject
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern



object Utils {
//    get parsed error message
    fun getMessageFromResponseObject(context: Context, `object`: JSONObject?): ArrayList<String> {
        val message = ArrayList<String>()

        if (`object` != null && `object`.has("error")) {
            val errors_arr = `object`.optJSONArray("error")
            try {
                for (i in 0 until errors_arr!!.length()) {
                    message.add(errors_arr.optString(i))
                }
            } catch (e: Exception) {

            }

        }
        if (message.size == 0) {
            message.add(context.getString(R.string.str_something_went_wrong))
        }
        return message
    }


}
