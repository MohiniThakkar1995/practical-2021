package com.example.practical.demomvvm.users

import androidx.lifecycle.MutableLiveData
import com.base.BAS.BaseNavigator
import com.example.demomvvm.AppClass
import com.example.demomvvm.USERS
import com.example.practical.demomvvm.base.BaseViewModel
import com.example.practical.demomvvm.network.API_END_POINTS
import com.example.practical.demomvvm.network.listeners.RetrofitResponseListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import org.json.JSONObject

class UsersViewModel(app: AppClass) : BaseViewModel<BaseNavigator>(app) {

    var mArrUserList = ArrayList<USERS?>()
    var userList = MutableLiveData<ArrayList<USERS?>>()

    fun apiGetUsersList() {

        val params: HashMap<String, String> = HashMap()
        params["start"] = "0"
        params["limit"] = "100"

        NetworkCall.with(appContext)
            .setRequestParams(params)
            .setEndPoint(API_END_POINTS.GET_USER_LIST)
            .setResponseListener(object : RetrofitResponseListener {

                override fun onPreExecute() {
                    navigator?.showLoading()
                }

                override fun onSuccess(statusCode: Int, jsonObject: JSONObject, response: String) {

                    navigator?.hideLoading()
//                    userInfo = Gson().fromJson<UserInfo>(jsonObject.optString("user").toString(), UserInfo::class.java)
                    /*   mMyEarning = Gson().fromJson<MyEarnings>(
                           jsonObject.toString(),
                           object : TypeToken<MyEarnings>() {}.type
                       )*/
                    mArrUserList = Gson().fromJson(
                        jsonObject.optJSONArray("users")!!.toString(),
                        object : TypeToken<ArrayList<FAQ>>() {}.type
                    )

                    userList.value = mArrUserList

                }

                override fun onError(statusCode: Int, messages: ArrayList<String>) {
                    navigator?.hideLoading()
                    navigator!!.showToast(messages[0])
                }

            }).makeCall()

    }

}