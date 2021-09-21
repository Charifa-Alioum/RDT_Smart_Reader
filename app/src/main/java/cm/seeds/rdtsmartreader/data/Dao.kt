package cm.seeds.rdtsmartreader.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import cm.seeds.rdtsmartreader.modeles.Form
import cm.seeds.rdtsmartreader.modeles.Image
import cm.seeds.rdtsmartreader.modeles.Test
import cm.seeds.rdtsmartreader.modeles.User


/**
 * Différentes méthodes sur les formulaires
 * @see getAllForms
 * @see saveForms
 */
@Dao
interface Dao {

    /**
     * Sauvegarde un utilisateur dans la BD
     */
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(users : List<User>)

    /**
     * Supprime un utilisateur
     */
    @Delete
    suspend fun deleteUser(users : List<User>)

    /**
     * Retourne la liste des différents utilisateur de présent dans la BD
     */
    @Query("SELECT * FROM user ORDER BY dateEnregistrement DESC")
    fun getUsers() : LiveData<List<User>>


    /**
     * Retourne toutes les professions présentes dans la BD afin de faciliter les saisies
     */
    @Query("SELECT DISTINCT profession FROM user ORDER BY profession ASC")
    fun getProfessions() : LiveData<List<String>>

    /**
     * Retourne tous les domiciles présentes dans la BD afin de faciliter les saisies
     */
    @Query("SELECT  DISTINCT userDomicile FROM user ORDER BY userDomicile ASC")
    fun getDomiciles() : LiveData<List<String>>


    /**
     * Retourne toutes les villes présentes dans la BD afin de faciliter les saisies
     */
    @Query("SELECT DISTINCT ville FROM user ORDER BY ville ASC")
    fun getVilles() : LiveData<List<String>>


    /**
     * Retourne toutes les aires de santé présentes dans la BD afin de faciliter les saisies
     */
    @Query("SELECT DISTINCT aireDeSante FROM user ORDER BY aireDeSante ASC")
    fun getAiresDeSantes() : LiveData<List<String>>


    /**
     * Retourne tous les sistrits présentes dans la BD afin de faciliter les saisies
     */
    @Query("SELECT DISTINCT district FROM user ORDER BY district ASC")
    fun getDisctrics() : LiveData<List<String>>


    /**
     * Retourne la liste des formulaires
     */
    @Query("SELECT * FROM form ORDER BY formId")
    fun getAllForms() : LiveData<List<Form>>


    /**
     * Méthode perméttant d'enregistrer un formulateur dans le BD
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveForms(forms : List<Form>)


    /**
     * Méthode d'enregistreemnt des images prises
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveImages(images: List<Image>)


    /**
     * Méthode qui retourne la liste des images dans l'application
     */
    @Query("SELECT * FROM image ORDER BY imageKey DESC")
    fun getImages() : LiveData<List<Image>>



/*    *//**
     * Retourne tous les noms de manipulateur présentes dans la BD afin de faciliter les saisies
     *//*
    @Query("SELECT DISTINCT manipulateur  FROM Test ORDER BY manipulateur ASC")
    fun getNomsManipulateur() : LiveData<List<String>>*/

}