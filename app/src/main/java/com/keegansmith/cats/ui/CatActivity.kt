package com.keegansmith.cats.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.keegansmith.cats.R

class CatActivity: FragmentActivity() {

    lateinit var listButton: Button
    lateinit var breedButton: Button
    lateinit var cacheButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cat_activity)

        listButton = findViewById(R.id.cat_list_button)
        breedButton = findViewById(R.id.breed_list_button)
        cacheButton = findViewById(R.id.view_cat_cache_button)

        listButton.setOnClickListener {
            setupFragment(CatListFragment())
            hideButtons()
        }

        breedButton.setOnClickListener {
            setupFragment(CatBreedFragment())
            hideButtons()
        }

        cacheButton.setOnClickListener {
            setupFragment(CatCacheFragment())
            hideButtons()
        }
    }

    private fun setupFragment(fragment: Fragment) {
        val fragmentTransaction= supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, fragment).addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun hideButtons() {
        listButton.visibility = View.GONE
        breedButton.visibility = View.GONE
        cacheButton.visibility = View.GONE
    }

    // Pretty hacky way to get these buttons to show, should use a different fragment or a viewmodel
    override fun onBackPressed() {
        super.onBackPressed()
        listButton.visibility = View.VISIBLE
        breedButton.visibility = View.VISIBLE
        cacheButton.visibility = View.VISIBLE
    }
}