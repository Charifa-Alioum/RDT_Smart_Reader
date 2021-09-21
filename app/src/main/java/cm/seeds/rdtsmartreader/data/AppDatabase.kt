package cm.seeds.rdtsmartreader.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cm.seeds.rdtsmartreader.modeles.Form
import cm.seeds.rdtsmartreader.modeles.Image
import cm.seeds.rdtsmartreader.modeles.Test
import cm.seeds.rdtsmartreader.modeles.User

@Database(entities = [User::class, Form::class, Image::class], version = 17)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object{
        private var INSTANCE : AppDatabase? = null

        fun database(application: Application): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(application).also { INSTANCE = it }
                }

        private fun buildDatabase(application: Application) =
                Room.databaseBuilder(application,
                        AppDatabase::class.java, "RTDDatabase.db")
                        .fallbackToDestructiveMigration()
                        .build()
    }

    abstract fun getDao() : Dao
}