package cm.seeds.rdtsmartreader.modeles

import java.io.Serializable

data class Option(
        val optionId : Long = System.currentTimeMillis(),
        var optionLabel : String = "",
        var selected : Boolean = false
)  : Serializable{

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Option

                if (optionId != other.optionId) return false
                if (optionLabel != other.optionLabel) return false
                if (selected != other.selected) return false

                return true
        }

        override fun hashCode(): Int {
                var result = optionId.hashCode()
                result = 31 * result + optionLabel.hashCode()
                result = 31 * result + selected.hashCode()
                return result
        }
}