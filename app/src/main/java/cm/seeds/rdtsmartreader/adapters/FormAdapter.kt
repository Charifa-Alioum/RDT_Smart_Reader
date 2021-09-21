package cm.seeds.rdtsmartreader.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cm.seeds.rdtsmartreader.databinding.ItemFormBinding
import cm.seeds.rdtsmartreader.helper.ToDoOnClick
import cm.seeds.rdtsmartreader.helper.gone
import cm.seeds.rdtsmartreader.helper.formatDate
import cm.seeds.rdtsmartreader.helper.show
import cm.seeds.rdtsmartreader.modeles.Form

class FormAdapter(private val toDoOnClick: ToDoOnClick) : ListAdapter<Form, FormAdapter.FormViewholder>(object : DiffUtil.ItemCallback<Form>(){
    override fun areItemsTheSame(oldItem: Form, newItem: Form): Boolean {
        return oldItem.formId == newItem.formId
    }

    override fun areContentsTheSame(oldItem: Form, newItem: Form): Boolean {
        return oldItem == newItem
    }
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewholder {
        return FormViewholder(ItemFormBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: FormViewholder, position: Int) {
        holder.bindData(getItem(position),position)
    }

    inner class FormViewholder(private val databinding : ItemFormBinding) : RecyclerView.ViewHolder(databinding.root){

        fun bindData(form: Form, positon : Int){
            databinding.formDateCreation.text = formatDate(form.dateCreationForm,"dd MMM yyyy")
            databinding.formName.text = form.formName
            databinding.formPages.text = "${form.pages.size} pages"

/*            if(positon == itemCount - 1){
                databinding.divider.gone()
            }else{
                databinding.divider.show()
            }*/

            if(form.haveErrors){
                databinding.warningIcon.show()
            }else{
                databinding.warningIcon.gone()
            }

            databinding.root.setOnClickListener {
                toDoOnClick.onItemClick(form,positon,it)
            }
        }

    }
}