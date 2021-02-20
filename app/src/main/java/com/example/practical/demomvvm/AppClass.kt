package com.example.demomvvm

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.showaikh.driver.network.listeners.DefaultActionPerformer
import java.util.*

class AppClass : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
//        NetworkCall(applicationContext).setCustomBaseURL(Constants.BASE_URL)
        NetworkCall.setActionPerformer(object : DefaultActionPerformer {
            override fun onActionPerform(
                headers: HashMap<String, String>,
                params: HashMap<String, String>
            ) {
                headers["Authorization"] = "Bearer EbGXoLtTU7ph6SWkmfVWBhfSh4MeCzKcntd6UFgGzbrE"
                headers["Accept"] = "application/json"
            }
        })
    }
    // endregion
    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'"
    }
    /**
     * For dynamic language change
     * @param base - default language
     */
    override fun attachBaseContext(base: Context) {
//        var language = "en"
        var language = ""
        super.attachBaseContext(base)
    }
}