package cm.seeds.rdtsmartreader.modeles

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Image(

    @PrimaryKey
    val imageKey : Long = System.currentTimeMillis(),
    val filePath : String,
    val name : String,
    val result : String
)
