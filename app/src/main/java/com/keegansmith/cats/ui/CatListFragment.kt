package com.keegansmith.cats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.keegansmith.cats.CatApplication
import com.keegansmith.cats.R
import com.keegansmith.cats.api.model.CatModel
import com.keegansmith.cats.catList.CatAdapter
import com.keegansmith.cats.catList.CatViewModel

class CatListFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.cat_list_fragment, container, false)
        // Setup recyclerview
        val recyclerView: RecyclerView = view.findViewById(R.id.cat_recycler)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val catAdapter = CatAdapter()
        recyclerView.adapter = catAdapter

        val errorMessage: TextView = view.findViewById(R.id.error_message_text_view)

        // Establish viewmodel and catService
        //TODO inject service
        val catService = (activity?.application as CatApplication).catService
        val catViewModel = ViewModelProviders.of(activity!!).get(CatViewModel::class.java)
        catViewModel.catService = catService

        catViewModel.errorMessage.observe(this, Observer {
            recyclerView.visibility = View.GONE
            errorMessage.visibility = View.VISIBLE
        })

        // Observe cat list livedata from viewmodel
        catViewModel.catList.observe(this, Observer<List<CatModel>> { list ->
            recyclerView.visibility = View.VISIBLE
            errorMessage.visibility = View.GONE
            catAdapter.cats = list
            catAdapter.notifyDataSetChanged()
        })

        // Fetch initial list
        catViewModel.fetchCats()

        // Set FAB click listener
        view.findViewById<FloatingActionButton>(R.id.cat_refresh).setOnClickListener { view -> catViewModel.fetchCats() }
        return view
    }

}
