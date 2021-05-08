package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import models.BookshelvesVolumeModels
import com.example.pocket_library_list.R
import com.squareup.picasso.Picasso


class BookListView(private val volumes: BookshelvesVolumeModels) :
    RecyclerView.Adapter<CustomViewHolder>() {

    /*
    This adapter inputs book related values in custom_book_view_row.xml, which later is used in
    all_book_screen.xml RecyclerViewer.
     */

    /* Changes the retrieved books thumbnail link from HTTP to HTTPS so that it could be
    used with Picasso */
    private fun editImageLink(image:String): String {
        var newImageLink = image
        newImageLink.removeRange(0, 3)
        newImageLink =
            newImageLink.substring(0, 4) + "s" + newImageLink.substring(4, newImageLink.length)
        return newImageLink
    }

    override fun getItemCount(): Int {
        return volumes.totalItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.custom_book_view_row, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        val title = volumes.items!![position].volumeInfo!!.title
        val author = volumes.items[position].volumeInfo!!.authors!![0]
        val releaseYear = volumes.items[position].volumeInfo!!.publishedDate
        val isbn =
            volumes.items[position].volumeInfo!!.industryIdentifiers!![0].identifier
        var publisher = volumes.items[position].volumeInfo!!.publisher
        val bookCover =
            volumes.items[position].volumeInfo!!.imageLinks!!.thumbnail
        val newBookCover = editImageLink(bookCover)

        if (publisher == "") {
            publisher = "NO DATA AVALIABLE"
        }

        holder.view.findViewById<TextView>(R.id.bookTitle).text = title
        holder.view.findViewById<TextView>(R.id.author).text = author
        holder.view.findViewById<TextView>(R.id.release_year).text = releaseYear
        holder.view.findViewById<TextView>(R.id.isbn).text = isbn
        holder.view.findViewById<TextView>(R.id.publisher).text = publisher
        val cover: ImageView = holder.view.findViewById(R.id.cover)
        Picasso.get().load(newBookCover).into(cover)
    }

}

class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

}




