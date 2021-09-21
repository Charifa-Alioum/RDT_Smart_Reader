package cm.seeds.rdtsmartreader.helper

import android.view.View

fun interface ToDoOnClick {

    fun onItemClick(item: Any, position: Int, view: View)

}