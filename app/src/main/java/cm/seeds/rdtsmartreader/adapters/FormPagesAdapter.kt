package cm.seeds.rdtsmartreader.adapters

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import cm.seeds.rdtsmartreader.modeles.Page
import cm.seeds.rdtsmartreader.ui.form.form_edition.FormCreationActivity
import cm.seeds.rdtsmartreader.ui.form.form_edition.page.FormPageEditionFragment
import cm.seeds.rdtsmartreader.ui.form.form_preview.fragment.PagePreviewFragment

class FormPagesAdapter(parent : FragmentActivity, private var pages : MutableList<Page> = mutableListOf(), private val isEdition : Boolean) : FragmentStateAdapter(parent){

    override fun getItemCount(): Int {
        return pages.size
    }

    fun submitList(pages: MutableList<Page>){
        this.pages = pages
        notifyDataSetChanged()
    }

    override fun createFragment(position: Int): Fragment {
        return if(isEdition){
            FormPageEditionFragment.getInstance(page = pages[position])
        }else{
            PagePreviewFragment.getNewInstance(page = pages[position],position = position)
        }
    }
}