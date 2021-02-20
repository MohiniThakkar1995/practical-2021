package com.example.practical.demomvvm.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Image(
    val imageUrl: String = ""
) : Serializable