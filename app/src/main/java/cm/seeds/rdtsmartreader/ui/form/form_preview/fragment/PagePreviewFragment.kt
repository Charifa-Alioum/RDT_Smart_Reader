package cm.seeds.rdtsmartreader.ui.form.form_preview.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cm.seeds.rdtsmartreader.adapters.QuestionEditionAdapter
import cm.seeds.rdtsmartreader.adapters.QuestionPreviewAdapter
import cm.seeds.rdtsmartreader.databinding.FragmentPagePreviewBinding
import cm.seeds.rdtsmartreader.modeles.Page

class PagePreviewFragment : Fragment() {

    private lateinit var pagePreviewViewModel: PagePreviewViewModel
    private lateinit var databinding : FragmentPagePreviewBinding
    private lateinit var adapterQuestionEdition : QuestionPreviewAdapter

    companion object{
        const val PAGE_TO_SHOW = "PAGE_TO_SHOW"
        const val POSITION_OF_PAGE = "POSITION_OF_PAGE"

        /**
         * Retourne une instance de ce frgament en précisant [page] comme la page à afficher
         */
        fun getNewInstance(page: Page, position : Int) : PagePreviewFragment {
            return PagePreviewFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PAGE_TO_SHOW,page)
                    putInt(POSITION_OF_PAGE, position)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pagePreviewViewModel = ViewModelProvider(this).get(PagePreviewViewModel::class.java)

        arguments?.let {
            pagePreviewViewModel.setPage(it.getSerializable(PAGE_TO_SHOW) as Page, it.getInt(
                POSITION_OF_PAGE))
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        databinding = FragmentPagePreviewBinding.inflate(inflater,container,false)
        return databinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupList()

        attachObservers()

    }

    private fun setupList() {
        adapterQuestionEdition = QuestionPreviewAdapter(childFragmentManager)
        databinding.listQuestion.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterQuestionEdition
        }
    }

    private fun attachObservers() {
        pagePreviewViewModel.page.observe(viewLifecycleOwner,{
            if(it!=null){
                databinding.pageTitle.text = it.title
                databinding.pageDetails.text = it.description

                //databinding.backgroundImage.setBackgroundColor(Color.parseColor(it.principalColor))

                adapterQuestionEdition.submitList(it.questions)

            }
        })

        pagePreviewViewModel.position.observe(viewLifecycleOwner,{

        })

    }

}