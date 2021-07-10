package com.singularitycoder.daggerhilt1

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.singularitycoder.daggerhilt1.databinding.ListItemRepoBinding

class RepoAdapter(
    private val repoList: List<Item>,
    private val context: Context
) : RecyclerView.Adapter<RepoAdapter.RepoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding = ListItemRepoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepoViewHolder(binding = binding)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bindItems(repo = repoList[position], context = context)
    }

    override fun getItemCount(): Int = repoList.size

    override fun getItemViewType(position: Int): Int = position

    class RepoViewHolder(private val binding: ListItemRepoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItems(repo: Item, context: Context) {
            binding.apply {
                tvUserName.text = repo.owner.login.orEmpty()
                tvRepoName.text = repo.name.orEmpty()
                tvRepoDescription.text = repo.description.orEmpty()
            }
            AppUtils.glideImage(context = context, imgUrl = repo.owner.avatarUrl, imageView = binding.ivUserImage)
            binding.root.setOnClickListener {}
        }
    }
}