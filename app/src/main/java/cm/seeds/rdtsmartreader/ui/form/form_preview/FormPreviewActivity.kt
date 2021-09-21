package cm.seeds.rdtsmartreader.ui.form.form_preview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.adapters.FormPagesAdapter
import cm.seeds.rdtsmartreader.databinding.ActivityFormPreviewBinding
import cm.seeds.rdtsmartreader.modeles.Form

class FormPreviewActivity : AppCompatActivity() {

    companion object{
        const val FORM_TO_SHOW = "FORM_TO_SHOW"
    }

    private lateinit var formPageAdapter : FormPagesAdapter
    private lateinit var formPreviewViewModel : FormPreviewViewModel
    private lateinit var dataBinding : ActivityFormPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = ActivityFormPreviewBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        formPreviewViewModel = ViewModelProvider(this).get(FormPreviewViewModel::class.java)

        val form = intent.getSerializableExtra(FORM_TO_SHOW)
        if(form!=null){
            formPreviewViewModel.setForm(form as Form)

            attachObservers()

            addActionOnViews()

        }else{
            finish()
        }
    }

    private fun addActionOnViews() {
        dataBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        dataBinding.viewpagerFormPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                dataBinding.subtitle.text = "Page $position"
                if(position == 0){
                    dataBinding.buttonPrevious.isEnabled = false
                    //dataBinding.buttonNext.isEnabled = true
                }

                if(position == formPageAdapter.itemCount - 1){
                    //dataBinding.buttonPrevious.isEnabled = false
                    dataBinding.buttonNext.isEnabled = true
                    dataBinding.buttonNext.text = "Terminer"
                }
            }
        })

        dataBinding.buttonNext.setOnClickListener {
            when{
                dataBinding.viewpagerFormPage.currentItem < formPageAdapter.itemCount -1 -> dataBinding.viewpagerFormPage.setCurrentItem(dataBinding.viewpagerFormPage.currentItem + 1, true)

                dataBinding.viewpagerFormPage.currentItem == formPageAdapter.itemCount -1 -> finish()
            }
        }

        dataBinding.buttonPrevious.setOnClickListener {
            when{
                dataBinding.viewpagerFormPage.currentItem > 0 -> dataBinding.viewpagerFormPage.setCurrentItem(dataBinding.viewpagerFormPage.currentItem - 1, true)

                dataBinding.viewpagerFormPage.currentItem == 0 -> finish()
            }
        }
    }

    private fun attachObservers() {
        formPreviewViewModel.form.observe(this,{
            if(it!=null){

                dataBinding.title.text = it.formName

                formPageAdapter = FormPagesAdapter(this@FormPreviewActivity,it.pages,isEdition = false)
                dataBinding.viewpagerFormPage.adapter = formPageAdapter
            }
        })
    }

}