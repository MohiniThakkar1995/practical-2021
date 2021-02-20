 package com.example.practical.demomvvm.ui.authentication

import com.base.BAS.BaseNavigator


 interface LoginNavigator : BaseNavigator {
    fun openCompleteProfileActivity()
    fun openMainActivity()
    fun openMobileVerificationActivity(userInfo: FAQ)

}