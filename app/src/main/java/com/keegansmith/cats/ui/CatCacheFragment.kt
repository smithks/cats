package com.keegansmith.cats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.keegansmith.cats.CatApplication
import com.keegansmith.cats.R
import com.keegansmith.cats.catList.CatViewModel
import javax.inject.Inject


class CatCacheFragment: Fragment() {

    @Inject
    lateinit var viewModel: CatViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.cat_cache_fragment, container, false)

        val factory = object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = (activity!!.application as CatApplication).catComponent.catViewModel() as T
        }
        viewModel = ViewModelProviders.of(this, factory).get(CatViewModel::class.java)
        viewModel.init((activity?.application as CatApplication).catComponent)

        val catImage = view.findViewById<ImageView>(R.id.cat_image)
        val fetchCatButton = view.findViewById<Button>(R.id.fetch_image_button)
        val deleteCatButton = view.findViewById<Button>(R.id.delete_image_button)
        val fileSizeTextView = view.findViewById<TextView>(R.id.image_file_size_text_view)
        val progressBar = view.findViewById<ProgressBar>(R.id.single_loading_bar)

        fetchCatButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            viewModel.fetchSingleImage()
        }

        deleteCatButton.setOnClickListener {
            viewModel.deleteImage()
        }

        viewModel.cacheImage.observe(this, Observer {
            progressBar.visibility = View.GONE
            Glide.with(catImage)
                .load(it)
                .into(catImage)
        })

        viewModel.cacheImageFileSize.observe(this, Observer {
            fileSizeTextView.text = it
        })

        progressBar.visibility = View.VISIBLE
        viewModel.fetchSingleImage()


        return view
    }

}