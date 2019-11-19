package com.keegansmith.cats.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import com.keegansmith.cats.R

class CatActivity: FragmentActivity() {

    lateinit var listButton: Button
    lateinit var breedButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cat_activity)

        listButton = findViewById(R.id.cat_list_button)
        breedButton = findViewById(R.id.breed_list_button)

        findViewById<Button>(R.id.cat_list_button).setOnClickListener {
            val fragment = CatListFragment()
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.fragment_container, fragment).addToBackStack(null)
            fragmentTransaction.commit()
            listButton.visibility = View.GONE
            breedButton.visibility = View.GONE
        }

        findViewById<Button>(R.id.breed_list_button).setOnClickListener {
            val fragment = CatBreedFragment()
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.fragment_container, fragment).addToBackStack(null)
            fragmentTransaction.commit()
            listButton.visibility = View.GONE
            breedButton.visibility = View.GONE
        }
    }

    // Pretty hacky way to get these buttons to show, should use a different fragment or a viewmodel
    override fun onBackPressed() {
        super.onBackPressed()
        listButton.visibility = View.VISIBLE
        breedButton.visibility = View.VISIBLE
    }
}