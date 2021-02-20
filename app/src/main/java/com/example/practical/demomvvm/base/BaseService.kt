package com.example.practical.demomvvm.base

import android.app.Service
import com.base.BAS.BaseNavigator
import com.example.demomvvm.AppClass

abstract class BaseService<V: BaseViewModel<*>> : Service(), BaseNavigator {

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract val viewModel: V

    protected val app: AppClass
        get() {
            return applicationContext as AppClass
        }

    override fun showLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showToast(msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}