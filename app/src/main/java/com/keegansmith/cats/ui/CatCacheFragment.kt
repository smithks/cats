package com.keegansmith.cats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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

        viewModel = ViewModelProviders.of(this).get(CatViewModel::class.java)
        viewModel.init((activity?.application as CatApplication).catComponent)

        val catImage = view.findViewById<ImageView>(R.id.cat_image)
        val fetchCatButton = view.findViewById<Button>(R.id.fetch_image_button)
        val deleteCatButton = view.findViewById<Button>(R.id.delete_image_button)
        val fileSizeTextView = view.findViewById<TextView>(R.id.image_file_size_text_view)

        fetchCatButton.setOnClickListener {
            viewModel.fetchSingleImage()
        }

        deleteCatButton.setOnClickListener {
            viewModel.deleteImage()
        }

        viewModel.singleCatImage.observe(this, Observer {
            Glide.with(catImage)
                .load(it)
                .into(catImage)
        })

        viewModel.imageFileSize.observe(this, Observer {
            fileSizeTextView.text = it
        })

        viewModel.fetchSingleImage()


        return view
    }

}