package com.example.practical.demomvvm.users


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.BAS.BaseNavigator
import com.example.practical.BR
import com.example.practical.R
import com.example.practical.databinding.ActivityUsersBinding
import com.example.practical.demomvvm.ViewModelProviderFactory
import com.example.practical.demomvvm.base.BaseActivity


class UserActivity : BaseActivity<ActivityUsersBinding, UsersViewModel>(), BaseNavigator {

    override val viewModel: UsersViewModel
        get() = ViewModelProvider(
            this,
            ViewModelProviderFactory<UsersViewModel>(UsersViewModel(app))
        ).get(UsersViewModel::class.java)
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_users

    var adapter: UsersListAdapter? = null

    override fun initialization(savedInstance: Bundle?) {

        viewModel.navigator = this

        viewModel.apiGetUsersList()

        setUpRecyclerView()

        populateData()
    }
    private fun populateData() {
        viewModel.faqList.observe(this, Observer {
            adapter!!.list = it
            if (it.size > 0) {
                viewDataBinding!!.rvFaqList.visibility = View.VISIBLE
                viewDataBinding!!.tvNoDataFound.visibility = View.GONE
            } else {
                viewDataBinding!!.rvFaqList.visibility = View.GONE
                viewDataBinding!!.tvNoDataFound.visibility = View.VISIBLE
            }
        })
    }
    private fun setUpRecyclerView() {
        viewDataBinding!!.rvFaqList.layoutManager = LinearLayoutManager(this@UserActivity)
        adapter = UsersListAdapter(this@UserActivity, viewDataBinding!!.rvFaqList)
        viewDataBinding!!.rvFaqList.adapter = adapter
    }
    companion object {
        fun newIntent(context: Context, isFinish: Boolean): Intent {
            val intent = Intent(context, UserActivity::class.java)
            if (isFinish)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

            return intent
        }
    }
    override fun showSnackBar(message: String, isError: Boolean) {
        TODO("Not yet implemented")
    }

    override fun showAlert(title: String, msg: String?) {
        TODO("Not yet implemented")
    }

    override fun gotoLogin() {
        TODO("Not yet implemented")
    }



}