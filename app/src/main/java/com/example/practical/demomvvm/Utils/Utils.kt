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
import com.example.demomvvm.R

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

/**
 * Created by RajeshKushvaha on 24-05-17
 */

object Utils {

    private val ORIENTATIONS = SparseIntArray()

    var filterPreventWhiteSpaces: InputFilter =
        InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (Character.isWhitespace(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }

    private fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } catch (e: Exception) {
            Log.e("Tag", "getRealPathFromURI Exception : $e")
            return ""
        } finally {
            cursor?.close()
        }
    }

    fun dipToPixels(context: Activity, dipValue: Float): Int {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics).toInt()
    }

    fun getDurationInSecVideo(context: Context, file: File?): Long {
        if (file == null || !file.exists())
            return 0

        val retriever = MediaMetadataRetriever()
        //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(context, Uri.fromFile(file))
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        if (TextUtils.isEmpty(time))
            return 0
        val timeInMillisec = java.lang.Long.parseLong(time)

        retriever.release()
        return TimeUnit.MILLISECONDS.toSeconds(timeInMillisec)
    }

    fun getFileSizeInMB(file: File?): Long {
        if (file == null || !file.exists())
            return 0

        val fileSizeInBytes = file.length()
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        val fileSizeInKB = fileSizeInBytes / 1024
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        return fileSizeInKB / 1024
    }

    fun preventDoubleClick(view: View?) {
        view?.isEnabled = false
        Handler().postDelayed({ view?.isEnabled = true }, 2000)
    }

    /*fun preventDoubleClick(view: View) {
        // preventing double, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
    }*/

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

    //*****************************************************************
    fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{6,15})"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    /**
     * Check if the device is connected to internet
     *
     * @return true if connected to internet false otherwise.
     */
    fun isConnectedToInternet(mContext: Context): Boolean {
        val connectivity =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivity.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isAvailable && activeNetwork.isConnected
    }

    fun showSoftKeyboard(editText: EditText, mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideSoftKeyboard(mContext: Context) {
        try {
            val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow((mContext as Activity).currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun getMinutesFromSeconds(seconds: String): String {
        return (seconds.toInt() / 60).toString()
    }

    /**
     * We have to return decimal point count and double value
     */
    private fun setDoubleDecimalPoints(decimalPoints: Int, value: Double): String {
        return String.format("%." + decimalPoints + "f", value)
    }


    /*  fun openCustomTabs(activity: Activity, uri: String) {
          // create an intent builder
          val intentBuilder = CustomTabsIntent.Builder()

          // set start and exit animations
          intentBuilder.setStartAnimations(activity, R.anim.slide_in_right, R.anim.slide_out_left)
          intentBuilder.setExitAnimations(
              activity,
              android.R.anim.slide_in_left,
              android.R.anim.slide_out_right
          )

          // build custom tabs intent
          val customTabsIntent = intentBuilder.build()
          try {
              // launch the url

              customTabsIntent.launchUrl(activity, Uri.parse(uri))
          } catch (e: ActivityNotFoundException) {
              e.printStackTrace()
          }

      }
  */

    fun clearCookies(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(
                "TAG",
                "Using clearCookies code for API >=" + Build.VERSION_CODES.LOLLIPOP_MR1.toString()
            )
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        } else {
            Log.d(
                "TAG",
                "Using clearCookies code for API <" + Build.VERSION_CODES.LOLLIPOP_MR1.toString()
            )
            val cookieSyncMngr = CookieSyncManager.createInstance(context)
            cookieSyncMngr.startSync()
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookie()
            cookieManager.removeSessionCookie()
            cookieSyncMngr.stopSync()
            cookieSyncMngr.sync()
        }
    }

    fun fileExists(URLName: String): Boolean {
        try {
            HttpURLConnection.setFollowRedirects(false)
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            val con = URL(URLName).openConnection() as HttpURLConnection
            con.requestMethod = "HEAD"
            return con.responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    /*fun getNewPathFromUri(mContext: Context, data: Intent): String? {
        var selectedAudioFile: String? = ""
        val selectedAudioUri = data.data
        var filename: String
        val fileType: String
        if ("content".equals(data.data!!.scheme!!, ignoreCase = true)) {
            // Return the remote address
            if (FileUtility.isGooglePhotosUri(data.data)) {
                val returnUri = data.data
                val returnCursor =
                    mContext.contentResolver.query(returnUri!!, null, null, null, null)
                val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
                returnCursor.moveToFirst()
                filename = returnCursor.getString(nameIndex)
                val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))

                val cR = mContext.contentResolver
                val mime = MimeTypeMap.getSingleton()
                val type = mime.getExtensionFromMimeType(cR.getType(data.data!!))
                if (type!!.contains("pdf") || type.contains("doc") || type.contains("docx")) {
                    filename = "$filename.$type"
                    fileType = if (type.contains("pdf")) "PDF" else "DOC"
                }
                val sourcePath = FileUtility.getExternalFilesDir(null, mContext).toString()
                try {
                    selectedAudioFile = FileUtility.copyFileStream(
                        File(sourcePath + "/" + filename),
                        data.data,
                        mContext
                    ).getAbsolutePath()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                if (data.data!!.path!!.contains("/storage")) {
                    selectedAudioFile =
                        data.data!!.path!!.substring(data.data!!.path!!.indexOf("storage") - 1)
                } else {
                    var cursor: Cursor? = null
                    val column = "_data"
                    val projection = arrayOf(column)

                    try {
                        cursor = mContext.contentResolver.query(
                            data.data!!,
                            projection,
                            null,
                            null,
                            null
                        )
                        if (cursor != null && cursor.moveToFirst()) {
                            val column_index = cursor.getColumnIndexOrThrow(column)
                            selectedAudioFile = cursor.getString(column_index)
                        }
                    } finally {
                        cursor?.close()
                    }
                }
            }
        } else {
            selectedAudioFile = selectedAudioUri!!.path
        }
        return selectedAudioFile
    }*/

    fun getQueryParams(url: String): Map<String, List<String>> {
        try {
            val params = HashMap<String, List<String>>()
            val urlParts = url.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (urlParts.size > 1) {
                val query = urlParts[1]
                for (param in query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    val pair =
                        param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val key = URLDecoder.decode(pair[0], "UTF-8")
                    var value = ""
                    if (pair.size > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8")
                    }

                    var values: MutableList<String>? = params[key] as MutableList<String>?
                    if (values == null) {
                        values = ArrayList()
                        params[key] = values
                    }
                    values.add(value)
                }
            }

            return params
        } catch (ex: UnsupportedEncodingException) {
            throw AssertionError(ex)
        }

    }


    /**App Running or not */
    fun isAppRunning(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val procInfos = activityManager.runningAppProcesses
        if (procInfos != null) {
            for (processInfo in procInfos) {
                if (processInfo.processName == packageName) {
                    return true
                }
            }
        }
        return false
    }


    fun foregrounded(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE
    }

    fun isMyServiceRunning(activity: Activity, serviceClass: Class<*>): Boolean {
        val manager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
