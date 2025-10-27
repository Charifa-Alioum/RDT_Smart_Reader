package cm.seeds.rdtsmartreader.modeles

import android.util.Log
import androidx.room.Entity
import cm.seeds.rdtsmartreader.helper.*
import cm.seeds.rdtsmartreader.imagedecoder.Classifier
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.min

@Entity
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


    companion object{

        fun setResult(nbLines : Int, nbTriangles : Int, lines: ArrayList<Classifier.Recognition>, triangles: ArrayList<Classifier.Recognition>) : Int{

            Log.e("TAG", "$nbLines nombre de ligne \n $nbTriangles nombre de triangle")

            var category = -1

            // Case of 2 lines-RDT
            when(nbTriangles){

                0 -> {
                    // case of special RDT
                    when (nbLines) {

                        0 -> category = CATEGORY_INVALID

                        2 -> category = CATEGORY_POSITIVE

                    }
                }

                2 -> {
                    // Case of 2 lines-RDT
                    when (nbLines) {

                        0 -> category = CATEGORY_INVALID // means no lines, then 'invalid' test

                        1 -> {
                            // here we have 2 cases depend of the position of the line:
                            // if the line is near of control-flag then 'negative', otherwise 'invalid'
                            // we calculate delta1 (distance to control-flag) and delta2 (distance to test-flag)
                            val line = lines[0]
                            val controlTriangle = triangles[0]
                            val testTriangle = triangles[1]
                            val delta1 =
                                abs(line.location.centerY() - controlTriangle.location.centerY())
                            val delta2 = abs(line.location.centerY() - testTriangle.location.centerY())
                            category = if (delta1 < delta2) {
                                // means that the line is near the control-flag
                                CATEGORY_NEGATIVE
                            } else {
                                // means that the line is near the test-flag
                                CATEGORY_INVALID
                            }
                        }

                        2 -> category = CATEGORY_POSITIVE // means control and test lines are present and equals to number of traingles, then 'positive' test

                    }
                }

                3 -> {
                    // case of 3-lines RDT
                    when (nbLines) {

                        0 -> category = CATEGORY_INVALID // means all attempted lines are present, then 'positive'

                        1 -> {
                            // means just one line are present
                            // if the line is near of control-flag then 'negative', otherwise 'invalid'
                            val line = lines[0]
                            val controlTriangle = triangles[0]
                            val test1Triangle = triangles[1]
                            val test2Triangle = triangles[2]

                            val delta1 = abs(line.location.centerY() - controlTriangle.location.centerY())
                            val delta2 = abs(line.location.centerY() - test1Triangle.location.centerY())
                            val delta3 = abs(line.location.centerY() - test2Triangle.location.centerY())

                            category = if (delta1 == min(delta1, min(delta2, delta3))) {
                                // means that the line is near control-flag
                                CATEGORY_NEGATIVE
                            } else {
                                CATEGORY_INVALID
                            }
                        }

                        2 -> {
                            // means two lines are present
                            val line1 = lines[0]
                            //val line2 = lines[1]
                            val controlTriangle = triangles[0]
                            val test1Triangle = triangles[1]
                            val test2Triangle = triangles[2]

                            val distanceLine1TriangleControl =
                                abs(line1.location.centerY() - controlTriangle.location.centerY())
                            val distanceLine1TriangleAgent1 =
                                abs(line1.location.centerY() - test1Triangle.location.centerY())
                            val distanceLine1TriangleAgent2 =
                                abs(line1.location.centerY() - test2Triangle.location.centerY())


                            var minDelta = min(min(distanceLine1TriangleControl, distanceLine1TriangleAgent1),distanceLine1TriangleAgent2)
                            if(minDelta != distanceLine1TriangleControl){
                                category = CATEGORY_INVALID
                            }else{
                                val line2 = lines[1]
                                val distanceLine2TriangleControl =
                                    abs(line2.location.centerY() - controlTriangle.location.centerY())
                                val distanceLine2TriangleAgent1 =
                                    abs(line2.location.centerY() - test1Triangle.location.centerY())
                                val distanceLine2TriangleAgent2 =
                                    abs(line2.location.centerY() - test2Triangle.location.centerY())

                                minDelta = min(min(distanceLine1TriangleControl, distanceLine1TriangleAgent1),distanceLine1TriangleAgent2)

                                category = when (minDelta) {
                                    distanceLine2TriangleAgent1 -> {
                                        CATEGORY_POSTIVE_AGENT_ONE
                                    }
                                    distanceLine2TriangleAgent2 -> {
                                        CATEGORY_POSTIVE_AGENT_TWO
                                    }

                                    else -> -1
                                }
                            }
                        }

                        3 -> category = CATEGORY_POSTIVE_BOTH_AGENT // means all attempted lines are present, then 'positive'
                    }
                }

            }

            return category

        }

    }

}
