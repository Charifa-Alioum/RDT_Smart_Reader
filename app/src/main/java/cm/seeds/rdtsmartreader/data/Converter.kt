package cm.seeds.rdtsmartreader.data

import androidx.room.TypeConverter
import cm.seeds.rdtsmartreader.modeles.Coordonnee
import cm.seeds.rdtsmartreader.modeles.Test
import cm.seeds.rdtsmartreader.modeles.User
import com.google.gson.Gson

class Converter {

    companion object{

        @TypeConverter
        @JvmStatic
        fun fromUserToString(user: User) : String{
            return Gson().toJson(user)
        }


        @TypeConverter
        @JvmStatic
        fun fromStringToUser(string: String) : User{
            return Gson().fromJson(string,User::class.java)
        }

        @TypeConverter
        @JvmStatic
        fun fromCoordonneeToString(coordonnee: Coordonnee) : String{
            return Gson().toJson(coordonnee)
        }


        @TypeConverter
        @JvmStatic
        fun fromStringToCoordonnee(string: String) : Coordonnee{
            return Gson().fromJson(string,Coordonnee::class.java)
        }

        @TypeConverter
        @JvmStatic
        fun fromTestToString(test: Test) : String{
            return Gson().toJson(test)
        }


        @TypeConverter
        @JvmStatic
        fun fromStringToTest(string: String) : Test{
            return Gson().fromJson(string,Test::class.java)
        }

    }
}