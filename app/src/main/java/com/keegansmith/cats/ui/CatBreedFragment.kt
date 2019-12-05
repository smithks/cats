package com.keegansmith.cats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.keegansmith.cats.CatApplication
import com.keegansmith.cats.R
import com.keegansmith.cats.catList.CatViewModel


class CatBreedFragment : Fragment() {

    lateinit var breedsTextView: TextView
    lateinit var breedsProgressBar: ProgressBar
    lateinit var breedsErrorMessage: TextView
    lateinit var fileSizeTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.cat_breed_fragment, container, false)
        val factory = object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = (activity!!.application as CatApplication).catComponent.catViewModel() as T
        }
        val catViewModel = ViewModelProviders.of(this, factory).get(CatViewModel::class.java)

        breedsTextView = view.findViewById(R.id.breeds_text_view)
        breedsProgressBar = view.findViewById(R.id.breeds_loading_progress)
        breedsErrorMessage = view.findViewById(R.id.error_message_text_view)
        fileSizeTextView = view.findViewById(R.id.file_size_text_view)

        catViewModel.init((activity?.application as CatApplication).catComponent)
        catViewModel.cacheText.observe(this, Observer { breeds ->
            breedsTextView.text = breeds.fold("", {acc, breedModel -> "$acc $breedModel"})
            if (breeds.isNotEmpty()) {
                breedsProgressBar.visibility = View.GONE
            }
        })

        catViewModel.errorMessage.observe(this, Observer {
            breedsErrorMessage.visibility = View.VISIBLE
        })

        breedsProgressBar.visibility = View.VISIBLE
        catViewModel.fetchBreeds()

        view.findViewById<Button>(R.id.fetch_breeds_button).setOnClickListener {
            catViewModel.fetchBreeds()
            breedsProgressBar.visibility = View.VISIBLE
        }

        view.findViewById<Button>(R.id.delete_breeds_button).setOnClickListener {
            catViewModel.deleteBreeds()
        }

        catViewModel.cacheTextFileSize.observe(this, Observer { fileSize ->
            fileSizeTextView.text = fileSize
        })

        return view
    }

}