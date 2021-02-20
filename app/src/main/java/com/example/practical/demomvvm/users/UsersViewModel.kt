package com.example.practical.demomvvm.users

import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.base.BAS.BaseNavigator
import com.example.demomvvm.AppClass
import com.example.demomvvm.Users
import com.example.practical.demomvvm.base.BaseViewModel
import com.example.practical.demomvvm.network.API_END_POINTS
import com.example.practical.demomvvm.network.listeners.RetrofitResponseListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import org.json.JSONObject

class UsersViewModel(app: AppClass) : BaseViewModel<BaseNavigator>(app) {

    var mArrUserList = ArrayList<Users?>()
    var userList = MutableLiveData<ArrayList<Users?>>()
    var start = ObservableInt(0)
    var isLast = ObservableInt(1)

    //    call userlist api
    fun apiGetUsersList() {

        val params: HashMap<String, String> = HashMap()
        params["offset"] = start.get().toString()
        params["limit"] = "10"

        NetworkCall.with(appContext)
            .setCustomBaseURL("http://sd2-hiring.herokuapp.com/")
            .setRequestType(1)
//            .setRequestParams(params)
            .setEndPoint(API_END_POINTS.GET_USER_LIST)
            .setResponseListener(object : RetrofitResponseListener {

                override fun onPreExecute() {
                    navigator?.showLoading()
                }

                override fun onSuccess(statusCode: Int, jsonObject: JSONObject, response: String) {

                    navigator?.hideLoading()
                    if (start.get() == 0) {
                        mArrUserList.clear()
                        try{
                            userList.value!!.clear()
                        }catch (e : Exception){}
                    }
                    val temp: ArrayList<Users> = Gson().fromJson(
                        jsonObject.optJSONArray("users")!!.toString(),
                        object : TypeToken<ArrayList<Users>>() {}.type
                    )

                    var isLastFromList: Int = 0
                    isLastFromList = if (jsonObject.optBoolean("has_more")) 0 else 1
                    isLast.set(isLastFromList)

                    mArrUserList.addAll(temp)
                    userList.value = mArrUserList
                }

                override fun onError(statusCode: Int, messages: ArrayList<String>) {
                    navigator?.hideLoading()
                    navigator!!.showToast(messages[0])
                }

            }).makeCall()

    }

}