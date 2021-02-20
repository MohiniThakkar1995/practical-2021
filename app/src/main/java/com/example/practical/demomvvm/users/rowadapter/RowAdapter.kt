package com.example.practical.demomvvm.users.rowadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practical.databinding.RowImageBinding
import com.example.practical.databinding.RowProgressbarBinding
import com.example.practical.databinding.RowUserListBinding

import com.example.practical.demomvvm.base.BaseRecyclerViewAdapter
import com.example.practical.demomvvm.base.BaseViewHolder


class RowAdapter(val context: Context, rv: RecyclerView) :
    BaseRecyclerViewAdapter<BaseViewHolder, com.example.practical.demomvvm.model.Image>(rv) {
    private val viewItem = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == viewItem) {
            val itemBinding = RowImageBinding.inflate(
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

    inner class MyViewHolder(private val itemBinding: RowImageBinding?) :
        BaseViewHolder(itemBinding!!.root) {

        override fun onBind(position: Int) {
            val model = list[position]
            Glide
                .with(context)
                .load(model!!.imageUrl)
                .centerCrop()
                .into(itemBinding!!.ivImage)
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