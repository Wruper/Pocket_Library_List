package adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocket_library_list.R
import com.squareup.picasso.Picasso
import models.Item

class BooksByGenre(private val bookList: ArrayList<Item> = ArrayList()):
    RecyclerView.Adapter<CustomViewHolders>() {

    /*
    This adapter inputs book related values in custom_book_view_row.xml, which later is used in
    all_book_screen.xml RecyclerViewer.
     */

    /* Changes the retrieved books thumbnail link from HTTP to HTTPS so that it could be
    used with Picasso */
    private fun editImageLink(image: String): String {
        var newImageLink = image
        newImageLink.removeRange(0, 3)
        newImageLink = newImageLink.substring(0, 4) + "s" + newImageLink.substring(4, newImageLink.length)
        return newImageLink
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolders {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.custom_book_view_row, parent, false)
        return CustomViewHolders (cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolders, position: Int) {

        val title = bookList[position].volumeInfo!!.title
        val author = bookList[position].volumeInfo!!.authors!![0]
        val releaseYear = bookList[position].volumeInfo!!.publishedDate
        val isbn = bookList[position].volumeInfo!!.industryIdentifiers!![0].identifier
        val publisher = bookList[position].volumeInfo!!.publisher
        val bookCover = bookList[position].volumeInfo!!.imageLinks!!.thumbnail
        val newBookCover = editImageLink(bookCover)

        holder.view.findViewById<TextView>(R.id.bookTitle).text = title
        holder.view.findViewById<TextView>(R.id.author).text = author
        holder.view.findViewById<TextView>(R.id.release_year).text = releaseYear
        holder.view.findViewById<TextView>(R.id.isbn).text = isbn
        holder.view.findViewById<TextView>(R.id.publisher).text = publisher
        val cover: ImageView = holder.view.findViewById(R.id.cover)
        Picasso.get().load(newBookCover).into(cover)

    }

    override fun getItemCount(): Int {
        return bookList.size
    }
}