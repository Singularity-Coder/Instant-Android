package com.example.epoxy1.view

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.epoxy1.R

@EpoxyModelClass
abstract class FoodViewItem : EpoxyModelWithHolder<FoodViewItem.FoodHolder>() {

    @EpoxyAttribute
    @DrawableRes
    var image: Int = 0

    @EpoxyAttribute
    var title: String? = ""

    @EpoxyAttribute
    var desc: String? = ""

    override fun getDefaultLayout(): Int {
        return R.layout.food_item
    }

    override fun bind(holder: FoodHolder) {
        holder.imageView.setImageResource(image)
        holder.titleView.text = title
        holder.descView.text = desc
    }

    inner class FoodHolder : EpoxyHolder() {
        lateinit var imageView: ImageView
        lateinit var titleView: TextView
        lateinit var descView: TextView

        override fun bindView(itemView: View) {
            imageView = itemView.findViewById<ImageView>(R.id.image)
            titleView = itemView.findViewById<TextView>(R.id.title)
            descView = itemView.findViewById<TextView>(R.id.desc)
        }
    }
}