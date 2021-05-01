package adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import book_genre.SelectedGenreBooks
import com.example.pocket_library_list.R
import models.BookshelveVolumeModels
import retrofit2.Callback

class CategoryAdapter(
        private val categoryList: ArrayList<String> = ArrayList<String>(),
        private val shelfType: String):
RecyclerView.Adapter<CustomViewHolders>(){





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolders {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.custom_categories_row, parent, false)
        return CustomViewHolders(cellForRow)

    }


    override fun onBindViewHolder(holder: CustomViewHolders, position: Int) {

         val category = categoryList[position]

        holder.view.findViewById<Button>(R.id.categoryButton).text = category
        holder.view.findViewById<Button>(R.id.categoryButton).setOnClickListener {
            val intent = Intent(it.context, SelectedGenreBooks::class.java)
            intent.putExtra("bookCategory",category)
            intent.putExtra("shelfType",shelfType)
            it.context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

}

class CustomViewHolders(val view: View) : RecyclerView.ViewHolder(view)