package cm.seeds.rdtsmartreader.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.databinding.ItemPersonBinding
import cm.seeds.rdtsmartreader.databinding.ItemPersonCardBinding
import cm.seeds.rdtsmartreader.helper.CONCLUSION_NEGATIF
import cm.seeds.rdtsmartreader.helper.CONCLUSION_POSITIF
import cm.seeds.rdtsmartreader.helper.ToDoOnClick
import cm.seeds.rdtsmartreader.helper.loadImageInView
import cm.seeds.rdtsmartreader.modeles.User

private val DIFF_USER = object : DiffUtil.ItemCallback<User>(){
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}

class UserAdapter(private val toDoOnClick: ToDoOnClick, @LayoutRes val layoutRes : Int = R.layout.item_person) : ListAdapter<User,UserAdapter.UserViewHolder>(DIFF_USER){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        return when(layoutRes){

            R.layout.item_person -> UserViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_person,parent,false))

            R.layout.item_person_card -> UserViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_person_card,parent,false))

            else -> throw Exception("")
        }
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindData(getItem(position),position)
    }

    inner class UserViewHolder(private val dataBinding : ViewBinding) : RecyclerView.ViewHolder(dataBinding.root) {

        fun bindData(user : User, position : Int){

            when(dataBinding){

                is ItemPersonBinding -> {

                    dataBinding.user = user

/*                    dataBinding.nomPersonne.text = user.userName

                    dataBinding.statutMaladie.text = user.test?.conclusion*/

                    loadImageInView(dataBinding.imageTest,user.test?.imageUri)

                    dataBinding.statutMaladie.setTextColor(when{
                        user.test?.conclusion?.equals(CONCLUSION_POSITIF,true) == true -> Color.RED
                        user.test?.conclusion?.equals(CONCLUSION_NEGATIF,true) == true -> Color.GREEN
                        else -> Color.BLACK
                    })

/*                    dataBinding.detailsPersonne.text = "${user.userAge} ans - ${user.userDomicile}"

                    dataBinding.synchedState.visibility = if(!user.synchronised) View.VISIBLE else View.GONE*/

                    dataBinding.root.setOnClickListener {
                        toDoOnClick.onItemClick(user, position, it)
                    }
                }

                is ItemPersonCardBinding -> {

                    dataBinding.user = user

                    loadImageInView(dataBinding.imageTest,user.test?.imageUri)

/*                    dataBinding.textviewNomPatient.text = user.userName
                    dataBinding.textviewAgePatient.text = "${user.userAge} ans - ${user.userDomicile}"
                    dataBinding.textviewTestResultat.text = user.test?.conclusion

                    dataBinding.textviewTestResultat.visibility = if(!user.synchronised) View.VISIBLE else View.GONE*/

                }

            }

        }

    }
}