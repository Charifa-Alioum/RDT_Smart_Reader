package cm.seeds.rdtsmartreader.modeles

import android.net.Uri
import cm.seeds.rdtsmartreader.helper.*
import java.io.Serializable

data class Test(

        var dateTest : Long = System.currentTimeMillis(),
        var datePrelevement : Long = System.currentTimeMillis(),
        var blocTest : String = "",
        var natureTest : String = "",
        var typePrelevement : String = TYPE_PRELEVEMENT_NASOPHARYNGE,
        var indicationPrelevement : String = INDICATION_PRELEVEMENT_VOLONTAIRE,
        var resultatsCovidAg : String = "",
        var resultatsCovidIgg : String = "",
        var resultatsCovidIgm : String = "",
        var conclusion : String = "",
        var manipulateur : String = "",
        var telephoneManipulateur : String = "",
        var imageUri : String = ""
    ) : Serializable{

    init {

        conclusion = when(natureTest){

            NATURE_TEST_COVID_19_AC -> resultatsCovidAg

            NATURE_TEST_COVID_19_AG -> when{

                resultatsCovidIgg == CONCLUSION_POSITIF -> "Exposé"

                resultatsCovidIgm == CONCLUSION_POSITIF -> "Infection en Cours"

                else -> ""

            }

            else -> ""

        }

    }

}
