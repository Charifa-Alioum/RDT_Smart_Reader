package cm.seeds.rdtsmartreader.modeles

import java.io.Serializable

data class Page(
        val pageId : Long = System.currentTimeMillis(),
        var title : String = "",
        var description : String = "",
        val questions : MutableList<Question>,
        var principalColor : String? = null
) : Serializable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Page

        if (pageId != other.pageId) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (questions != other.questions) return false
        if (principalColor != other.principalColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pageId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + questions.hashCode()
        result = 31 * result + (principalColor?.hashCode() ?: 0)
        return result
    }
}
