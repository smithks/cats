package com.keegansmith.cats.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.keegansmith.cats.R

class CatActivity: FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cat_activity)
    }
}