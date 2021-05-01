package book_genre

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pocket_library_list.R


class SelectedGenreBooks: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)


        val intent = intent
        val shelfType = intent.extras!!.getString("shelfType")
        val category = intent.extras!!.getString("bookCategory")
        println(shelfType)
        println(category)

    }


}