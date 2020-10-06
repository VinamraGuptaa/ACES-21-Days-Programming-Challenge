package com.example.notes20

import androidx.room.Database

import androidx.room.RoomDatabase


@Database(entities =[(Model::class)],exportSchema = false,version = 2)
abstract class Database:RoomDatabase() {
    abstract fun callDao():notes_Dao

}