 package com.example.practical.demomvvm.ui.authentication

import android.text.TextUtils
import androidx.databinding.ObservableField
import com.example.demomvvm.AppClass
import com.example.demomvvm.R
import com.google.gson.Gson
import com.showaikh.driver.network.API_END_POINTS
import com.showaikh.driver.network.listeners.RetrofitResponseListener
import com.showaikh.driver.ui.base.BaseViewModel

import org.json.JSONObject
import java.util.*

class LoginViewModel(app: AppClass) : BaseViewModel<LoginNavigator>(app) {

    var mobileNumber = ObservableField<String>("")
    var password = ObservableField<String>("")
    val TAG = "LoginViewModel"
    var userInfo: FAQ? = null

    fun validate(countryCode: String) {

        if (TextUtils.isEmpty(mobileNumber.get())) {
            navigator!!.showSnackBar(appContext.getString(R.string.err_empty_mobile_number), true)
        } else {
            apiLogin(countryCode)
        }
    }

    private fun apiLogin(countryCode: String) {

        val params = HashMap<String, String>()

       /* params[API_CONSTANTS.MOBILE_NO] = mobileNumber.get()!!
        params[API_CONSTANTS.COUNTRY_CODE] = countryCode
        params[API_CONSTANTS.PASSWORD] = password.get()!!
        params[API_CONSTANTS.DEVICE_ID] = session.getFCMToken()
        params[API_CONSTANTS.DEVICE_TYPE] = Constants.ANDROID
*/
        NetworkCall.with(appContext)
            .setRequestParams(params)
            .setEndPoint(API_END_POINTS.SIGN_IN)
            .setResponseListener(object : RetrofitResponseListener {

                override fun onPreExecute() {
                    navigator?.showLoading()
                }

                override fun onSuccess(statusCode: Int, jsonObject: JSONObject, response: String) {

                    navigator?.hideLoading()

//                    Logger.e(TAG, "Success Message : ${jsonObject.optString("message")}")

                    userInfo = Gson().fromJson<FAQ>(jsonObject.optString("user").toString(), FAQ::class.java)

                }

                override fun onError(statusCode: Int, messages: ArrayList<String>) {
                    navigator?.hideLoading()
                    navigator!!.showToast(messages[0])
                }

            }).makeCall()

    }

 /*   fun getFCMToken() {

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            //            session.setValueFromKey(PreferenceKeys.KEY_FCM_TOKEN, token)
            session.setFCMToken(instanceIdResult.token)
        }
    }*/



}