package com.example.notes20

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface notes_Dao {
    @Insert
    fun savenote_dao(model: Model)
    @Query("DELETE FROM mynote WHERE id=:id")
    fun deletenote(id: Int)


    @Query("UPDATE OR REPLACE mynote SET Title=:title,Body=:body,Date=:date WHERE id=:id ")
    fun updatenote(title:String,body:String,date:String,id: Int)
    @Query("Select * FROM mynote   ")
    fun getnote(): List<Model>

}