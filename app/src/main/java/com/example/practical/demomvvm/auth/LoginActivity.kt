 package com.example.practical.demomvvm.ui.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.demomvvm.BR
import com.example.demomvvm.R
import com.example.demomvvm.ViewModelProviderFactory
import com.example.demomvvm.databinding.ActivityLoginBinding
import com.showaikh.driver.ui.base.BaseActivity


class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(), View.OnClickListener, LoginNavigator {
    override val viewModel: LoginViewModel
        get() = ViewModelProvider(this, ViewModelProviderFactory<LoginViewModel>(LoginViewModel(app))).get(LoginViewModel::class.java)
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_login
//    var isActive = false
    override fun initialization(savedInstance: Bundle?) {
        viewModel.navigator = this
//        viewModel.getFCMToken()
//        viewModel.getToken()
        setListener()
    }
    private fun setListener() {
        viewDataBinding!!.btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }

    override fun openCompleteProfileActivity() {
    }

    override fun openMainActivity() {
    }

    override fun openMobileVerificationActivity(userInfo: FAQ) {
        TODO("Not yet implemented")
    }

    override fun getRoomData() {
        TODO("Not yet implemented")
    }

    override fun showSnackBar(message: String, isError: Boolean) {
        TODO("Not yet implemented")
    }

    override fun showAlert(title: String, msg: String?) {
        TODO("Not yet implemented")
    }

    override fun gotoLogin() {
        TODO("Not yet implemented")
    }


    override fun onResume() {
        super.onResume()
        isActive = true
    }

    override fun onStop() {
        super.onStop()
        isActive = false
    }

    override fun onDestroy() {
        super.onDestroy()
        isActive = false
    }

    companion object {

        var isActive = false

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, LoginActivity::class.java)
            return intent
        }
    }
}
