package cm.seeds.rdtsmartreader.helper

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import cm.seeds.rdtsmartreader.databinding.LoadingDialogLayoutBinding

class LoadingDialog(context: Context) : Dialog(context) {

    private lateinit var databinding : LoadingDialogLayoutBinding
    private var time = 0
    private val counter = object : CountDownTimer(Long.MAX_VALUE,650){
        override fun onTick(millisUntilFinished: Long) {
            when(time%3){

                0 -> {
                    databinding.dot1.visibility = VISIBLE
                    databinding.dot2.visibility = INVISIBLE
                    databinding.dot3.visibility = INVISIBLE
                }

                1 -> {
                    databinding.dot1.visibility = INVISIBLE
                    databinding.dot2.visibility = VISIBLE
                    databinding.dot3.visibility = INVISIBLE
                }

                2 -> {
                    databinding.dot1.visibility = INVISIBLE
                    databinding.dot2.visibility = INVISIBLE
                    databinding.dot3.visibility = VISIBLE
                }

            }
            time++
        }

        override fun onFinish() {
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = LoadingDialogLayoutBinding.inflate(layoutInflater)
        setContentView(databinding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setOnShowListener {
            counter.start()
        }

        setOnDismissListener {
            counter.cancel()
        }
    }
}