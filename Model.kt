package com.example.notes20

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName ="mynote")
 class Model:Serializable  {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "Title")
    var Title: String = ""

    @ColumnInfo(name = "Body")
    var Body: String = " "

    @ColumnInfo(name = "Date")
    var Date: String = ""


}
