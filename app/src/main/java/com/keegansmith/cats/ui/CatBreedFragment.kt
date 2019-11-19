package com.keegansmith.cats.ui

import android.os.Bundle
import android.service.autofill.TextValueSanitizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.keegansmith.cats.CatApplication
import com.keegansmith.cats.R
import com.keegansmith.cats.catList.CatViewModel


class CatBreedFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.cat_breed_fragment, container, false)
        val catViewModel = ViewModelProviders.of(activity!!).get(CatViewModel::class.java)

        catViewModel.catService = (activity?.application as CatApplication).catService

        catViewModel.breedList.observe(this, Observer {breeds ->
            view.findViewById<TextView>(R.id.breeds_text_view).text = breeds.fold("", {acc, breedModel -> "$acc $breedModel"})
        })

        catViewModel.errorMessage.observe(this, Observer {
            view.findViewById<TextView>(R.id.error_message_text_view).visibility = View.VISIBLE
        })

        catViewModel.fetchBreeds()

        return view
    }

}