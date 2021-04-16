package adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import models.BookshelveVolumeModels
import com.example.pocket_library_list.R
import com.squareup.picasso.Picasso


class ReadListView(val volumes: BookshelveVolumeModels) : RecyclerView.Adapter<CustomViewHolder>() {




    // Changing the URL from api from HTTP to HTTPS, so that Picaso can use it
    private fun editImageLink(image: String): String {
        var newImageLink = image
        newImageLink.removeRange(0, 3)
        newImageLink = newImageLink.substring(0, 4) + "s" + newImageLink.substring(4, newImageLink.length)
        return newImageLink
    }

    //Checks if the retrieved ISBN is ISBN_10 or ISBN_13
    private fun checkISBN(isbn: String): String {
        var newISBN: String = isbn
        if (isbn.length != 13) {
            return "97986$newISBN"
        } else {
            return newISBN
        }
    }


    override fun getItemCount(): Int {
        return volumes.totalItems

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.custom_list_view_row, parent, false)
        return CustomViewHolder(cellForRow)

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val title = volumes.items!!.get(position)!!.volumeInfo!!.title
        val author = volumes.items!!.get(position)!!.volumeInfo!!.authors!!.get(0)
        val releaseYear = volumes.items!!.get(position)!!.volumeInfo!!.publishedDate
        val isbn = volumes.items!!.get(position)!!.volumeInfo!!.industryIdentifiers!!.get(0).identifier
        var publisher = volumes.items!!.get(position)!!.volumeInfo!!.publisher
        val bookCover = volumes.items!!.get(position)!!.volumeInfo!!.imageLinks!!.thumbnail!!.toString()
        val newBookCover = editImageLink(bookCover)
        println(isbn + "aa")

        if (publisher == "") {
            publisher = "NO DATA AVALIABLE"
        }



        holder.view.findViewById<TextView>(R.id.bookTitle).text = title
        holder.view.findViewById<TextView>(R.id.author).text = author
        holder.view.findViewById<TextView>(R.id.release_year).text = releaseYear
        holder.view.findViewById<TextView>(R.id.isbn).text = isbn
        holder.view.findViewById<TextView>(R.id.publisher).text = publisher
        val cover: ImageView = holder.view.findViewById(R.id.cover)
        Picasso.get().load(newBookCover).into(cover);


    }
}



    class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }




