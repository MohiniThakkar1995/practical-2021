 package com.example.practical.demomvvm.utils

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.appcompat.app.AlertDialog

import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by ak on 07/07/18.
 */

object CommonUtils {
    @SuppressLint("all")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun isEmailValid(email: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)
        return matcher.matches()
    }

    @Throws(IOException::class)
    fun loadJSONFromAsset(context: Context, jsonFileName: String): String {

        val manager = context.assets
        val `is` = manager.open(jsonFileName)

        val size = `is`.available()
        val buffer = ByteArray(size)
        `is`.read(buffer)
        `is`.close()

        return String(buffer, Charset.forName("UTF-8"))
    }





    fun parseErrorResponse(json: String?): String? {

        if (json.isNullOrBlank()) {
            return "Something went wrong"
        }

        val obj = JSONObject(json)

        val status = obj.optString("Status")

        if (status.isNotBlank()) {

            when (status) {

                "Errors" -> {

                    val error = obj.optJSONArray("Errors")

                    if (error != null) {

                        return error.optJSONObject(0).optString("Message")
                    }
                }
            }
        }

        return obj.optString("error_description", obj.optString("Message", "Something went wrong"))
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        var width = drawable.getIntrinsicWidth()
        width = if (width > 0) width else 1
        var height = drawable.getIntrinsicHeight()
        height = if (height > 0) height else 1

        var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        var canvas = Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }




    fun showSingleChoiceAlertDialog(
        context: Context,
        title: String, @ArrayRes list: Int,
        selection: Int,
        onSelection: (position: Int, value: String) -> Unit
    ) {

        val list = context.resources.getStringArray(list)
        showSingleChoiceAlertDialog(context, title, list, selection, onSelection)
    }

    fun showSingleChoiceAlertDialog(
        context: Context,
        title: String,
        list: Array<String>,
        selection: Int,
        onSelection: (position: Int, value: String) -> Unit
    ) {

        val mBuilder = AlertDialog.Builder(context)

        mBuilder.setTitle(title)
        mBuilder.setSingleChoiceItems(
            list, selection
        ) { p0, p1 -> onSelection(p1, list[p1]);p0.dismiss() }


        mBuilder.show()
    }



    /**
     * Creates a marker from drawable file
     * @param drawable Drawable
     * @return BitmapDescriptor
     *//*
    public fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    */
    /**
     * Get Bitmap image with assets image and colorPrimary Background
     * @param bitmap Bitmap
     * @return Bitmap
     *//*
    fun getBitmapFromView(context: Context, bitmap: Bitmap): Bitmap {
        val view = View.inflate(context, R.layout.layout_marker, null)
        view.ivMarker.setImageBitmap(bitmap)
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }

    */
    /**
     * Get Bitmap image with assets image and colorPrimary Background
     * @param bitmap Bitmap
     * @return Bitmap
     *//*
    fun getBitmapFromView(context: Context): Bitmap {
        val view = View.inflate(context, R.layout.layout_marker_draggable, null)
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }*/


    fun toCamelCase(s: String): String {
        if (s.isEmpty()) {
            return s;
        }
        val parts = s.split(" ");
        var camelCaseString = "";
        for (part in parts) {
            camelCaseString = camelCaseString + toProperCase(part) + " ";
        }
        return camelCaseString.trim();
    }

    fun toProperCase(s: String): String {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }

    @JvmStatic
    fun preventDoubleClick(view: View) {
        view.isEnabled = false;

        android.os.Handler().postDelayed(Runnable {
            view.isEnabled = true;
        }, 200)
    }

    fun getSHA1(text: String): Pair<String, ByteArray> {
        var textSwapped = text.reversed()
        textSwapped = swapChars(swapChars(textSwapped, 1, 13), 2, 14)
        val md = MessageDigest.getInstance("SHA-1");
        val textBytes = textSwapped.toByteArray(Charsets.ISO_8859_1);
        md.update(textBytes, 0, textBytes.size);
        val sha1hash = md.digest();
        return Pair(sha1hash.joinToString("") { "%02x".format(it) }, sha1hash);
    }

    fun swapChars(str: String, lIdx: Int, rIdx: Int): String {
        val sb = StringBuilder(str);
        val l = sb[lIdx]
        val r = sb[rIdx];
        sb.setCharAt(lIdx, r);
        sb.setCharAt(rIdx, l);
        return sb.toString();
    }


    fun bytesToHex(hashInBytes: ByteArray): String {
        val sb = StringBuilder();
        hashInBytes.map {
            sb.append(String.format("%02x", it));
        }
        return sb.toString();
    }
}// This utility class is not publicly instantiable
