package com.keegansmith.cats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.keegansmith.cats.R


class CatMainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.cat_main_fragment, container, false)

        view.findViewById<Button>(R.id.cat_list_button).setOnClickListener {
            val action = CatMainFragmentDirections.actionCatMainFragmentToCatListFragment()
            findNavController().navigate(action)
        }

        view.findViewById<Button>(R.id.breed_list_button).setOnClickListener {
            val action = CatMainFragmentDirections.actionCatMainFragmentToCatBreedFragment()
            findNavController().navigate(action)
        }

        view.findViewById<Button>(R.id.view_cat_cache_button).setOnClickListener {
            val action = CatMainFragmentDirections.actionCatMainFragmentToCatCacheFragment()
            findNavController().navigate(action)
        }

        return view
    }

}