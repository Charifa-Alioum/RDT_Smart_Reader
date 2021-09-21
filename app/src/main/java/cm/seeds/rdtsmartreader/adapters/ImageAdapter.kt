package cm.seeds.rdtsmartreader.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cm.seeds.rdtsmartreader.databinding.ItemPersonCardBinding
import cm.seeds.rdtsmartreader.helper.DATE_FORMAT
import cm.seeds.rdtsmartreader.helper.ToDoOnClick
import cm.seeds.rdtsmartreader.helper.loadImageInView
import cm.seeds.rdtsmartreader.helper.formatDate
import cm.seeds.rdtsmartreader.modeles.Image
import java.io.File

class ImageAdapter(private val toDoOnClick: ToDoOnClick) : ListAdapter<Image, ImageAdapter.ImageViewHolder>(object  : DiffUtil.ItemCallback<Image>(){
    override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.imageKey == newItem.imageKey
    }

    override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem == newItem
    }
}){
    val DATE_FORMAT = "dd MMM yyyy, hh:mm"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ItemPersonCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bindData(getItem(position),position)
    }

    inner class ImageViewHolder(private val databinding : ItemPersonCardBinding) : RecyclerView.ViewHolder(databinding.root){

        fun bindData(image : Image, position : Int){
            databinding.root.setOnClickListener {
                toDoOnClick.onItemClick(image,position,it)
            }
            loadImageInView(databinding.imageTest,image.filePath)
            databinding.textviewNomPatient.text = image.name
            databinding.textviewTestResultat.text = image.result
            databinding.textviewAgePatient.text = formatDate(image.imageKey, DATE_FORMAT)
        }

    }
}