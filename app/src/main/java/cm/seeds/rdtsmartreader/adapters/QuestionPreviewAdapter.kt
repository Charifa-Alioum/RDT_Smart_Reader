package cm.seeds.rdtsmartreader.adapters

import android.content.Context
import android.os.Parcel
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.databinding.*
import cm.seeds.rdtsmartreader.helper.*
import cm.seeds.rdtsmartreader.modeles.Question
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import java.lang.StringBuilder


class QuestionPreviewAdapter(private val fragmentManager: FragmentManager) : ListAdapter<Question, QuestionPreviewAdapter.QuestionPreviewViewHolder>(object : DiffUtil.ItemCallback<Question>(){
    override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
        return oldItem.questionId == newItem.questionId
    }

    override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
        return oldItem == newItem
    }

}){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionPreviewViewHolder {
        return QuestionPreviewViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),viewType,parent,false))
    }

    override fun onBindViewHolder(holder: QuestionPreviewViewHolder, position: Int) {
        holder.bindData(getItem(position),position)
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position).questionType){

            TYPE_QUESTION_SELECT_ONE, TYPE_QUESTION_SELECT_MULTIPLE -> R.layout.item_question_select

            TYPE_QUESTION_CHECKOX, TYPE_QUESTION_YES_OR_NO -> R.layout.item_question_checkbox

            TYPE_QUESTION_DATE -> R.layout.item_question_date

            TYPE_QUESTION_TEXT, TYPE_QUESTION_NUMBER_ENTIER, TYPE_QUESTION_NUMBER_REEL -> R.layout.item_question_text

            TYPE_QUESTION_RADIO -> R.layout.item_question_radio

            else ->  R.layout.item_question_text

        }
    }



    inner class QuestionPreviewViewHolder(private val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root){

        fun bindData(question : Question, position : Int){

            when(dataBinding){

                is ItemQuestionTextBinding -> {
                    dataBinding.layoutEdittext.helperText = question.questionHint
                    dataBinding.layoutEdittext.hint = question.questionLabel
                    when(question.questionType){
                        TYPE_QUESTION_NUMBER_REEL -> dataBinding.edittext.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                        TYPE_QUESTION_NUMBER_ENTIER -> dataBinding.edittext.inputType = InputType.TYPE_CLASS_NUMBER
                        TYPE_QUESTION_TEXT -> dataBinding.edittext.inputType = InputType.TYPE_CLASS_TEXT
                    }
                }

                is ItemQuestionDateBinding -> {
                    dataBinding.edittextLayout.hint = question.questionLabel
                    dataBinding.edittextLayout.setEndIconOnClickListener {
                        val datePickerDialog = MaterialDatePicker.Builder.datePicker().apply {
                            setTitleText(question.questionLabel)
                            setCalendarConstraints(CalendarConstraints.Builder()
                                .setOpenAt((question.maxDate + question.minDate) / 2)
                                .setEnd(question.maxDate)
                                .setStart(question.minDate)
                                .setValidator(object : CalendarConstraints.DateValidator{
                                override fun describeContents(): Int {
                                    return 0
                                }

                                override fun writeToParcel(p0: Parcel, flags: Int) {

                                }

                                override fun isValid(date: Long): Boolean {
                                    return date in question.minDate..question.maxDate
                                }
                            }).build())
                        }.build()
                        datePickerDialog.addOnPositiveButtonClickListener {
                            dataBinding.edittextDate.setText(formatDate(it,"dd MMM yyyy"))
                        }
                        datePickerDialog.show(fragmentManager,"DATE")
                    }
                }

                is ItemQuestionCheckboxBinding -> {
                    dataBinding.questionLabel.text = question.questionLabel
                    dataBinding.layoutCheckbox.removeAllViews()
                    question.options.forEach {

                        val layoutOption = MaterialCheckBox(dataBinding.root.context)
                        val layoutParams = layoutOption.layoutParams
                        if(layoutParams is ViewGroup.MarginLayoutParams){
                            layoutParams.setMargins(16,8,16,8)
                            layoutOption.requestLayout()
                        }
                        layoutOption.text = it.optionLabel
                        layoutOption.isChecked = it.selected

                        dataBinding.layoutCheckbox.addView(layoutOption)
                    }
                }

                is ItemQuestionRadioBinding -> {
                    dataBinding.questionLabel.text = question.questionLabel
                    dataBinding.radiogroup.removeAllViews()
                    question.options.forEach {
                        val layoutOption = RadioButton(dataBinding.root.context)
                        val layoutParams = layoutOption.layoutParams
                        if(layoutParams is ViewGroup.MarginLayoutParams){
                            layoutParams.setMargins(16,8,16,8)
                            layoutOption.requestLayout()
                        }
                        layoutOption.text = it.optionLabel
                        layoutOption.isChecked = it.selected
                        dataBinding.radiogroup.addView(layoutOption)
                    }
                }

                is ItemQuestionSelectBinding -> {
                    dataBinding.questionLabel.text = question.questionLabel
                    when(question.questionType){

                        TYPE_QUESTION_SELECT_MULTIPLE -> {

                            val allItems = mutableListOf<String>()
                            val selectedItems = mutableListOf<String>()
                            question.options.forEach {
                                allItems.add(it.optionLabel)
                                if(it.selected){
                                    selectedItems.add(it.optionLabel)
                                }
                            }

                            val adapter = AdapterMultipleChoice(dataBinding.root.context,selectedItems = selectedItems, allItems = allItems)
                            dataBinding.edittext.setAdapter(adapter)
                            dataBinding.edittext.setOnItemClickListener { parent, view, indexOfSelected, id ->
                                val item = adapter.getItem(indexOfSelected)
                                if(selectedItems.contains(item)){
                                    selectedItems.remove(item)
                                }else{
                                    selectedItems.add(item!!)
                                }
                                adapter.selectedItems = selectedItems
                                adapter.notifyDataSetChanged()

                                val builder = StringBuilder()
                                selectedItems.forEachIndexed { index, content ->
                                    if(index == 0){
                                        builder.append(content)
                                    }else{
                                        builder.append(", ").append(content)
                                    }
                                }
                                dataBinding.edittext.setText(builder.toString())
                            }

                        }

                        TYPE_QUESTION_SELECT_ONE -> {
                            val allItems = mutableListOf<String>()
                            var selectedItem = ""
                            question.options.forEach {
                                allItems.add(it.optionLabel)
                                if(it.selected){
                                    selectedItem = it.optionLabel
                                }
                            }

                            val adapter = AdapterSingleChoice(dataBinding.root.context,selectedItem = selectedItem, allItems = allItems)
                            dataBinding.edittext.setAdapter(adapter)
                            dataBinding.edittext.setOnItemClickListener { parent, view, indexOfSelected, id ->
                                adapter.selectedItem = adapter.getItem(indexOfSelected)!!
                                adapter.notifyDataSetChanged()
                            }
                        }

                    }

                }
            }

        }

    }

    inner class AdapterMultipleChoice(context : Context, var selectedItems : List<String>, val allItems : List<String>) : ArrayAdapter<String>(context,android.R.layout.simple_list_item_multiple_choice,android.R.id.text1,allItems){

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return updateView(super.getDropDownView(position, convertView, parent), position)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return updateView(super.getView(position, convertView, parent),position)
        }

        private fun updateView(view : View, position: Int) : View{
            val checkedTextView = view.findViewById<CheckedTextView>(android.R.id.text1)
            val item = getItem(position)
            if(item!=null){
                checkedTextView.isChecked = selectedItems.contains(item)
            }

            return view
        }
    }

    inner class AdapterSingleChoice(context : Context, var selectedItem : String, val allItems : List<String>) : ArrayAdapter<String>(context,android.R.layout.simple_list_item_checked,android.R.id.text1,allItems){

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return updateView(super.getDropDownView(position, convertView, parent), position)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return updateView(super.getView(position, convertView, parent),position)
        }

        private fun updateView(view : View, position: Int) : View{
            val checkedTextView = view.findViewById<CheckedTextView>(android.R.id.text1)
            val item = getItem(position)
            if(item!=null){
                checkedTextView.isChecked = selectedItem == item
            }

            return view
        }
    }
}