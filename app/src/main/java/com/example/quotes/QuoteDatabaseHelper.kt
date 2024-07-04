package com.example.quotes

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class QuoteDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "QuotesDB"

        // Table name and columns
        const val TABLE_QUOTES = "quotes"
        const val COLUMN_QUOTE_ID = "_id"
        const val COLUMN_QUOTE_TEXT = "quote_text"
        const val COLUMN_QUOTE_LIKED = "quote_liked"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_QUOTES_TABLE = ("CREATE TABLE $TABLE_QUOTES (" +
                "$COLUMN_QUOTE_ID INTEGER PRIMARY KEY," +
                "$COLUMN_QUOTE_TEXT TEXT," +
                "$COLUMN_QUOTE_LIKED INTEGER DEFAULT 0)")
        db.execSQL(CREATE_QUOTES_TABLE)
        seedDatabase(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }

    private fun seedDatabase(db: SQLiteDatabase) {
        // Insert initial quotes into the database
        val initialQuotes = arrayOf(
            "The only way to do great work is to love what you do. - Steve Jobs",
            "In the end, it's not the years in your life that count. It's the life in your years. - Abraham Lincoln",
            "Life is what happens when you're busy making other plans. - John Lennon",
            "Get busy living or get busy dying. - Stephen King",
            "The only limit to our realization of tomorrow will be our doubts of today. - Franklin D. Roosevelt",
            "The greatest glory in living lies not in never falling, but in rising every time we fall. - Nelson Mandela",
            "The way to get started is to quit talking and begin doing. - Walt Disney",
            "Your time is limited, so don't waste it living someone else's life. - Steve Jobs",
            "If life were predictable it would cease to be life, and be without flavor. - Eleanor Roosevelt",
            "Spread love everywhere you go. Let no one ever come to you without leaving happier. - Mother Teresa",
            "Life is either a daring adventure or nothing at all. - Helen Keller",
            "The best and most beautiful things in the world cannot be seen or even touched - they must be felt with the heart. - Helen Keller",
            "Many of life's failures are people who did not realize how close they were to success when they gave up. - Thomas A. Edison",
            "You have brains in your head. You have feet in your shoes. You can steer yourself any direction you choose. - Dr. Seuss",
            "Believe you can and you're halfway there. - Theodore Roosevelt",
            "It is never too late to be what you might have been. - George Eliot",
            "The future belongs to those who believe in the beauty of their dreams. - Eleanor Roosevelt",
            "Tell me and I forget. Teach me and I remember. Involve me and I learn. - Benjamin Franklin",
            "It always seems impossible until it's done. - Nelson Mandela",
            "Don't cry because it's over, smile because it happened. - Dr. Seuss"
        )

        initialQuotes.forEach { quote ->
            val values = ContentValues().apply {
                put(COLUMN_QUOTE_TEXT, quote)
            }
            db.insert(TABLE_QUOTES, null, values)
        }
    }

    fun updateQuoteLikedStatus(quoteId: Long, liked: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_QUOTE_LIKED, if (liked) 1 else 0)
        }
        db.update(TABLE_QUOTES, values, "$COLUMN_QUOTE_ID = ?", arrayOf(quoteId.toString()))
        db.close()
    }

    @SuppressLint("Range")
    fun getLikedQuotes(): List<Quote> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_QUOTES WHERE $COLUMN_QUOTE_LIKED = 1", null)
        val likedQuotes = mutableListOf<Quote>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_QUOTE_ID))
            val text = cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE_TEXT))
            val liked = cursor.getInt(cursor.getColumnIndex(COLUMN_QUOTE_LIKED)) == 1
            likedQuotes.add(Quote(id, text, liked))
        }
        cursor.close()
        db.close()
        return likedQuotes
    }
}
