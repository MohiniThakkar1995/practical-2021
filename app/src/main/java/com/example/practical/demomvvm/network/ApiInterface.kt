 package com.example.practical.demomvvm.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*


interface ApiInterface {

    @POST
    fun APICall(@Url endPoint: String, @HeaderMap hashMap: HashMap<String, String>, @Body requestClass: Any): Call<ResponseBody>

    @FormUrlEncoded
    @POST
    fun APICall(@Url endPoint: String, @HeaderMap hashMap: HashMap<String, String>, @FieldMap fields: HashMap<String, String>): Call<ResponseBody>

    @Multipart
    @POST
    fun APIMultipartCall(@Url endPoint: String, @HeaderMap hashMap: HashMap<String, String>, @PartMap fields: HashMap<String, RequestBody>): Call<ResponseBody>

    @GET
    fun APICall(@Url endPoint: String, @HeaderMap hashMap: HashMap<String, String>): Call<ResponseBody>

    @GET
    fun getMethod(@Url endpoint: String): Call<ResponseBody>

}
