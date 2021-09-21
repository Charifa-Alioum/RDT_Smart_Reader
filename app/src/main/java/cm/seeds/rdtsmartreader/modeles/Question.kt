package cm.seeds.rdtsmartreader.modeles

import java.io.Serializable

data class Question(
        val questionId : Long = System.currentTimeMillis(),
        var questionLabel : String = "",
        var questionLabelForSaving : String = "",
        var required : Boolean = false,
        var questionHint : String = "",
        var questionType : String = "",
        var minDate : Long = 0,
        var maxDate : Long = 0
) : Serializable{

    var options = mutableListOf<Option>()

    fun removeOption(option: Option) {
        options.remove(option)
    }

    fun addOption(newOption : Option){
        options.add(newOption)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Question

        if (questionId != other.questionId) return false
        if (questionLabel != other.questionLabel) return false
        if (questionLabelForSaving != other.questionLabelForSaving) return false
        if (required != other.required) return false
        if (questionHint != other.questionHint) return false
        if (questionType != other.questionType) return false
        if (minDate != other.minDate) return false
        if (maxDate != other.maxDate) return false
        if (options != other.options) return false

        return true
    }

    override fun hashCode(): Int {
        var result = questionId.hashCode()
        result = 31 * result + questionLabel.hashCode()
        result = 31 * result + questionLabelForSaving.hashCode()
        result = 31 * result + required.hashCode()
        result = 31 * result + questionHint.hashCode()
        result = 31 * result + questionType.hashCode()
        result = 31 * result + minDate.hashCode()
        result = 31 * result + maxDate.hashCode()
        result = 31 * result + options.hashCode()
        return result
    }
}
