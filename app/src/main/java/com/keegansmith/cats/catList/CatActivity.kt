package com.keegansmith.cats.catList

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.keegansmith.cats.CatApplication
import com.keegansmith.cats.R
import com.keegansmith.cats.api.CatService
import com.keegansmith.cats.api.model.CatModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class CatActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cat_activity)

        // Setup recyclerview
        val recyclerView: RecyclerView = findViewById(R.id.cat_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val catAdapter = CatAdapter()
        recyclerView.adapter = catAdapter

        // Establish viewmodel and catService
        //TODO inject service
        val catService = (application as CatApplication).catService
        val catViewModel = ViewModelProviders.of(this).get(CatViewModel::class.java)
        catViewModel.catService = catService

        // Observe cat list livedata from viewmodel
        catViewModel.catList.observe(this, Observer<List<CatModel>> { list ->
            catAdapter.cats = list
            catAdapter.notifyDataSetChanged()
        })

        // Fetch initial list
        catViewModel.fetchCats()

        // Set FAB click listener
        findViewById<FloatingActionButton>(R.id.cat_refresh).setOnClickListener {view -> catViewModel.fetchCats() }

    }
}