package cm.seeds.rdtsmartreader.ui.form.all_form

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cm.seeds.rdtsmartreader.adapters.FormAdapter
import cm.seeds.rdtsmartreader.data.ViewModelFactory
import cm.seeds.rdtsmartreader.databinding.ActivityAllFormBinding
import cm.seeds.rdtsmartreader.modeles.Form
import cm.seeds.rdtsmartreader.ui.form.form_edition.FormCreationActivity

class AllFormActivity : AppCompatActivity() {

    private lateinit var allFormViewModel: AllFormViewModel
    private lateinit var databinding : ActivityAllFormBinding
    private lateinit var formAdapter : FormAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allFormViewModel = ViewModelProvider(this,ViewModelFactory(application)).get(AllFormViewModel::class.java)
        databinding = ActivityAllFormBinding.inflate(layoutInflater)
        setContentView(databinding.root)

        setSupportActionBar(databinding.toolbar)

        setupList()

        attachObservers()

        addActionsOnViews()
    }

    private fun addActionsOnViews() {
        databinding.buttonCreateForm.setOnClickListener {
            startActivity(Intent(this, FormCreationActivity::class.java))
        }
    }

    private fun attachObservers() {
        allFormViewModel.forms.observe(this,{
            formAdapter.submitList(it)
        })
    }

    private fun setupList() {
        formAdapter = FormAdapter{ any, _, _ ->
            startActivity(Intent(this@AllFormActivity, FormCreationActivity::class.java).apply {
                putExtra(FormCreationActivity.WHAT_TO_DO, FormCreationActivity.UPDATE_FORM)
                putExtra(FormCreationActivity.FORM,any as Form)
            })
        }

        databinding.recyclerviewListForm.apply {
            layoutManager = LinearLayoutManager(this@AllFormActivity)
            adapter = formAdapter
        }
    }
}