package com.example.practical.listener

import org.json.JSONObject

interface RetrofitResponseListener {
    fun onPreExecute()
    fun onSuccess(statusCode: Int, jsonObject: JSONObject, response: String)
    fun onError(statusCode: Int, messages: ArrayList<String>)
}