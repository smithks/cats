package com.keegansmith.cats.catList

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.keegansmith.cats.R
import com.keegansmith.cats.api.model.CatModel

class CatAdapter: RecyclerView.Adapter<CatViewHolder>() {

    var cats: List<CatModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cat_item, parent, false)

        return CatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cats.size
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        holder.view.findViewById<ProgressBar>(R.id.cat_loading).visibility = View.VISIBLE
        Glide.with(holder.view)
            .asBitmap()
            .load(cats[position].url)
            .listener(object: RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return true
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.view.findViewById<ProgressBar>(R.id.cat_loading).visibility = View.GONE
                    return false
                }

            })
            .transition(BitmapTransitionOptions.withCrossFade())
            .centerCrop()
            .into(holder.view.findViewById(R.id.cat_image))
    }

    // Playing around with dynamically resizing the imageview based on image size
    class CatTarget(val imageView: ImageView, val adapter: CatAdapter, val position: Int) : CustomViewTarget<ImageView, Bitmap>(imageView) {
        override fun onLoadFailed(errorDrawable: Drawable?) {

        }

        override fun onResourceCleared(placeholder: Drawable?) {
            imageView.minimumHeight = 0
            imageView.setImageBitmap(null)
        }

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            val heightDp = (resource.height / imageView.resources.displayMetrics.density).toInt()
            imageView.minimumHeight = heightDp

            imageView.setImageBitmap(resource)
        }

    }
}