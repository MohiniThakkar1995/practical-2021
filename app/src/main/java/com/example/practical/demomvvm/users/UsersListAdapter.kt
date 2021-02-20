package com.example.practical.demomvvm.users

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demomvvm.USERS
import com.example.practical.databinding.RowProgressbarBinding
import com.example.practical.databinding.RowUserListBinding

import com.example.practical.demomvvm.base.BaseRecyclerViewAdapter
import com.example.practical.demomvvm.base.BaseViewHolder


class UsersListAdapter(val context: Context, rv: RecyclerView) :
    BaseRecyclerViewAdapter<BaseViewHolder, USERS>(rv) {
    private val viewItem = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == viewItem) {
            val itemBinding = RowUserListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            MyViewHolder(itemBinding)
        } else {
            val isLoadingBinding = RowProgressbarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            LoadingView(isLoadingBinding)
        }
    }

    inner class MyViewHolder(private val itemBinding: RowUserListBinding?) :
        BaseViewHolder(itemBinding!!.root) {

        override fun onBind(position: Int) {
            val model = list[position]
            itemBinding!!.tvUserName.text = model!!.name

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .into(itemBinding.ivUserImage)
            if (position % 2 == 0) {
                itemBinding.lineven.visibility = View.VISIBLE
                itemBinding.linodd.visibility = View.GONE

                itemBinding.rvEven.layoutManager = GridLayoutManager(context, 2)
                var adapterRow : UsersListAdapter
            } else {
                itemBinding.lineven.visibility = View.GONE
                itemBinding.linodd.visibility = View.VISIBLE
                Glide
                    .with(context)
                    .load(model.items[0])
                    .centerCrop()
                    .into(itemBinding.ivOddFirst)

            }

        }


    }

    inner class LoadingView(loadingBinding: RowProgressbarBinding?) :
        BaseViewHolder(loadingBinding!!.root) {
        override fun onBind(position: Int) {
        }
    }
    override fun getItemViewType(position: Int): Int {
        val viewProgress = 0
        val model = list[position]
        return if (model != null) viewItem else viewProgress
    }
}