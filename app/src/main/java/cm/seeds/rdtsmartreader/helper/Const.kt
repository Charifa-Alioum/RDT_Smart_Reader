package cm.seeds.rdtsmartreader.helper

import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavOptions
import cm.seeds.rdtsmartreader.R

const val REQUEST_CODE_CAMERA_PERMISSION = 1
const val REQUEST_CODE_CAPTURE_IMAGE = 2
const val REQUEST_CODE_LOCATION_PERMISSION = 3
const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 4
const val REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 5
const val REQUEST_CODE_CHOOSE_IMAGE = 6
const val REQUEST_CODE_ACTIVITY_IMAGE_CAPTURE = 8


/**
 * Différents types de questions lors de la création formulaire
 */
const val TYPE_QUESTION_SELECT_ONE = "Select Unique"
const val TYPE_QUESTION_SELECT_MULTIPLE = "Select Multiple"
const val TYPE_QUESTION_TEXT = "Texte"
const val TYPE_QUESTION_DATE = "Date"
const val TYPE_QUESTION_CHECKOX = "Checkbox"
const val TYPE_QUESTION_RADIO = "Radio"
const val TYPE_QUESTION_NUMBER_ENTIER = "Nombre Entier"
const val TYPE_QUESTION_NUMBER_REEL = "Nombre Réel"
const val TYPE_QUESTION_YES_OR_NO = "Oui / Non"


const val MINIMUM_CONFIDENCE_TF_OD_API = 0.4f
const val TF_OD_API_INPUT_SIZE = 640

const val TF_OD_API_IS_QUANTIZED = false

const val TF_OD_API_MODEL_FILE = "yolov5s.tflite"

const val TF_OD_API_LABELS_FILE = "file:///android_asset/data.txt"

const val DATE_FORMAT = "dd MMM yyyy"

/**
 * Différentes natures de Test
 */
const val NATURE_TEST_COVID_19_AG = "COVID19 Ag"
const val NATURE_TEST_COVID_19_AC = "COVID19 Ac"


/**
 * Différents types de prélèvement
 */
const val TYPE_PRELEVEMENT_NASOPHARYNGE = "Nasopharyngé"


/**
 * Différentes indications de prélèvement
 */
const val INDICATION_PRELEVEMENT_VOLONTAIRE = "Volontaire"
const val INDICATION_PRELEVEMENT_CONTROLE = "Controle"


/**
 * Différents résultats possible
 */
const val RESULTATS_TEST_PRESENCE_SARS_COV_AG = "Présence SARS Cov Ag"
const val RESULTATS_TEST_ABSCENCE_SARS_COV_AG = "Abscence SARS Cov Ag"
const val RESULTATS_TEST_INDETERMINE = "Indéterminé"


/**
 * Différentes conclusio d'in test
 */
const val CONCLUSION_POSITIF = "Test Positif"
const val CONCLUSION_NEGATIF = "Test Négatif"
const val CONCLUSION_INDETERMINE = "Test Indéterminé"
const val CONCLUSION_INVALIDE = "Test Invalide"
const val CONCLUSION_POSITIVE_AGENT_ONE = "Agent 1 positif"
const val CONCLUSION_POSITIVE_BOTH_AGENT = "Tous les agents sont positifs"
const val CONCLUSION_POSITIVE_AGENT_TWO = "Agent 2 positif"


/**
 * Différentes catégories
 */
const val CATEGORY_NEGATIVE = 0
const val CATEGORY_POSITIVE = 1
const val CATEGORY_INVALID = 2
const val CATEGORY_POSTIVE_BOTH_AGENT = 3
const val CATEGORY_POSTIVE_AGENT_ONE = 4
const val CATEGORY_POSTIVE_AGENT_TWO = 5


/**
 * Différents genre
 */
const val GENRE_MASCULIN = "Masculin"
const val GENRE_FEMININ = "Féminin"


/**
 * symptomes des maladies
 */
const val SYMPTOMES_FIEVRE = "Fièvre( T° >= 38 °C)"
const val SYMPTOMES_FRISSON = "Frisson"
const val SYMPTOMES_TOUX = "Toux"
const val SYMPTOMES_MAL_DE_GORGE = "Mal de groge"
const val SYMPTOMES_ECOULEMENT_NASAL = "Ecoulement nsal"
const val SYMPTOMES_VOMISSEMENTS = "Vomissements"
const val SYMPTOMES_DIARHEE = "Diarhée"
const val SYMPTOMES_PERTE_ODORAT = "Perte de l'odorat"
const val SYMPTOMES_ERUPTION_CUTANNEE = "Eruption cutanée"
const val SYMPTOMES_CONJONCTIVITE = "Conjonctivité"
const val SYMPTOMES_ESSOUFLEMENT = "Essouflement"
const val SYMPTOMES_DOULEURS_MUSCULAIRES = "Douleurs musculaires"
const val SYMPTOMES_DIFFICULTES_A_RESPIRER = "Difficultés à respirer"
const val SYMPTOMES_PERTE_DE_SAVEUR = "Perte de saveur"
const val SYMPTOMES_FATIGUE_INTENSE = "Fatigue intense"

/**
 * le navOptions utlisé pour la avigation entre les fragments
 */
val navOptions = NavOptions.Builder()
    .setEnterAnim(R.anim.fade_in)
    .setExitAnim(R.anim.fade_out)
    .setPopExitAnim(R.anim.fade_out)
    .setPopEnterAnim(R.anim.fade_in)
    .build()

/**
 * duréé minimale de mise à jour de la derniére localisation connue
 * exprimé en millisecondes
 */
const val MIN_TIME_LOCATION_UPDATE = 10000.toLong()


/**
 * distance minimale de mise à jour de la derniére localisation connue
 * exprimé en mètre
 */
const val MIN_DISATNCE_LOCATION_UPDATE = 10f


/**
 * Liste des metiers de professionnel de sante
 */
var listProfessionnelSante = listOf(
        "Medecin","Infirmier","Laborantin","Hygieniste"
).sorted()


/**
 * Liste des métiers possibles
 */
var listAllProfession = listOf(
        "Employé(e) d'etat",
        "Employé(e) dans le privé",
        "Sans Emploi",
        "Etudiant(e)",
        "Travailleur dans le secteur informel",
        "Eleve"
).sorted()

/**
 * clé de la préférence permettant de savoir la position actuelle de l'enregistrement
 */
const val PREFERENCES_LOCALISATION = "cle_localisation"



val mapRegionAndDistrict = hashMapOf<String,List<String>>(
        "Adamaoua" to listOf<String>(
                "Tignre","Banyo","Bankim","Djohong","Meiganga","Ngaoundere Rural","Ngaoundal","Tibati","Ngaoundere Urbain"
        ),
        "Sud" to listOf<String>(
                "Olamze","Ambam","Kribi","Mvangan","Djoum","Ebolowa","Lolodorf","Zoetele","Sangmelima","Meyomessala",
        ),
        "Sud Ouest" to listOf<String>(
                "Eyumojock","Limbe","Buea","Tiko","Muyuka","Mbongue","Kumba","Konye","Tombel","Ekondo Titi","Bakassi","Bangem","Nguti","Fontem","Wabane","Mamfe","Akwaya","Mumdemba"
        ),
        "Nord Ouest" to listOf<String>(
                "Nkambe"
        ),
        "Littoral" to listOf<String>(
                "Boko","Manoka","Bonassama"
        ),
        "Nord" to listOf<String>(
                "ds"
        ),
        "Extreme Nord" to listOf<String>(
                "sd"
        ),
        "Centre" to listOf<String>(),
        "Est" to listOf<String>(),
        "Ouest" to listOf<String>(),
)

val mapDistrictAireSante = HashMap<String,List<String>>()