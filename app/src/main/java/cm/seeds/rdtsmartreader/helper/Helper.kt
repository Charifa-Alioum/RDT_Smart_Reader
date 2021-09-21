package cm.seeds.rdtsmartreader.helper

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.*
import android.net.Uri
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.imagedecoder.Classifier
import cm.seeds.rdtsmartreader.modeles.Coordonnee
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextInt


/**
 * Affiche un Toast
 */
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


/**
 * Methode utilitaire permettant d'afficher n'importe qu'elle vue
 */
fun View.show() {
    this.visibility = View.VISIBLE
}


/**
 * Methode utilitaire permettant de cacher n'importe qu'elle vue
 */
fun View.gone() {
    this.visibility = View.GONE
}


/**
 * @param string
 * Vérifie si la chaine de carractère passée en paramètre est un numéro de téléphone
 * @return true si c'est le cas et false sinon
 */
fun isPhoneNumber(string: String): Boolean {
    return if (TextUtils.isEmpty(string)) {
        false
    } else {
        if (PhoneNumberUtils.isGlobalPhoneNumber(string)) {
            if (string.startsWith("+237")) {
                // quand ca commence par "+" on ajoute jusqu'a 4 carracteres
                string.length >= 13
            } else {
                string.length >= 9
            }
        } else {
            //loginEditText.setError(context.getString(R.string.inserer_numero_de_telephone_ou_email));
            false
        }
    }
}

/**
 * @param context le contexte à utiliser
 * @param permission la permission demandée
 * @param show défini s'il faut affciher la boite de dialogue ou pas
 * @param toDoOnButtonCLick permet de savoir quoi faire quand on clique sur les boutons
 * @return le AlertDialog construit
 */
fun getDialogForPermissionDetails(context: Context, permission: String, show: Boolean, toDoOnButtonCLick: DialogInterface.OnClickListener): Dialog {

    var content = ""
    when (permission) {
        Manifest.permission.CAMERA -> {
            content = context.getString(R.string.camera_permission_explanation)
        }

        Manifest.permission.ACCESS_FINE_LOCATION -> {
            content = context.getString(R.string.permission_message_for_localisation)
        }

        Manifest.permission.ACCESS_COARSE_LOCATION -> {
            content = context.getString(R.string.permission_message_for_localisation)
        }
    }

    val dialogBuilder = MaterialAlertDialogBuilder(context)
            .setMessage(content)
            .setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_background))
            .setTitle(context.getString(R.string.besoin_permission))
            .setPositiveButton(context.getString(R.string.autoriser), toDoOnButtonCLick)
            .setNegativeButton(context.getString(R.string.refuser), toDoOnButtonCLick)

    if (show) {
        dialogBuilder.show()
    }

    return dialogBuilder.create()
}


/**
 * Retourne la boite une boite de dialog présentant une interface d'attente
 */
fun getLoadingDialog(context: Context, cancelable : Boolean = false): Dialog {
    return LoadingDialog(context).apply {
        setCancelable(cancelable)
    }
}

/**
 * Affiche une texte à l'utilisateur sous forme de boite de dialogue
 */
fun showMessage(context: Context, title: String, message: String?) {

    val actionListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {

            DialogInterface.BUTTON_POSITIVE -> dialog.dismiss()

        }
    }

    MaterialAlertDialogBuilder(context)
            .setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_background))
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.ok), actionListener)
            .show()

}

/**
 * Retourne le nombre d'item de la taille fourni en paramètre
 */
fun numberOfItemInLine(activity: Activity, dimenRes: Int): Int {
    val display = activity.windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    val with = size.x
    val oneItemWidth = activity.resources.getDimension(dimenRes)
    val numberOfItemInLine = with / oneItemWidth

    return numberOfItemInLine.toInt()
}

/**
 * Charge une image dans un imageView en utilisant la référence de celui ci
 * @param imageView
 * @param imageReference
 * @param isCircle
 */
fun loadImageInView(imageView: ImageView, imageReference: Any?, defaultResourceId: Int = R.drawable.default_res, isCircle: Boolean = false) {

    var glideManger = when (imageReference) {

        is String -> Glide.with(imageView).load(imageReference).error(defaultResourceId)

        is Int -> Glide.with(imageView).load(imageReference).error(defaultResourceId)

        is Uri -> Glide.with(imageView).load(imageReference).error(defaultResourceId)

        else -> null
    }

    if (glideManger != null) {

        if (isCircle) {
            glideManger = glideManger.circleCrop()
        }

        glideManger = glideManger.transition(DrawableTransitionOptions.withCrossFade())

        glideManger.into(imageView)

    }
}


/**
 * Transforme un long en une chaine de carractère
 */
fun formatDate(date: Long?, format: String): String {
    return try {
        SimpleDateFormat(format).format(date)
    } catch (parseException: ParseException) {
        ""
    }
}


/**
 * Affiche une boite de dialogue demandant une confirmation à l'utilisateur
 */
fun getConfirmationDialog(context: Context, titre: String, message: String, positiveText: String, negativeText: String, onButtonClick: DialogInterface.OnClickListener) {

    MaterialAlertDialogBuilder(context)
            .setBackground(context.getDrawable(R.drawable.dialog_background))
            .setTitle(titre)
            .setMessage(message)
            .setPositiveButton(positiveText, onButtonClick)
            .setNegativeButton(negativeText, onButtonClick)
            .show()
}


/**
 * Enregistre une localisation dans les préférences
 */
fun saveLocation(context: Context, localisation: Coordonnee) {
    context.getSharedPreferences(PREFERENCES_LOCALISATION, Context.MODE_PRIVATE)
            .edit()
            .putString(PREFERENCES_LOCALISATION, Gson().toJson(localisation))
            .apply()
}


/**
 * Génère un code de taille [lenth] pour différente vérification
 * @param lenth
 * @return un code de taille [lenth]
 */
fun generateCode(lenth: Int): String {
    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..lenth)
            .map { nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
}

/**
 * Charge la derniere localisation enregistréé dans les préférences
 */
fun loadLocation(context: Context) : Coordonnee?{
    val coordonneesString = context.getSharedPreferences(PREFERENCES_LOCALISATION,Context.MODE_PRIVATE).getString(PREFERENCES_LOCALISATION,null)
    if(coordonneesString!=null){
        return Gson().fromJson(coordonneesString,Coordonnee::class.java)
    }
    return null
}