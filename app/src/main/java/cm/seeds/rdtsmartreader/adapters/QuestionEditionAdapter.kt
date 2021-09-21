package cm.seeds.rdtsmartreader.adapters

import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.forEachIndexed
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.databinding.ItemQuestionBinding
import cm.seeds.rdtsmartreader.databinding.ItemQuestionOptionBinding
import cm.seeds.rdtsmartreader.helper.*
import cm.seeds.rdtsmartreader.modeles.Option
import cm.seeds.rdtsmartreader.modeles.Question
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class QuestionEditionAdapter(private val fragmentManager: FragmentManager, private val toDoOnClick: ToDoOnClick) : ListAdapter<Question,QuestionEditionAdapter.QuestionViewHolder>(object : DiffUtil.ItemCallback<Question>(){
    override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
        return oldItem.questionId == newItem.questionId
    }

    override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        return QuestionViewHolder(ItemQuestionBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bindData(getItem(position), position)
    }

    inner class QuestionViewHolder(private val databinding : ItemQuestionBinding) : RecyclerView.ViewHolder(databinding.root){
        private val DATE_FORMAT = "dd MMM yyyy"
        private val inputManager = databinding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        fun bindData(question: Question, position : Int){

            databinding.questionLabel.setText(question.questionLabel)
            databinding.edittextHintQuestion.setText(question.questionHint)
            databinding.edittextTypeQuestion.setText(question.questionType)
            databinding.checkboxRequiredQuestion.isChecked = question.required
            databinding.edittextSavedLabel.setText(question.questionLabelForSaving)
            databinding.editextDateMin.setText(formatDate(question.minDate,DATE_FORMAT))
            databinding.edittextDateMax.setText(formatDate(question.maxDate,DATE_FORMAT))

            databinding.edittextTypeQuestion.setAdapter(ArrayAdapter(databinding.root.context,R.layout.simple_list_item,R.id.text1, listOf(
                    TYPE_QUESTION_SELECT_ONE,
                TYPE_QUESTION_SELECT_MULTIPLE,
                TYPE_QUESTION_TEXT,
                TYPE_QUESTION_DATE,
                TYPE_QUESTION_CHECKOX,
                TYPE_QUESTION_RADIO,
                TYPE_QUESTION_NUMBER_ENTIER,
                TYPE_QUESTION_NUMBER_REEL,
                TYPE_QUESTION_YES_OR_NO
            )))

            databinding.layoutOptions.gone()

            databinding.layoutDateOption.gone()

            when(question.questionType){

                TYPE_QUESTION_SELECT_MULTIPLE, TYPE_QUESTION_SELECT_ONE, TYPE_QUESTION_CHECKOX, TYPE_QUESTION_RADIO, TYPE_QUESTION_YES_OR_NO->{
                    databinding.layoutOptions.show()
                }

                TYPE_QUESTION_DATE  ->{
                    databinding.layoutDateOption.show()
                }

                TYPE_QUESTION_TEXT, TYPE_QUESTION_NUMBER_ENTIER, TYPE_QUESTION_NUMBER_REEL -> {

                }

            }

            databinding.layoutOptionsValues.removeAllViews()
            if(!question.options.isNullOrEmpty()){
                question.options.forEachIndexed { index, option ->
                    addOptionsOnQuestion(option, question, index)
                }
            }

            addActionsOnViews(question, position)
        }

        private fun addOptionsOnQuestion(option: Option, question: Question, optionIndex: Int) {
            val optionsBinding = ItemQuestionOptionBinding.inflate(LayoutInflater.from(databinding.layoutOptionsValues.context), databinding.layoutOptionsValues, false)
            optionsBinding.optionLabel.setText(option.optionLabel)
            optionsBinding.checkbox.isChecked = option.selected

            optionsBinding.checkbox.setOnClickListener {
                when(databinding.edittextTypeQuestion.text.toString()){

                    TYPE_QUESTION_SELECT_MULTIPLE, TYPE_QUESTION_CHECKOX -> {
                        option.selected = optionsBinding.checkbox.isChecked
                    }

                    TYPE_QUESTION_SELECT_ONE, TYPE_QUESTION_RADIO, TYPE_QUESTION_YES_OR_NO -> {
                        databinding.layoutOptionsValues.forEachIndexed { index, view ->
                            if(index == optionIndex){
                                question.options[index].selected = optionsBinding.checkbox.isChecked
                                //view.findViewById<CheckBox>(R.id.checkbox).isChecked = optionsBinding.checkbox.isChecked
                            }else{
                                question.options[index].selected = false
                                view.findViewById<CheckBox>(R.id.checkbox).isChecked = false
                            }
                        }
                    }
                }
            }

            optionsBinding.actionButton.setOnClickListener {
                val actionListener = DialogInterface.OnClickListener { _, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            question.options.remove(option)
                            databinding.layoutOptionsValues.removeViewAt(optionIndex)
                        }
                    }
                }
                MaterialAlertDialogBuilder(optionsBinding.root.context)
                        .setMessage("Voulez vous vraiment supprimer cette option?")
                        .setBackground(ContextCompat.getDrawable(databinding.root.context,R.drawable.dialog_background))
                        .setPositiveButton("Supprimer", actionListener)
                        .setNegativeButton("Ne pas supprimer", actionListener)
                        .show()
            }

            optionsBinding.optionLabel.doAfterTextChanged {
                option.optionLabel = it.toString()
                if(it.toString().isBlank()){
                    optionsBinding.optionLabel.setError("", ContextCompat.getDrawable(databinding.root.context,R.drawable.ic_round_warning_24))
                }else{
                    optionsBinding.optionLabel.error = null
                }
            }

            databinding.layoutOptionsValues.addView(optionsBinding.root)
        }

        private fun addActionsOnViews(question: Question, position: Int) {

            val datePicker = MaterialDatePicker.Builder.dateRangePicker().apply {
                setTitleText("Choisissez l'intervalle de la tade")
                if(question.minDate >0 && question.maxDate >0){
                    setSelection(Pair(question.minDate, question.maxDate))
                }
            }.build()
            datePicker.apply {
                addOnPositiveButtonClickListener {
                    if(it?.first != null && it.second!=null){
                        question.minDate = it.first!!
                        question.maxDate = it.second!!
                        databinding.editextDateMin.setText(formatDate(it.first!!,DATE_FORMAT))
                        databinding.edittextDateMax.setText(formatDate(it.second!!,DATE_FORMAT))
                    }
                }
            }

            databinding.buttonDateMax.setOnClickListener {
                datePicker.show(fragmentManager,"DATE")
            }

            databinding.buttonDateMin.setOnClickListener {
                datePicker.show(fragmentManager,"DATE")
            }



            databinding.questionLabel.doAfterTextChanged {
                question.questionLabel = it.toString()
                if(it.toString().isBlank()){
                    databinding.questionLabel.setError("", ContextCompat.getDrawable(databinding.root.context,R.drawable.ic_round_warning_24))
                }else{
                    databinding.questionLabel.error = null
                }
            }

            databinding.edittextTypeQuestion.setOnItemClickListener { _, view, _, _ ->
                val value = view.findViewById<TextView>(R.id.text1).text.toString()
                question.questionType = value
                when(value){
                    TYPE_QUESTION_SELECT_ONE, TYPE_QUESTION_SELECT_MULTIPLE, TYPE_QUESTION_RADIO, TYPE_QUESTION_CHECKOX  ->{
                        databinding.layoutOptions.show()
                        databinding.buttonAddOption.show()
                        databinding.layoutDateOption.gone()
                    }

                    TYPE_QUESTION_TEXT, TYPE_QUESTION_NUMBER_REEL, TYPE_QUESTION_NUMBER_ENTIER ->{
                        databinding.layoutOptions.gone()
                        databinding.layoutDateOption.gone()
                    }

                    TYPE_QUESTION_DATE ->{
                        databinding.layoutOptions.gone()
                        databinding.layoutDateOption.show()
                    }

                    TYPE_QUESTION_YES_OR_NO -> {
                        databinding.layoutOptions.show()
                        databinding.layoutDateOption.gone()
                        question.options.clear()
                        val option = Option(optionLabel = "Yes / No")
                        question.options.add(option)
                        addOptionsOnQuestion(option,question,0)
                        databinding.buttonAddOption.gone()
                    }
                }
            }

            databinding.buttonAddOption.setOnClickListener {
                val numberOfOptions = question.options.size + 1
                val newOption = Option(optionLabel = "Option $numberOfOptions")
                question.options.add(newOption)
                addOptionsOnQuestion(newOption,question,numberOfOptions - 1)
            }

            databinding.checkboxRequiredQuestion.setOnClickListener {
                question.required = databinding.checkboxRequiredQuestion.isChecked
            }

            databinding.edittextSavedLabel.doAfterTextChanged {
                question.questionLabelForSaving = it.toString()
                if(it.toString().isBlank()){
                    databinding.edittextSavedLabel.setError("", ContextCompat.getDrawable(databinding.root.context,R.drawable.ic_round_warning_24))
                }else{
                    databinding.edittextSavedLabel.error = null
                }
            }

            databinding.edittextHintQuestion.doAfterTextChanged {
                question.questionHint = it.toString()
            }

            databinding.buttonDeleteQuestion.setOnClickListener {
                toDoOnClick.onItemClick(question,position,it)
            }
        }


/*        private fun handleTextChange(textView: TextView, setPreviousTextIfNotCorrect : Boolean = false, previousText : String) : Boolean{
            val newLabel = textView.text
            return if (newLabel.isBlank()) {
                showToast(textView.context, "Une question ne peut avoir un intitulé vide")
                if(setPreviousTextIfNotCorrect){
                    textView.text = previousText
                    textView.isCursorVisible = false
                    inputManager.hideSoftInputFromWindow(
                        textView.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                    return true
                }
                false
            } else {
                textView.isCursorVisible = false
                inputManager.hideSoftInputFromWindow(
                    textView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
                true
                //databinding.edittextQuestionLabel.isEnabled = false
            }
        }*/
    }
}