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
import com.example.practical.demomvvm.adapter.UsersListAdapter
import com.example.practical.demomvvm.base.BaseActivity
import com.example.practical.demomvvm.base.BaseRecyclerViewAdapter

//implement all common functions in base activity, and extends it
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

    //    initialization
    override fun initialization(savedInstance: Bundle?) {

        viewModel.navigator = this

        viewModel.apiGetUsersList()

        setUpRecyclerView()

        populateData()
    }

    //    observe list and populate data on recyclerview
    private fun populateData() {
        viewModel.userList.observe(this, Observer {
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

    //    setup recyclerview
    private fun setUpRecyclerView() {
        viewDataBinding!!.rvFaqList.layoutManager = LinearLayoutManager(this@UserActivity)
        adapter = UsersListAdapter(
            this@UserActivity,
            viewDataBinding!!.rvFaqList
        )
        viewDataBinding!!.rvFaqList.adapter = adapter

        adapter!!.onLoadMoreListener = object : BaseRecyclerViewAdapter.OnLoadMoreListener {

            override fun onLoadMore() {
                if (viewModel.isLast.get() != 1) {
                    viewModel.start.set(viewModel.start.get() + 10)
                    viewModel.userList.value!!.add(null)
                    adapter!!.notifyItemInserted(viewModel.userList.value!!.size - 1)
                    viewModel.apiGetUsersList()
                }
            }
        }
    }

}