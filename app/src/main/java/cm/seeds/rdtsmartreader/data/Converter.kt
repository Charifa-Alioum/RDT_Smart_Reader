package cm.seeds.rdtsmartreader.data

import androidx.room.TypeConverter
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

    }

}