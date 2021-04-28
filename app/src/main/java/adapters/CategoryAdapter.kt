package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.pocket_library_list.R
import models.BookshelveVolumeModels

class CategoryAdapter(private val volumes: BookshelveVolumeModels):
RecyclerView.Adapter<CustomViewHolders>(){


    override fun getItemCount(): Int {
        return volumes.totalItems
    }

    fun checkIfNoCategory(category: String?): String{
        return if(category == null){
           "No category"
        } else{
            category
        }
    }

    fun checkIfCategoryRepeats(category: String, arrayList: ArrayList<String>):String{
       arrayList.add(0,category)
        for(i in 1..arrayList.size){ //sak no 1, jo 0 elements viemer ir tas kuru ievietoja
                if(arrayList[i] !=  category){
                    continue
                }
            else{
                arrayList.drop(0) // izmet elementu, kuru mes tiko ielikam
                    break
                }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolders {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.custom_categories_row, parent, false)
        return CustomViewHolders(cellForRow)

    }


    override fun onBindViewHolder(holder: CustomViewHolders, position: Int) {
        val categoryList = ArrayList<String>()


         var category = volumes.items!![position].volumeInfo?.categories?.get(0)

        category = checkIfNoCategory(category)


        holder.view.findViewById<Button>(R.id.categoryButton).text = category.toString()




    }



}

class CustomViewHolders(val view: View) : RecyclerView.ViewHolder(view) {

}