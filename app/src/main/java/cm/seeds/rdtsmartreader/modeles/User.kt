package cm.seeds.rdtsmartreader.modeles

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import cm.seeds.rdtsmartreader.helper.GENRE_MASCULIN
import cm.seeds.rdtsmartreader.helper.generateCode

@Entity
data class User(

        @PrimaryKey
        var userId: String = "12",
        var epiId : String = generateCode(10),
        var userName: String = "Arthur Krawist",
        var userDomicile: String = "Mballa 2",
        var userAge: Int = 0,
        var genre: String = GENRE_MASCULIN,
        var telephone: String = "690142761",
        var dateEnregistrement: Long = System.currentTimeMillis(),
        var synchronised: Boolean = false,
        var profession: String = "Etudiant",
        var ville: String = "Yaounde",
        var region: String = "Centre",
        var aireDeSante: String = "Djoungolo",
        var district: String = "Djoungolo",
        var isDoctor: Boolean = false,
        var professionPersonnelSante: String = "",
        var isAlive: Boolean = true,
        var dateDeces: Long = 0,
        var asymptomatique: Boolean = false,
        var dateDebutSymptomes: Long = 0,

        var fievre: String = "",
        var frisson: String = "",
        var toux: String = "",
        var malDeGorge: String = "",
        var ecoulementNasal: String = "",
        var vomissement: String = "",
        var diarhee: String = "",
        var perteOdorat: String = "",
        var eruptionCutanee: String = "",
        var conjonctivite: String = "",
        var essouflement: String = "",
        var douleursMusculaires: String = "",
        var difficultesARespirerer: String = "",
        var perteSaveur: String = "",
        var fatigueIntense: String = "",
        var autres: String = "",

        var isAgeInYear : Boolean = true,

        var autresProfession: String? = "",

        @Embedded
        var coordonnee: Coordonnee?,

        @Embedded
        var test: Test? = null
) {


}
