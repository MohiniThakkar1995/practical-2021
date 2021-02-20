package com.example.practical.demomvvm.base


import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.base.BAS.BaseNavigator
import com.demotoro.utils.CommonUtils
import com.example.demomvvm.AppClass
import com.example.practical.R
import com.google.android.material.snackbar.Snackbar


/**
 * Created by ak on 09/07/18.
 */

@Suppress("UNCHECKED_CAST")
abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel<*>> : Fragment(), BaseNavigator {

    var baseActivity: BaseActivity<*, *>? = null
    var mActivity: Activity? = null
    private var snackbar: Snackbar? = null
        private set
    lateinit var mContext: Context
    var viewDataBinding: T? = null
        private set
    private var mViewModel: V? = null
    private var mRootView: View? = null

    var permissionListener: setPermissionListener? = null
    // Progress


    /**
     * Override for set view model
     *
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

    var imei = ""
    var isBuzz = false
    var isLight = false
    lateinit var bluetoothManager: BluetoothManager

    override fun onCreate(savedInstanceState: Bundle?) {
        //  performDependencyInjection()
        super.onCreate(savedInstanceState)
        mViewModel = viewModel
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (viewDataBinding == null) {
            viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
            mRootView = viewDataBinding!!.root
            initialization(savedInstanceState)
        }
        onSetupToolbar()
        return mRootView
    }

    open fun onSetupToolbar() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding!!.setVariable(bindingVariable, mViewModel)
        viewDataBinding!!.executePendingBindings()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*, *>) {
            val activity = context
            this.baseActivity = activity
            mActivity = getActivity()
            this.mContext = activity

        }
    }

    fun onBackPressed(): Boolean {
        return false
    }

    interface setPermissionListener {
        fun onPermissionGranted(requestCode: Int)

        fun onPermissionDenied(requestCode: Int)

        fun onPermissionNeverAsk(requestCode: Int)
    }

    fun requestAppPermissions(
        requestedPermissions: Array<String>,
        requestCode: Int,
        listener: setPermissionListener
    ) {
        this.permissionListener = listener
        var permissionCheck = PackageManager.PERMISSION_GRANTED
        for (permission in requestedPermissions) {
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(activity!!, permission)
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, requestedPermissions, requestCode)
        } else {
            if (permissionListener != null) permissionListener!!.onPermissionGranted(requestCode)
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
                    mContext,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (permissionListener != null) permissionListener?.onPermissionGranted(requestCode)
                break
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity!!,
                    permission
                )
            ) {
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

    fun showPermissionSettingDialog(message: String) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(R.string.need_permission)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.app_settings) { dialog, which ->
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:" + activity!!.packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            startActivity(intent)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
        builder.create().show()
    }

    fun <T : ViewDataBinding> getActivityBinding(): T = baseActivity?.viewDataBinding as T


    override fun onDetach() {
        super.onDetach()
        baseActivity = null
    }

    /**
     * Hide soft keyboard from screen
     */
    fun hideKeyboard() {
        if (baseActivity != null) {
            baseActivity!!.hideKeyboard()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if (snackbar != null && snackbar!!.isShown) snackbar!!.dismiss()

    }


    interface Callback {

        fun onFragmentAttached()

        fun onFragmentDetached(tag: String)
    }

    abstract fun initialization(savedInstance: Bundle?)

    /**
     * @see BaseActivity.showLoading
     */
    override fun showLoading() {
        hideKeyboard()
        hideLoading()
    }



    /**
     * @see BaseActivity.hideLoading
     */
    override fun hideLoading() {

    }

    /**
     * @see BaseActivity.showToast
     */
    override fun showToast(msg: String) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show()
    }

    fun showToastShort(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }



    fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity!!.window.statusBarColor = color
        }
    }

    override fun finishActivity() {
        activity!!.finish()
    }

    override fun gotoLogin() {
        baseActivity?.finish()
        /* val intent = LoginActivity.newIntent(mContext).apply {

             flags = Intent.FLAG_ACTIVITY_NEW_TASK
             flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
             flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
         }

         startActivity(intent)*/

    }

    fun hasGPSDevice(context: Context): Boolean {
        val mgr = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager ?: return false
        val providers = mgr.allProviders ?: return false
        return providers.contains(LocationManager.GPS_PROVIDER)
    }

    override fun onPause() {
        super.onPause()

    }

    protected val app: AppClass
        get() {
            return context?.applicationContext as AppClass
        }

}
