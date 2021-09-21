package cm.seeds.rdtsmartreader.modeles

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Form(
        @PrimaryKey
        val formId  : Long = System.currentTimeMillis(),
        val dateCreationForm : Long = System.currentTimeMillis(),
        val formName : String = "",
        var pages : MutableList<Page>,
        var haveErrors  : Boolean = false
) : Serializable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Form

        if (formId != other.formId) return false
        if (dateCreationForm != other.dateCreationForm) return false
        if (formName != other.formName) return false
        if (pages != other.pages) return false
        if (haveErrors != other.haveErrors) return false

        return true
    }

    override fun hashCode(): Int {
        var result = formId.hashCode()
        result = 31 * result + dateCreationForm.hashCode()
        result = 31 * result + formName.hashCode()
        result = 31 * result + pages.hashCode()
        result = 31 * result + haveErrors.hashCode()
        return result
    }
}