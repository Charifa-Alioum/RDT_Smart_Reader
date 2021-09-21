package cm.seeds.rdtsmartreader.helper

import android.app.Application
import android.content.Context
import android.graphics.*
import android.util.Log
import cm.seeds.rdtsmartreader.imagedecoder.Classifier
import cm.seeds.rdtsmartreader.imagedecoder.Classifier.Recognition
import cm.seeds.rdtsmartreader.imagedecoder.TessOCR
import cm.seeds.rdtsmartreader.imagedecoder.YoloV5Classifier
import cm.seeds.rdtsmartreader.imagedecoder.env.ImageUtils
import cm.seeds.rdtsmartreader.imagedecoder.env.Utils
import cm.seeds.rdtsmartreader.modeles.Test
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.math.abs
import kotlin.math.min


class PythonMod() {

    private var classifier : Classifier? = null
    private var application : Application? = null
    private var preprocessingMod : PyObject? = null
    private var tessOCR : TessOCR? = null

    private constructor(application: Application) : this(){

        tessOCR = TessOCR(application, "eng")

        classifier = getClassifier(application)

        this.application = application

        if(!Python.isStarted()){
            Python.start(AndroidPlatform(application))
        }

        preprocessingMod = Python.getInstance().getModule("preprocessing")

    }

    companion object{

        const val LINE = "line"
        const val TRIANGLE = "triangle"

        const val CATEGORY_INVALID = 2
        const val CATEGORY_POSITIVE = 1
        const val CATEGORY_NEGATIVE = 0


        private var instance : PythonMod? = null

        fun getPythonMod(application: Application) : PythonMod =
            instance?: synchronized(this){
                instance?: initPythonMod(application)
            }

        private fun initPythonMod(application: Application) : PythonMod {
            return PythonMod(application)
        }

    }

    fun getLabelOfCategory(category: Int): String {

        return when(category){

            //-1 -> CONCLUSION_INDETERMINE

            CATEGORY_NEGATIVE -> CONCLUSION_NEGATIF

            CATEGORY_POSITIVE -> CONCLUSION_POSITIF

            CATEGORY_INVALID -> CONCLUSION_INVALIDE

            CATEGORY_POSTIVE_AGENT_ONE -> CONCLUSION_POSITIVE_AGENT_ONE

            CATEGORY_POSTIVE_AGENT_TWO -> CONCLUSION_POSITIVE_AGENT_TWO

            CATEGORY_POSTIVE_BOTH_AGENT -> CONCLUSION_POSITIVE_BOTH_AGENT

            else -> CONCLUSION_INDETERMINE

        }
    }

    private fun getClassifier(context: Context) : Classifier? {
        val maintainAspectRatio = true
        val sensorOrientation = 90
        val previewHeight = TF_OD_API_INPUT_SIZE
        val previewWidth = TF_OD_API_INPUT_SIZE

        val frameToCropTransform = ImageUtils.getTransformationMatrix(
            previewWidth, previewHeight,
            TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE,
            sensorOrientation, maintainAspectRatio
        )
        val cropToFrameTransform = Matrix()
        frameToCropTransform!!.invert(cropToFrameTransform)

        return try {
            YoloV5Classifier.create(
                context.assets,
                TF_OD_API_MODEL_FILE,
                TF_OD_API_LABELS_FILE,
                TF_OD_API_IS_QUANTIZED,
                TF_OD_API_INPUT_SIZE
            )
        } catch (e: IOException) {
            /*e.printStackTrace()
            Log.e("TAG", "Exception initializing classifier!")
            showToast(requireContext(), "Classifier could not be initialized")*/
            null
        }
    }


    /**
     * Methode qui identifie les lignes et les colonnes sur une image
     * @return le nombre de ligne et de colonne de l'image
     */
    suspend fun identify(sourceBitmap: Bitmap) : Int{
/*        val sourceBitmap = when {

            (imagePath is String) -> BitmapFactory.decodeFile(imagePath)

            imagePath is Int -> BitmapFactory.decodeResource(application?.resources, imagePath)

            else -> null
        }*/

        val cropBitmap = Utils.processBitmap(sourceBitmap, TF_OD_API_INPUT_SIZE)

        val results = classifier?.recognizeImage(cropBitmap)

        return handleResult(cropBitmap, results)
    }

    /**
     * Algorithme de pretraitement de l'image
     * @return une bitmap représentant l'image traité
     */
    suspend fun preproccessImage(imagePath: String) : List<Bitmap?>{

        return withContext(Dispatchers.IO){
            try {
                val bytes : List<PyObject>? = preprocessingMod?.callAttr("preprocess", imagePath)?.asList()

                val cropBytes = bytes?.get(0)?.toJava(ByteArray::class.java)
                val part1Bytes = bytes?.get(1)?.toJava(ByteArray::class.java)
                val part2Bytes = bytes?.get(2)?.toJava(ByteArray::class.java)

                val cropBitmap = BitmapFactory.decodeByteArray(cropBytes, 0, cropBytes?.size?:0)
                val part1Bitmap = BitmapFactory.decodeByteArray(part1Bytes, 0, part1Bytes?.size?:0)
                val part2Bitmap = BitmapFactory.decodeByteArray(part2Bytes, 0, part2Bytes?.size?:0)

                mutableListOf(cropBitmap,part1Bitmap,part2Bitmap)
            }catch(e : Exception){
                Log.e("TAG","Impossible de traiter l'image recu")
                mutableListOf<Bitmap>()
            }
        }
    }


    /**
     * Methode qui analyse les résultats fournis par l'algorithme de detections de forme sur l'image
     * @return le nombre de lignes et de triangles de l'image
     */
    private suspend fun handleResult(bitmap: Bitmap?, results: List<Recognition?>?): Int {
        return withContext(Dispatchers.IO){

/*            var lines = 0
            var triangles = 0*/

            var category : Int = -1

            if(bitmap!=null){
                //val canvas = Canvas(bitmap)
                val paint = Paint()
                paint.color = Color.RED
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 2.0f
                //val mappedRecognitions: List<Classifier.Recognition> = LinkedList()


                results?.let {
                    category = classifyTDR(it)
                }
            }

            category
        }
    }


    /**
     * Algorithme de classification du résultat de l'anlyse du nombre de ligne et de triangle du TDR
     *
     */
    fun classifyTDR(objects: List<Recognition?>, vararg detectedString: String) : Int{

        var category = -1 // Default category

        val CATEGORY_INVALID = 2
        val CATEGORY_POSITIVE = 1
        val CATEGORY_NEGATIVE = 0

        val lines: ArrayList<Recognition> = ArrayList() // list of detected lines

        val triangles: ArrayList<Recognition> = ArrayList() // list of detected triangles


        // get all lines and triangles objects

        // get all lines and triangles objects
        for (obj in objects) {
            if (obj?.title == LINE) lines.add(obj)
            if (obj?.title == TRIANGLE) triangles.add(obj)
        }

        // sort to have control-line before test-line in list (ceci pour s'assurer que la liste des lignes soit classee par ordre croissant
        // sur les ordonnees du centre des lignes)

        // sort to have control-line before test-line in list (ceci pour s'assurer que la liste des lignes soit classee par ordre croissant
        // sur les ordonnees du centre des lignes)
        lines.sortBy { it.location.centerY() }
        // sort to have control-flag before test flag in list
        // sort to have control-flag before test flag in list
        triangles.sortBy { it.location.centerY() }


        val nbLines: Int = lines.size // Le nombre de lignes detectees


/*        // Case of 2 lines-RDT
        when(triangles.size){

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

                    2 -> category =
                        CATEGORY_POSITIVE // means control and test lines are present and equals to number of traingles, then 'positive' test

                }
            }

            3 -> {
                // case of 3-lines RDT
                when (nbLines) {

                    0 -> category =
                        CATEGORY_INVALID // means all attempted lines are present, then 'positive'

                    1 -> {
                        // means just one line are present
                        // if the line is near of control-flag then 'negative', otherwise 'invalid'
                        val line = lines[0]
                        val controlTriangle = triangles[0]
                        val test1Triangle = triangles[1]
                        val test2Triangle = triangles[2]
                        val delta1 =
                            abs(line.location.centerY() - controlTriangle.location.centerY())
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
                        val line1Delta1 =
                            abs(line1.location.centerY() - controlTriangle.location.centerY())
                        val line1Delta2 =
                            abs(line1.location.centerY() - test1Triangle.location.centerY())
                        val line1Delta3 =
                            abs(line1.location.centerY() - test2Triangle.location.centerY())
                        category = if (line1Delta1 != min(
                                line1Delta1,
                                min(line1Delta2, line1Delta3)
                            )
                        ) {
                            // means the lines are not near of the control-flag, then 'invalid'
                            CATEGORY_INVALID
                        } else {
                            CATEGORY_POSITIVE
                        }
                    }

                    3 -> category =
                        CATEGORY_POSITIVE // means all attempted lines are present, then 'positive'
                }
            }

        }

        return category*/

        return Test.setResult(nbLines,triangles.size,lines,triangles)

    }


    /**
     * Méthode qui détecte les chaines de carractères présentes sur une image
     */
    fun getTextOnImage(bitmap: Bitmap?): String? {
        return if(bitmap!=null){
            tessOCR?.getOCRResult(bitmap)
        }else{
            null
        }
    }

}