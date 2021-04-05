package cm.seeds.rdtsmartreader.ui.main.informations

import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcel
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.view.get
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.data.ViewModelFactory
import cm.seeds.rdtsmartreader.data.Status
import cm.seeds.rdtsmartreader.databinding.AddInformationsBottomSheetLayoutBinding
import cm.seeds.rdtsmartreader.helper.*
import cm.seeds.rdtsmartreader.modeles.Symptomes
import cm.seeds.rdtsmartreader.modeles.Test
import cm.seeds.rdtsmartreader.modeles.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.radiobutton.MaterialRadioButton
import java.lang.NumberFormatException
import java.text.ParseException
import java.text.SimpleDateFormat

class AddInformationsBottomSheetFragment : BottomSheetDialogFragment() {

    private val listOfSymptomes = mutableListOf<Symptomes>(

            Symptomes("Fièvre (T° > 38 °C)",Symptomes.CODE_SYMPTOME_FIEVRE),
            Symptomes("Frisson",Symptomes.CODE_SYMPTOME_FRISSON),
            Symptomes("Toux", Symptomes.CODE_SYMPTOME_TOUX),
            Symptomes("Mal de Gorge", Symptomes.CODE_SYMPTOME_MAL_GORGE),
            Symptomes("Ecoulement Nasal", Symptomes.CODE_SYMPTOME_ECOULEMENT_NASAL),
            Symptomes("Vomissements", Symptomes.CODE_SYMPTOME_VOMISSEMENT),
            Symptomes("Diarrhée",Symptomes.CODE_SYMPTOME_DIARHEE),
            Symptomes("Perte de l'odorat",Symptomes.CODE_SYMPTOME_PERTE_ODORAT),
            Symptomes("Eruption cutannée",Symptomes.CODE_SYMPTOME_ERUPTION_CUTANNE),
            Symptomes("Conjonctivite",Symptomes.CODE_SYMPTOME_CONJONCTIVITE),
            Symptomes("Essouflement",Symptomes.CODE_SYMPTOME_ESSOUFLEMENT),
            Symptomes("Douleurs musculaires",Symptomes.CODE_SYMPTOME_DOULEURS_MUSCULAIRES),
            Symptomes("Difficultés à respirer",Symptomes.CODE_SYMPTOME_DIFFICULTES_A_RESPIRER),
            Symptomes("Perte de saveur",Symptomes.CODE_SYMPTOME_PERTE_SAVEUR),
            Symptomes("Fatigue intense",Symptomes.CODE_SYMPTOME_FATIGUE_INTENSE),
    )

    companion object {
        const val TAG = "TAG_ADD_INFORMATIONS_BOTTOM_SHEET"
    }

    private lateinit var dataBinding: AddInformationsBottomSheetLayoutBinding
    private lateinit var informationsViewModel: InformationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        informationsViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(requireActivity().application)).get(InformationsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.add_informations_bottom_sheet_layout, container, false)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        addActionsonViews()

        attachObservers()

        updateView()

    }

    private fun updateView() {

        dataBinding.edittextGenrePatient.setAdapter(ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1, listOf(GENRE_MASCULIN, GENRE_FEMININ)))

        dataBinding.edittextProfessionnelSante.setAdapter(ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1, listProfessionnelSante))

        dataBinding.professionPatient.setAdapter(ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1, listAllProfession))

        openView(0)

        val user = informationsViewModel.userToSave.value

        listOfSymptomes.forEach {
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_symptome, dataBinding.layoutAllSymptome, false)

            view.findViewById<TextView>(R.id.label_symptoms).text = it.nomSymptoms
            val positiveRadio = view.findViewById<MaterialRadioButton>(R.id.oui)
            val negativeRadio = view.findViewById<MaterialRadioButton>(R.id.non)
            val neutreRadio = view.findViewById<MaterialRadioButton>(R.id.nsp)

            when(it.codeSymptome){

                Symptomes.CODE_SYMPTOME_FIEVRE -> {
                    negativeRadio.isChecked = user?.fievre?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.fievre?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.fievre?.equals("Nsp",true) == true
                }

                Symptomes.CODE_SYMPTOME_FRISSON -> {
                    negativeRadio.isChecked = user?.frisson?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.frisson?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.frisson?.equals("Nsp",true) == true
                }

                Symptomes.CODE_SYMPTOME_TOUX -> {
                    negativeRadio.isChecked = user?.toux?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.toux?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.toux?.equals("Nsp",true) == true
                }

                Symptomes.CODE_SYMPTOME_MAL_GORGE -> {
                    negativeRadio.isChecked = user?.malDeGorge?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.malDeGorge?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.malDeGorge?.equals("Nsp",true) == true
                }

                Symptomes.CODE_SYMPTOME_ECOULEMENT_NASAL -> {
                    negativeRadio.isChecked = user?.ecoulementNasal?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.ecoulementNasal?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.ecoulementNasal?.equals("Nsp",true) == true
                }

                Symptomes.CODE_SYMPTOME_FATIGUE_INTENSE -> {
                    negativeRadio.isChecked = user?.fatigueIntense?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.fatigueIntense?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.fatigueIntense?.equals("Nsp",true) == true
                }

                Symptomes.CODE_SYMPTOME_VOMISSEMENT -> {
                    negativeRadio.isChecked = user?.vomissement?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.vomissement?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.vomissement?.equals("Nsp",true) == true
                }

                Symptomes.CODE_SYMPTOME_DIARHEE -> {
                    negativeRadio.isChecked = user?.diarhee?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.diarhee?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.diarhee?.equals("Nsp",true) == true
                }

                Symptomes.CODE_SYMPTOME_PERTE_ODORAT -> {
                    negativeRadio.isChecked = user?.perteOdorat?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.perteOdorat?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.perteOdorat?.equals("Nsp",true) == true
                }

                Symptomes.CODE_SYMPTOME_ERUPTION_CUTANNE -> {
                    negativeRadio.isChecked = user?.eruptionCutanee?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.eruptionCutanee?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.eruptionCutanee?.equals("Nsp",true) == true
                }

                Symptomes.CODE_SYMPTOME_CONJONCTIVITE -> {
                    negativeRadio.isChecked = user?.conjonctivite?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.conjonctivite?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.conjonctivite?.equals("Nsp",true) == true
                }

                Symptomes.CODE_SYMPTOME_ESSOUFLEMENT -> {
                    negativeRadio.isChecked = user?.essouflement?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.essouflement?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.essouflement?.equals("Nsp",true) == true
                }

                Symptomes.CODE_SYMPTOME_DOULEURS_MUSCULAIRES -> {
                    negativeRadio.isChecked = user?.douleursMusculaires?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.douleursMusculaires?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.douleursMusculaires?.equals("Nsp",true) == true
                }

                Symptomes.CODE_SYMPTOME_DIFFICULTES_A_RESPIRER -> {
                    negativeRadio.isChecked = user?.difficultesARespirerer?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.difficultesARespirerer?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.difficultesARespirerer?.equals("Nsp",true) == true
                }

                Symptomes.CODE_SYMPTOME_PERTE_SAVEUR -> {
                    negativeRadio.isChecked = user?.perteSaveur?.equals("Non",true) == true
                    positiveRadio.isChecked = user?.perteSaveur?.equals("Oui",true) == true
                    neutreRadio.isChecked = user?.perteSaveur?.equals("Nsp",true) == true
                }

            }

            val onCkeckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->

                if(isChecked){

                    when(it.codeSymptome){

                        Symptomes.CODE_SYMPTOME_FIEVRE -> {
                            informationsViewModel.userToSave.value?.fievre = buttonView.text.toString()
                        }

                        Symptomes.CODE_SYMPTOME_FRISSON -> {
                            informationsViewModel.userToSave.value?.frisson = buttonView.text.toString()
                        }

                        Symptomes.CODE_SYMPTOME_TOUX -> {
                            informationsViewModel.userToSave.value?.toux = buttonView.text.toString()
                        }

                        Symptomes.CODE_SYMPTOME_MAL_GORGE -> {
                            informationsViewModel.userToSave.value?.malDeGorge = buttonView.text.toString()
                        }

                        Symptomes.CODE_SYMPTOME_ECOULEMENT_NASAL -> {
                            informationsViewModel.userToSave.value?.ecoulementNasal = buttonView.text.toString()
                        }

                        Symptomes.CODE_SYMPTOME_FATIGUE_INTENSE -> {
                            informationsViewModel.userToSave.value?.fatigueIntense = buttonView.text.toString()
                        }

                        Symptomes.CODE_SYMPTOME_VOMISSEMENT -> {
                            informationsViewModel.userToSave.value?.vomissement = buttonView.text.toString()
                        }

                        Symptomes.CODE_SYMPTOME_DIARHEE -> {
                            informationsViewModel.userToSave.value?.diarhee = buttonView.text.toString()
                        }

                        Symptomes.CODE_SYMPTOME_PERTE_ODORAT -> {
                            informationsViewModel.userToSave.value?.perteOdorat = buttonView.text.toString()
                        }

                        Symptomes.CODE_SYMPTOME_ERUPTION_CUTANNE -> {
                            informationsViewModel.userToSave.value?.eruptionCutanee = buttonView.text.toString()
                        }

                        Symptomes.CODE_SYMPTOME_CONJONCTIVITE -> {
                            informationsViewModel.userToSave.value?.conjonctivite = buttonView.text.toString()
                        }

                        Symptomes.CODE_SYMPTOME_ESSOUFLEMENT -> {
                            informationsViewModel.userToSave.value?.essouflement = buttonView.text.toString()
                        }

                        Symptomes.CODE_SYMPTOME_DOULEURS_MUSCULAIRES -> {
                            informationsViewModel.userToSave.value?.douleursMusculaires = buttonView.text.toString()
                        }

                        Symptomes.CODE_SYMPTOME_DIFFICULTES_A_RESPIRER -> {
                            informationsViewModel.userToSave.value?.difficultesARespirerer = buttonView.text.toString()
                        }

                        Symptomes.CODE_SYMPTOME_PERTE_SAVEUR -> {
                            informationsViewModel.userToSave.value?.perteSaveur = buttonView.text.toString()
                        }

                    }

                }

            }

            positiveRadio.setOnCheckedChangeListener(onCkeckedChangeListener)
            negativeRadio.setOnCheckedChangeListener(onCkeckedChangeListener)
            neutreRadio.setOnCheckedChangeListener(onCkeckedChangeListener)

            dataBinding.layoutAllSymptome.addView(view)
        }

    }

    private fun attachObservers() {

        informationsViewModel.userToSave.observe(viewLifecycleOwner, {
            dataBinding.userToSave = it
            addDataToViews(it)
        })

        informationsViewModel.liveDataAllAireDeSante.observe(viewLifecycleOwner,{
            dataBinding.aireSantePatient.setAdapter(ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1,it))
        })

        informationsViewModel.liveDataAllDomicile.observe(viewLifecycleOwner,{
            dataBinding.edittextDomilePatient.setAdapter(ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1,it))
        })

        informationsViewModel.liveDataAllRegion.observe(viewLifecycleOwner,{
            dataBinding.regionPatient.setAdapter(ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1,it))
            //dataBinding.regionPatient.setText("")
        })

        informationsViewModel.liveDataAllDistrict.observe(viewLifecycleOwner,{
            dataBinding.districtPatient.setAdapter(ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1,it))
            //dataBinding.districtPatient.setText("")
        })

    }

    private fun addDataToViews(user: User) {

        // Les updates sur les informations personnelles de l'utlisateur
        dataBinding.edittextIdentifiants.setText(user.userId)

        dataBinding.edittextNomClient.setText(user.userName)

        dataBinding.professionPatient.setText(user.profession)

        dataBinding.edittextDomilePatient.setText(user.userDomicile)

        dataBinding.regionPatient.setText(user.region)

        dataBinding.districtPatient.setText(user.district)

        dataBinding.aireSantePatient.setText(user.aireDeSante)

        dataBinding.edittextTelephonePatient.setText(user.telephone)

        dataBinding.edittextGenrePatient.setText(user.genre)

        dataBinding.edittextAgePatient.setText(user.userAge.toString())
        dataBinding.radiobuttonAgeAnnee.isChecked = user.isAgeInYear
        dataBinding.radiobuttonAgeMois.isChecked = !user.isAgeInYear

        dataBinding.radiobuttonProfessionnelSanteOui.isChecked = user.isDoctor
        dataBinding.radiobuttonProfessionnelSanteNon.isChecked = !user.isDoctor
        if(dataBinding.radiobuttonProfessionnelSanteOui.isChecked){
            dataBinding.edittextProfessionnelSante.setText(user.professionPersonnelSante)
            dataBinding.layoutProfessionnelDante.visibility = VISIBLE
        }


        dataBinding.radiobuttonStatusCaseVivant.isChecked = user.isAlive
        dataBinding.radiobuttonStatusCaseMort.isChecked = !user.isAlive
        if(dataBinding.radiobuttonStatusCaseMort.isChecked){
            dataBinding.layoutDateDeces.visibility = VISIBLE
            dataBinding.dateDecesPatient.setText(parseDateToString(user.dateDeces, DATE_FORMAT))
        }


        //Update sur les symptomes
        dataBinding.radiobuttonAsymptomatiqueNon.isChecked = user.asymptomatique
        if(user.asymptomatique){
            dataBinding.layoutAllSymptome.visibility = VISIBLE
            dataBinding.dateDebutSymptomePatient.setText(parseDateToString(user.dateDebutSymptomes, DATE_FORMAT))
        }else{
            dataBinding.layoutAllSymptome.visibility = GONE
            dataBinding.dateDebutSymptomePatient.setText("")
        }

/*        if (user.genre) {
            dataBinding.radiobuttonGenreFeminin.isChecked = false
            dataBinding.radiobuttonGenreMasculin.isChecked = true
        } else {
            dataBinding.radiobuttonGenreFeminin.isChecked = true
            dataBinding.radiobuttonGenreMasculin.isChecked = false
        }*/


        // les updates sur le tests
        //type de test
        dataBinding.radiobuttonNatureTestCovidAc.isChecked = user.test?.natureTest == NATURE_TEST_COVID_19_AC
        dataBinding.radiobuttonNatureTestCovidAg.isChecked = user.test?.natureTest == NATURE_TEST_COVID_19_AG


        //type prélèvement
        dataBinding.radiobuttonTypePrelevementNasopharynge.isChecked = user.test?.typePrelevement == TYPE_PRELEVEMENT_NASOPHARYNGE
        dataBinding.radiobuttonTypePrelevementAutre.isChecked = user.test?.typePrelevement != TYPE_PRELEVEMENT_NASOPHARYNGE
        if(dataBinding.radiobuttonTypePrelevementAutre.isChecked){
            dataBinding.layoutAutreTypePrelevement.visibility = VISIBLE
            dataBinding.edittextAutresTypePrelevement.setText(user.test?.typePrelevement)
        }

        //Indications prélèvement
        dataBinding.radiobuttonIndicationsTestControle.isChecked = user.test?.indicationPrelevement == INDICATION_PRELEVEMENT_CONTROLE
        dataBinding.radiobuttonIndicationsTestVolontaire.isChecked = user.test?.indicationPrelevement == INDICATION_PRELEVEMENT_VOLONTAIRE
        dataBinding.radiobuttonIndicationsTestAutre.isChecked = (user.test?.indicationPrelevement != INDICATION_PRELEVEMENT_VOLONTAIRE) && (user.test?.indicationPrelevement != INDICATION_PRELEVEMENT_CONTROLE)
        if(dataBinding.radiobuttonIndicationsTestAutre.isChecked){
            dataBinding.edittextAutresIndicationsPrelevement.visibility = VISIBLE
            dataBinding.edittextAutresIndicationsPrelevement.setText(user.test?.indicationPrelevement)
        }

        //résultats
        if(dataBinding.radiobuttonNatureTestCovidAg.isChecked){

            dataBinding.layoutResultatsCovidAc.visibility = VISIBLE
            dataBinding.layoutResultatsCovidAg.visibility = GONE

            dataBinding.radiobuttonCovidAgPositif.isChecked = user.test?.resultatsCovidAg == CONCLUSION_POSITIF
            dataBinding.radiobuttonCovidAgNegatif.isChecked = user.test?.resultatsCovidAg == CONCLUSION_NEGATIF

        }else{

            dataBinding.layoutResultatsCovidAc.visibility = GONE
            dataBinding.layoutResultatsCovidAg.visibility = VISIBLE

            dataBinding.radiobuttonCovidIgcNegatif.isChecked = user.test?.resultatsCovidIgg == CONCLUSION_NEGATIF
            dataBinding.radiobuttonCovidIgcPositif.isChecked = user.test?.resultatsCovidIgg == CONCLUSION_POSITIF

            dataBinding.radiobuttonCovidIgmNegatif.isChecked = user.test?.resultatsCovidIgg == CONCLUSION_NEGATIF
            dataBinding.radiobuttonCovidIgmPositif.isChecked = user.test?.resultatsCovidIgg == CONCLUSION_POSITIF

        }


        //Conclusion
        dataBinding.radiobuttonConclusionIndetermine.isChecked = user.test?.conclusion == CONCLUSION_INDETERMINE
        dataBinding.radiobuttonConclusionNegatif.isChecked = user.test?.conclusion == CONCLUSION_NEGATIF
        dataBinding.radiobuttonConclusionPositif.isChecked = user.test?.conclusion == CONCLUSION_POSITIF

        //Infos sur le manipulateur
        dataBinding.edittextTelephoneManipulateur.setText(user.test?.telephoneManipulateur)
        dataBinding.edittextManipulateur.setText(user.test?.manipulateur)

    }

    private fun openView(position: Int) {

        when (position) {

            0 -> {
                dataBinding.cardInfoPersonnelles.visibility = VISIBLE
                dataBinding.cardInfosSymptomes.visibility = GONE
                dataBinding.cardInfosExamen.visibility = GONE
                dataBinding.pageLabel.text = getString(R.string.informations_personnelles)
                dataBinding.pageDetails.text = getString(R.string.details_infos_personnelles)
            }

            1 -> {
                if (infoIsOk(0)) {
                    dataBinding.cardInfoPersonnelles.visibility = GONE
                    dataBinding.cardInfosSymptomes.visibility = VISIBLE
                    dataBinding.cardInfosExamen.visibility = GONE
                    dataBinding.pageLabel.text = getString(R.string.symptome_cas)
                    dataBinding.pageDetails.text = getString(R.string.symptome_cas)
                } else {
                    showToast(requireContext(), getString(R.string.veuillez_bien_remplir_cette_page))
                }
            }

            2 -> {
                if (infoIsOk(1)) {
                    dataBinding.cardInfoPersonnelles.visibility = GONE
                    dataBinding.cardInfosSymptomes.visibility = GONE
                    dataBinding.cardInfosExamen.visibility = VISIBLE
                    dataBinding.pageLabel.text = getString(R.string.informations_test)
                    dataBinding.pageDetails.text = getString(R.string.informations_test)
                }
            }
        }

        setNextButtonText()

        setPreviousButtonText()

    }

    private fun infoIsOk(pagePosition: Int): Boolean {
        val user = informationsViewModel.userToSave.value ?: User(coordonnee = loadLocation(requireContext()))
        var isCorrect = true
        when (pagePosition) {

            0 -> {

                val id = dataBinding.edittextIdentifiants.text.toString()
                val names = dataBinding.edittextNomClient.text.toString()
                val profession = dataBinding.professionPatient.text.toString()
                val domicile = dataBinding.edittextDomilePatient.text.toString()
                val region = dataBinding.regionPatient.text.toString()
                val aireSante = dataBinding.aireSantePatient.text.toString()
                val district = dataBinding.districtPatient.text.toString()
                val telephone = dataBinding.edittextTelephonePatient.text.toString()
                val genre = dataBinding.edittextGenrePatient.text.toString()
                val isYear = dataBinding.radiobuttonAgeAnnee.isChecked
                val isProfessionnelSante = dataBinding.radiobuttonProfessionnelSanteOui.isChecked
                val isAlive = dataBinding.radiobuttonStatusCaseVivant.isChecked
                val dateMort = dataBinding.dateDecesPatient.text.toString()
                val professionSante = dataBinding.edittextProfessionnelSante.text.toString()


                val age = try {
                    dataBinding.edittextAgePatient.text.toString().toInt()
                } catch (e: NumberFormatException) {
                    -1
                }

                if (TextUtils.isEmpty(id)) {
                    dataBinding.edittextIdentifiants.error = ""
                    isCorrect = false
                } else {
                    user.userId = id
                    dataBinding.edittextIdentifiants.error = null
                }


                if (TextUtils.isEmpty(id)) {
                    dataBinding.edittextIdentifiants.error = ""
                    isCorrect = false
                } else {
                    user.userId = id
                    dataBinding.edittextIdentifiants.error = null
                }


                if (TextUtils.isEmpty(names)) {
                    dataBinding.edittextNomClient.error = ""
                    isCorrect = false
                } else {
                    user.userName = names
                    dataBinding.edittextNomClient.error = null
                }

                if (TextUtils.isEmpty(profession)) {
                    dataBinding.professionPatient.error = ""
                    isCorrect = false
                } else {
                    user.profession = profession
                    dataBinding.professionPatient.error = null
                }

                if (TextUtils.isEmpty(domicile)) {
                    dataBinding.edittextDomilePatient.error = ""
                    isCorrect = false
                } else {
                    user.userDomicile = domicile
                    dataBinding.edittextDomilePatient.error = null
                }

                if (TextUtils.isEmpty(region)) {
                    dataBinding.regionPatient.error = ""
                    isCorrect = false
                } else {
                    user.ville = region
                    dataBinding.regionPatient.error = null
                }

                if (TextUtils.isEmpty(aireSante)) {
                    dataBinding.aireSantePatient.error = ""
                    isCorrect = false
                } else {
                    user.aireDeSante = aireSante
                    dataBinding.aireSantePatient.error = null
                }

                if (TextUtils.isEmpty(district)) {
                    dataBinding.districtPatient.error = ""
                    isCorrect = false
                } else {
                    user.district = district
                    dataBinding.districtPatient.error = null
                }

                if (TextUtils.isEmpty(telephone)) {
                    dataBinding.edittextTelephonePatient.error = ""
                    isCorrect = false
                } else {
                    user.telephone = telephone
                    dataBinding.edittextTelephonePatient.error = null
                }

                user.genre = genre
                user.isAgeInYear = isYear

                if (age <= 0) {
                    dataBinding.edittextAgePatient.error = ""
                    isCorrect = false
                } else {
                    user.userAge = age
                    dataBinding.edittextAgePatient.error = null
                }

                user.isDoctor = isProfessionnelSante

                if(isProfessionnelSante){
                    if(TextUtils.isEmpty(professionSante)){
                        dataBinding.edittextProfessionnelSante.error = ""
                        isCorrect = false
                    }else{
                        dataBinding.edittextProfessionnelSante.error = null
                        user.professionPersonnelSante = professionSante
                    }
                }


                user.isAlive = isAlive

                if (!isAlive) {
                    if (TextUtils.isEmpty(dateMort)) {
                        dataBinding.dateDecesPatient.error = ""
                        isCorrect = false
                    } else {
                        user.dateDeces = try {
                            SimpleDateFormat(DATE_FORMAT).parse(dateMort).time
                        } catch (e: ParseException) {
                            isCorrect = false
                            dataBinding.dateDecesPatient.error = ""
                            0
                        }
                    }

                }

            }

            1 -> {

                val asymptomatique = dataBinding.radiobuttonAsymptomatiqueOui.isChecked
                val dateDebutSymptomes = dataBinding.dateDebutSymptomePatient.text.toString()

                user.asymptomatique = asymptomatique

                if(!asymptomatique){
                    if (TextUtils.isEmpty(dateDebutSymptomes)) {
                        isCorrect = false
                        dataBinding.dateDebutSymptomePatient.error = ""
                    } else {
                        user.dateDebutSymptomes = try {
                            SimpleDateFormat(DATE_FORMAT).parse(dateDebutSymptomes).time
                        } catch (e: ParseException) {
                            isCorrect = false
                            dataBinding.dateDebutSymptomePatient.error = ""
                            0
                        }
                    }



                    var numbersSymptoms = 0

                    for (index in 0 until listOfSymptomes.size) {
                        val view = dataBinding.layoutAllSymptome[index + 1]

                        if (
                                view.findViewById<MaterialRadioButton>(R.id.oui).isChecked ||
                                view.findViewById<MaterialRadioButton>(R.id.non).isChecked ||
                                view.findViewById<MaterialRadioButton>(R.id.nsp).isChecked
                        ) {
                            numbersSymptoms++
                        }
                    }

                    if (numbersSymptoms <= 0) {
                        isCorrect = false
                        showToast(requireContext(), "Le cas doit avoir au moins un symptomes, si ce n'est pas le cas veuillez cocher Asymptomatique")
                    }

                }


            }

            2 -> {

                val test = Test(
                        natureTest = when {
                            dataBinding.radiobuttonNatureTestCovidAc.isChecked -> NATURE_TEST_COVID_19_AC
                            dataBinding.radiobuttonNatureTestCovidAg.isChecked -> NATURE_TEST_COVID_19_AG
                            else -> ""
                        },
                        typePrelevement = when {
                            dataBinding.radiobuttonTypePrelevementNasopharynge.isChecked -> TYPE_PRELEVEMENT_NASOPHARYNGE
                            dataBinding.radiobuttonTypePrelevementAutre.isChecked -> {
                                val autre = dataBinding.edittextAutresTypePrelevement.text.toString()
                                if (TextUtils.isEmpty(autre))
                                    getString(R.string.autre)
                                else
                                    autre
                            }
                            else -> getString(R.string.autre)
                        },
                        indicationPrelevement = when {
                            dataBinding.radiobuttonIndicationsTestVolontaire.isChecked -> INDICATION_PRELEVEMENT_VOLONTAIRE
                            dataBinding.radiobuttonIndicationsTestControle.isChecked -> INDICATION_PRELEVEMENT_CONTROLE
                            dataBinding.radiobuttonIndicationsTestAutre.isChecked -> {
                                val autre = dataBinding.edittextAutresIndicationsPrelevement.text.toString()
                                if (TextUtils.isEmpty(autre))
                                    getString(R.string.autre)
                                else
                                    autre
                            }
                            else -> getString(R.string.autre)

                        },

                        resultatsCovidAg = when{
                            dataBinding.radiobuttonCovidAgNegatif.isChecked -> CONCLUSION_NEGATIF
                            dataBinding.radiobuttonCovidAgPositif.isChecked -> CONCLUSION_POSITIF
                            else -> ""
                        },
                        resultatsCovidIgg = when{
                            dataBinding.radiobuttonCovidIgcNegatif.isChecked -> CONCLUSION_NEGATIF
                            dataBinding.radiobuttonCovidIgcPositif.isChecked -> CONCLUSION_POSITIF
                            else -> ""
                        },
                        resultatsCovidIgm = when{
                            dataBinding.radiobuttonCovidIgmNegatif.isChecked -> CONCLUSION_NEGATIF
                            dataBinding.radiobuttonCovidIgmPositif.isChecked -> CONCLUSION_POSITIF
                            else -> ""
                        }
                )

                val nomManipulateur = dataBinding.edittextManipulateur.text.toString()
                val telephoneManipulateur = dataBinding.edittextTelephoneManipulateur.text.toString()

                if(test.natureTest == NATURE_TEST_COVID_19_AG){
                    if(!dataBinding.radiobuttonCovidAgPositif.isChecked && !dataBinding.radiobuttonCovidAgNegatif.isChecked){
                        isCorrect = false
                    }
                }else if(test.natureTest == NATURE_TEST_COVID_19_AC){

                    if(!dataBinding.radiobuttonCovidIgcPositif.isChecked && !dataBinding.radiobuttonCovidIgcNegatif.isChecked){
                        isCorrect = false
                    }

                    if(!dataBinding.radiobuttonCovidIgmPositif.isChecked && !dataBinding.radiobuttonCovidIgmNegatif.isChecked){
                        isCorrect = false
                    }

                }

                if(TextUtils.isEmpty(nomManipulateur)){
                    isCorrect = false
                    dataBinding.edittextManipulateur.error = ""
                }else{
                    dataBinding.edittextManipulateur.error = null
                    test.manipulateur = nomManipulateur
                }

                if(TextUtils.isEmpty(telephoneManipulateur)){
                    isCorrect = false
                    dataBinding.edittextTelephoneManipulateur.error = ""
                }else{
                    dataBinding.edittextTelephoneManipulateur.error = null
                    test.telephoneManipulateur = telephoneManipulateur
                }

                if(isCorrect){
                    informationsViewModel.testToSave.value = test
                }

            }

        }

        return isCorrect
    }

    private fun addActionsonViews() {

        dataBinding.regionPatient.doOnTextChanged { text, _, _, _ ->
            informationsViewModel.setupDistrictOf(text.toString())
        }

        dataBinding.radiobuttonNatureTestCovidAc.setOnCheckedChangeListener { _, isChecked ->
            dataBinding.layoutResultas.visibility = VISIBLE
            if(isChecked){
                dataBinding.layoutResultatsCovidAc.visibility = VISIBLE
                dataBinding.layoutResultatsCovidAg.visibility = GONE
            }else{
                dataBinding.layoutResultatsCovidAc.visibility = GONE
                dataBinding.layoutResultatsCovidAg.visibility = VISIBLE
            }

        }

        dataBinding.radiobuttonAsymptomatiqueOui.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                dataBinding.layoutAllSymptome.visibility = GONE
            } else {
                dataBinding.layoutAllSymptome.visibility = VISIBLE
            }
        }

        dataBinding.radiobuttonProfessionnelSanteOui.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                dataBinding.layoutProfessionnelDante.visibility = VISIBLE
            }else{
                dataBinding.layoutProfessionnelDante.visibility = GONE
            }
        }

        dataBinding.radiobuttonIndicationsTestAutre.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                dataBinding.layoutAutreIndicationsPrelevement.visibility = VISIBLE
            }else{
                dataBinding.layoutAutreIndicationsPrelevement.visibility = GONE
            }
        }


        dataBinding.radiobuttonTypePrelevementAutre.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                dataBinding.layoutAutreTypePrelevement.visibility = VISIBLE
            }else{
                dataBinding.layoutAutreTypePrelevement.visibility = GONE
            }
        }

        dataBinding.radiobuttonStatusCaseMort.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                dataBinding.layoutDateDeces.visibility = VISIBLE
            } else {
                dataBinding.layoutDateDeces.visibility = GONE
            }
        }

        dataBinding.buttonDateDebutSymptome.setOnClickListener {

            val datePicker = MaterialDatePicker
                    .Builder
                    .datePicker()
                    .setCalendarConstraints(CalendarConstraints
                            .Builder()
                            .setOpenAt(System.currentTimeMillis())
                            .setValidator(object : CalendarConstraints.DateValidator {
                                override fun isValid(date: Long): Boolean {
                                    val isCorrect = date < System.currentTimeMillis()
                                    dataBinding.dateDebutSymptomePatient.setText(parseDateToString(date, DATE_FORMAT))
                                    return isCorrect
                                }

                                override fun describeContents(): Int {
                                    return 0
                                }

                                override fun writeToParcel(dest: Parcel?, flags: Int) {

                                }
                            })
                            .build())
                    .setTitleText(getString(R.string.date_deces))
                    .build()

            datePicker.show(childFragmentManager, "DATE_PICKER_DEBUT_SYMPTOMES")
        }

        dataBinding.buttonDateDeces.setOnClickListener {
            val datePicker = MaterialDatePicker
                    .Builder
                    .datePicker()
                    .setCalendarConstraints(CalendarConstraints
                            .Builder()
                            .setOpenAt(System.currentTimeMillis())
                            .setValidator(object : CalendarConstraints.DateValidator {
                                override fun isValid(date: Long): Boolean {
                                    val isCorrect = date < System.currentTimeMillis()
                                    dataBinding.dateDecesPatient.setText(parseDateToString(date, DATE_FORMAT))
                                    return isCorrect
                                }

                                override fun describeContents(): Int {
                                    return 0
                                }

                                override fun writeToParcel(dest: Parcel?, flags: Int) {

                                }
                            })
                            .build())
                    .setTitleText(getString(R.string.date_deces))
                    .build()

            datePicker.show(childFragmentManager, "DATE_PICKER_DATE_DECES")
        }

        dataBinding.buttonScanCode.setOnClickListener {
            findNavController().navigate(R.id.scanFragment, null, navOptions)
        }

        dataBinding.buttonNext.setOnClickListener {

            when {

                dataBinding.cardInfoPersonnelles.visibility == VISIBLE -> {
                    openView(1)
                }

                dataBinding.cardInfosSymptomes.visibility == VISIBLE -> {
                    openView(2)
                }

                dataBinding.cardInfosExamen.visibility == VISIBLE -> {
                    if (infoIsOk(2)) {
                        checkAndSaveUser()
                    }
                }

            }

        }

        dataBinding.buttonPrevious.setOnClickListener {

            when {

                dataBinding.cardInfoPersonnelles.visibility == VISIBLE -> {

                    val onButtonClick = DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                        when (which) {

                            DialogInterface.BUTTON_POSITIVE -> {
                                dismiss()
                            }

                            DialogInterface.BUTTON_NEGATIVE -> {

                            }

                        }

                    }

                    getConfirmationDialog(requireContext(), "Avertissements", "Voulez vous vraiment arreter l'enregistrement? Les informations déja saisies seront perdues", "continuer", "fermer", onButtonClick)
                }

                dataBinding.cardInfosSymptomes.visibility == VISIBLE -> {
                    openView(0)
                }

                dataBinding.cardInfosExamen.visibility == VISIBLE -> {
                    openView(1)
                }

            }
        }

    }

    private fun setNextButtonText() {
        when {

            dataBinding.cardInfoPersonnelles.visibility == VISIBLE -> {
                dataBinding.buttonNext.text = getString(R.string.suivant)
            }

            dataBinding.cardInfosSymptomes.visibility == VISIBLE -> {
                dataBinding.buttonNext.text = getString(R.string.suivant)
            }

            dataBinding.cardInfosExamen.visibility == VISIBLE -> {
                dataBinding.buttonNext.text = getString(R.string.enregistrer)
            }

        }
    }

    private fun setPreviousButtonText() {
        when {

            dataBinding.cardInfoPersonnelles.visibility == VISIBLE -> {
                dataBinding.buttonPrevious.text = getString(R.string.annuler)
                dataBinding.buttonPrevious.isEnabled = true
            }

            dataBinding.cardInfosSymptomes.visibility == VISIBLE -> {
                dataBinding.buttonPrevious.text = getString(R.string.precedent)
                dataBinding.buttonPrevious.isEnabled = true
            }

            dataBinding.cardInfosExamen.visibility == VISIBLE -> {
                dataBinding.buttonPrevious.text = getString(R.string.precedent)
                dataBinding.buttonPrevious.isEnabled = true
            }

        }
    }

    private fun checkAndSaveUser() {

        informationsViewModel.saveUser()

        //informationsViewModel.userSavingResult?.removeObservers(viewLifecycleOwner)
        informationsViewModel.userSavingResult?.observe(viewLifecycleOwner, {

            val loadingDialog = getLoadingDialog(requireContext())
            //loadingDialog.dismiss()
            when (it.status) {

                Status.SUCCESS -> {
                    showToast(requireContext(), "Patient ${it.data?.userName} sauvegardé")
                    dismiss()
                }

                Status.ERROR -> {
                    //loadingDialog.dismiss()
                    showMessage(requireContext(), getString(R.string.erreur), it.message)
                }

                Status.LOADING -> {
                    //loadingDialog.show()
                }

            }

        })
    }


}