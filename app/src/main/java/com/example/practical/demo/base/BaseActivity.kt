package com.example.practical.demomvvm.base

import android.annotation.TargetApi
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.base.BAS.BaseNavigator
import com.example.demomvvm.AppClass
import com.example.practical.R


abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel<*>> : AppCompatActivity(),
    BaseNavigator {
    // TODO
    // this can probably depend on isLoading variable of BaseViewModel,
    // since its going to be common for all the activities
    private var mProgressDialog: Dialog? = null
    var isBackArrow = false

    var permissionListener: setPermissionListener? = null

    // Progress
    var viewDataBinding: T? = null
        private set
    private var mViewModel: V? = null

    /**
     * Override for set view model
     * @return view model instance
     */
    abstract val viewModel: V

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract val bindingVariable: Int

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int


    override fun onCreate(savedInstanceState: Bundle?) {
        // performDependencyInjection()
        super.onCreate(savedInstanceState)
//        val w = window
//        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        performDataBinding()
        setStatusBarGradiant()
        initialization(savedInstanceState)
    }

    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, layoutId)
        this.mViewModel = if (mViewModel == null) viewModel else mViewModel
        viewDataBinding!!.setVariable(bindingVariable, mViewModel)
        viewDataBinding!!.executePendingBindings()
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * hide soft keyboard
     */
    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * All initialization will be done on this method
     * This method execute after the data binding
     */
    abstract fun initialization(savedInstance: Bundle?)

    protected fun getCompatColor(color: Int): Int {

        return ContextCompat.getColor(this, color)
    }


    /**
     * Setting up toolbar accross the app with back arrow
     * @param toolbar Toolbar
     * @param strTitle String
     * @param isBackArrow Boolean
     * @param icon Int
     */
    fun setUpToolbarWithBlackBackArrow(
        toolbar: Toolbar,
        strTitle: String,
        isBackArrow: Boolean, @DrawableRes icon: Int = android.R.drawable.arrow_down_float
    ) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(isBackArrow)
            actionBar.setHomeAsUpIndicator(icon)
        }
    }


    fun setUpToolbarWithStatus(
        toolbar: Toolbar,
        strTitle: String,
        isBackArrow: Boolean, @DrawableRes icon: Int = android.R.drawable.arrow_down_float,
        isOrderStatus: Boolean = false,
        strOrderStatus: String = "",
        statusBackgroundColor: Int = ContextCompat.getColor(this, android.R.color.darker_gray)
    ) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(isBackArrow)
            actionBar.setHomeAsUpIndicator(icon)

        }
    }

    /**
     * setting up toolbar with drawer menu
     * @param toolbar Toolbar
     * @param strTitle String
     */
    fun setUpToolbarWithMenu(
        toolbar: Toolbar,
        strTitle: String,
        strSubTitle: String = "",
        @DrawableRes background: Int = R.color.white,
        @DrawableRes navMenu: Int = android.R.drawable.btn_dropdown,
        textColorInt: Int = ContextCompat.getColor(this, R.color.black),
        isLogo: Boolean = false,
        isRating: Boolean = false,
        isSwitch: Boolean = false,
        isMenuText: Boolean = false,
        menuText: String = ""
    ) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(navMenu)
            toolbar.setBackgroundResource(background)

        }
    }

    /**
     * adds fragment into given container id with/without backStack and animation
     *
     */
    open fun addFragment(
        containerRes: Int,
        fragment: Fragment,
        addToBackStack: Boolean,
        animate: Boolean
    ) {
        var t = supportFragmentManager.beginTransaction()
            .replace(containerRes, fragment, fragment.javaClass.simpleName)
        if (addToBackStack) {
            t = t.addToBackStack(fragment.javaClass.simpleName)
        }
        if (animate) {
            t = t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }
        fragment.retainInstance = true
        t.commitAllowingStateLoss()
    }

    /**
     * set status bar gradient for O@
     * @param activity Activity
     */
    fun setStatusBarGradiant() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getCompatColor(R.color.white)
            //            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        }
    }

    /**
     * On back press handle
     * @param item MenuItem
     * @return Boolean
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    protected fun getFragmentByTag(tag: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(tag)
    }

    /**
     * show loader "Please wait"
     */
    override fun showLoading() {
        hideKeyboard()
        hideLoading()
    }

    /**
     * Stop loader
     */
    override fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            /*if (!EspressoIdlingResource.idlingResource.isIdleNow()) {
                EspressoIdlingResource.decrement() // Set app as idle.
            }*/
            mProgressDialog!!.dismiss()
        }
    }

    /**
     * show short toast message
     * @param msg String
     */
    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun showToastShort(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    override fun onPause() {
        super.onPause()
        SESSION_EXPIRED_RECEIVER.unregister(this)

        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }




    fun requestAppPermissions(
        requestedPermissions: Array<String>,
        requestCode: Int, listener: setPermissionListener
    ) {
        this.permissionListener = listener
        var permissionCheck = PackageManager.PERMISSION_GRANTED
        for (permission in requestedPermissions) {
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission)
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, requestedPermissions, requestCode)
        } else {
            if (permissionListener != null) permissionListener?.onPermissionGranted(requestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (permissionListener != null) permissionListener?.onPermissionGranted(requestCode)
                break
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                if (permissionListener != null) permissionListener?.onPermissionDenied(requestCode)
                break
            } else {
                if (permissionListener != null) {
                    permissionListener?.onPermissionNeverAsk(requestCode)
                    break
                }
            }
        }
    }

    interface setPermissionListener {
        fun onPermissionGranted(requestCode: Int)

        fun onPermissionDenied(requestCode: Int)

        fun onPermissionNeverAsk(requestCode: Int)
    }

    fun showPermissionSettingDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.need_permission)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.app_settings) { dialog, which ->
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:" + packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            startActivity(intent)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
        builder.create().show()
    }

    protected val app: AppClass
        get() {
            return applicationContext as AppClass
        }

    /**
     * set full screen view for splash screen
     */
    fun setFullScreenView() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

    }

    private class SessionExpiredBroadcastReceiver : BroadcastReceiver() {
        private var listener: BaseActivity<*, *>? = null

        override fun onReceive(context: Context, intent: Intent) {
        }

        fun register(listener: BaseActivity<*, *>, context: Context) {
            this.listener = listener
            LocalBroadcastManager.getInstance(context)
                .registerReceiver(this, IntentFilter(SESSION_EXPIRED))
        }

        fun unregister(context: Context) {
            this.listener = null
            LocalBroadcastManager.getInstance(context).unregisterReceiver(this)
        }
    }

    companion object {
        private val SESSION_EXPIRED_RECEIVER = SessionExpiredBroadcastReceiver()
        val SESSION_EXPIRED = "SESSION_EXPIRED"
    }

    override fun onResume() {
        super.onResume()
        SESSION_EXPIRED_RECEIVER.register(this, this)
    }


    override fun attachBaseContext(newBase: Context?) {

//        val mSessionManager = SessionManager.getInstance(newBase!!)
//
//        var language = ""
//        when (mSessionManager.getDataByKey(PreferenceKeys.SELECTED_LANGUAGE)) {
//
//            AppConstants.ENGLISH_LANGUAGE -> {
//
//                language = AppConstants.ENGLISH_LANGUAGE
//            }
//            AppConstants.ARABIC_LANGUAGE -> {
//
//                language = AppConstants.ARABIC_LANGUAGE
//            }
//        }
        super.attachBaseContext(newBase)
    }

}


