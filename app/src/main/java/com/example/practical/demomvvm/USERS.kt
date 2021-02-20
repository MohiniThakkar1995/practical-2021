package com.example.demomvvm

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class USERS(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("image")
    val image: String = "",
    @SerializedName("items")
    val items: ArrayList<String> = ArrayList()
):Serializable