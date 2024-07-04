package com.example.quotes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quotes.QuoteDatabaseHelper.Companion.COLUMN_QUOTE_ID
import com.example.quotes.QuoteDatabaseHelper.Companion.COLUMN_QUOTE_TEXT
import com.example.quotes.QuoteDatabaseHelper.Companion.COLUMN_QUOTE_LIKED
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var quoteDatabaseHelper: QuoteDatabaseHelper
    private lateinit var likeBtn: ImageButton
    private lateinit var quoteTextView: TextView
    private lateinit var shareBtn: FloatingActionButton
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
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
        likeBtn = findViewById(R.id.likeBtn)
        quoteTextView = findViewById(R.id.quoteTextView)
        shareBtn = findViewById(R.id.shareBtn)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        quoteDatabaseHelper = QuoteDatabaseHelper(this)
        displayRandomQuote()

        likeBtn.setOnClickListener {
            val db = quoteDatabaseHelper.writableDatabase

            val quoteText = quoteTextView.text.toString()

            // Fetch quote ID based on quote text
            val cursor = db.rawQuery("SELECT $COLUMN_QUOTE_ID, $COLUMN_QUOTE_LIKED FROM ${QuoteDatabaseHelper.TABLE_QUOTES} WHERE $COLUMN_QUOTE_TEXT = ?", arrayOf(quoteText))
            if (cursor.moveToFirst()) {
                val quoteId = cursor.getLong(cursor.getColumnIndex(COLUMN_QUOTE_ID))
                val isLiked = cursor.getInt(cursor.getColumnIndex(COLUMN_QUOTE_LIKED)) == 1

                // Toggle liked status
                val newLikedStatus = !isLiked
                // Update database with new liked status
                quoteDatabaseHelper.updateQuoteLikedStatus(quoteId, newLikedStatus)

                // Update UI based on new liked status
                if (newLikedStatus) {
                    likeBtn.setImageResource(R.drawable.like_red) // Change to red icon when liked
                } else {
                    likeBtn.setImageResource(R.drawable.like) // Change to default icon when not liked
                }
            }

            cursor.close()
            db.close()
        }

        shareBtn.setOnClickListener {
            val quoteText = quoteTextView.text.toString()
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Quote of the day\n\n$quoteText")
            startActivity(Intent.createChooser(shareIntent, "Share Quote"))
        }
    }

    @SuppressLint("Range")
    private fun displayRandomQuote() {
        val db = quoteDatabaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${QuoteDatabaseHelper.TABLE_QUOTES} ORDER BY RANDOM() LIMIT 1", null)
        if (cursor.moveToFirst()) {
            val quoteText = cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE_TEXT))
            val quoteLiked = cursor.getInt(cursor.getColumnIndex(COLUMN_QUOTE_LIKED)) == 1

            quoteTextView.text = quoteText

            if (quoteLiked) {
                likeBtn.setImageResource(R.drawable.like_red) // Set red icon if liked
            } else {
                likeBtn.setImageResource(R.drawable.like) // Set default icon if not liked
            }
        }
        cursor.close()
        db.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorites -> {
                // Handle Favorites menu item click
                navigateToFavoritesActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToFavoritesActivity() {
        val intent = Intent(this, FavoritesActivity::class.java)
        startActivity(intent)
    }
}