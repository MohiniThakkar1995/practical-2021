package com.example.demomvvm

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.example.practical.demomvvm.network.listeners.DefaultActionPerformer
import java.util.*

class AppClass : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
//        initialize multidex
        MultiDex.install(this)
//        NetworkCall(applicationContext).setCustomBaseURL(Constants.BASE_URL)
        NetworkCall.setActionPerformer(object : DefaultActionPerformer {
            override fun onActionPerform(
                headers: HashMap<String, String>,
                params: HashMap<String, String>
            ) {

            }
        })
    }

  /*  *//**
     * For dynamic language change
     * @param base - default language
     *//*
    override fun attachBaseContext(base: Context) { (base)
    }*/
}