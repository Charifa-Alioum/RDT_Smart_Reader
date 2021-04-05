package cm.seeds.rdtsmartreader.ui.main.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.databinding.ScanresultBottomsheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ScanResultActionsButtonSheet : BottomSheetDialogFragment() {

    private lateinit var dataBinding : ScanresultBottomsheetLayoutBinding

    companion object{
        const val TAG = "TAG_BOTTOM_ACTIONS_WITH_CODE"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.scanresult_bottomsheet_layout,container,false)

        return dataBinding.root
    }

}