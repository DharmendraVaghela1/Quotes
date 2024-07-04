package com.example.quotes

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var quoteDatabaseHelper: QuoteDatabaseHelper
        lateinit var favoritesRecyclerView: RecyclerView
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.colorPrimaryDark)
        }
        setSupportActionBar(findViewById(R.id.my_toolbar))
        quoteDatabaseHelper = QuoteDatabaseHelper(this)
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView)

        // Set up RecyclerView
        favoritesRecyclerView.layoutManager = LinearLayoutManager(this)
        val favoriteQuotes = quoteDatabaseHelper.getLikedQuotes()
        val adapter = QuoteAdapter(favoriteQuotes)
        favoritesRecyclerView.adapter = adapter
    }
}
