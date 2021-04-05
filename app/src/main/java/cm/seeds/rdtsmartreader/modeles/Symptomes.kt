package cm.seeds.rdtsmartreader.modeles

data class Symptomes(

        val nomSymptoms : String,
        val codeSymptome : Int
){

    companion object{

        const val CODE_SYMPTOME_FIEVRE = 1
        const val CODE_SYMPTOME_FRISSON = 2
        const val CODE_SYMPTOME_TOUX = 3
        const val CODE_SYMPTOME_MAL_GORGE = 4
        const val CODE_SYMPTOME_ECOULEMENT_NASAL = 5
        const val CODE_SYMPTOME_VOMISSEMENT = 6
        const val CODE_SYMPTOME_DIARHEE = 7
        const val CODE_SYMPTOME_PERTE_ODORAT = 8
        const val CODE_SYMPTOME_ERUPTION_CUTANNE = 9
        const val CODE_SYMPTOME_CONJONCTIVITE = 10
        const val CODE_SYMPTOME_ESSOUFLEMENT = 11
        const val CODE_SYMPTOME_DOULEURS_MUSCULAIRES = 12
        const val CODE_SYMPTOME_DIFFICULTES_A_RESPIRER = 13
        const val CODE_SYMPTOME_PERTE_SAVEUR = 14
        const val CODE_SYMPTOME_FATIGUE_INTENSE = 15

    }

}
