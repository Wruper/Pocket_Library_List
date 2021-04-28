package book_categories

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.pocket_library_list.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.squareup.picasso.Picasso

class CategoriesMenu: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.book_status_select)
        onClickListener()
        retrieveBasicInfo()

    }

    @SuppressLint("SetTextI18n")
    private fun retrieveBasicInfo() {
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personPhoto = acct.photoUrl
            val avatar: ImageView = findViewById(R.id.profilePic)
            val text: TextView = findViewById(R.id.name)
            text.text = "Welcome back: $personName"
            Picasso.get().load(personPhoto).into(avatar);

        }
    }


    private fun onClickListener(){
        val toRead: CardView = findViewById(R.id.toRead)
        val read: CardView = findViewById(R.id.read)
        val currentlyReading: CardView = findViewById(R.id.currentlyReading)

        read.setOnClickListener{
            val intent = Intent(this, ReadCategory::class.java)
            startActivity(intent)
        }

        toRead.setOnClickListener{
            val intent = Intent(this, ToReadCategory::class.java)
            startActivity(intent)
        }

        currentlyReading.setOnClickListener{
            val intent = Intent(this, CurrentlyReadingCategory::class.java)
            startActivity(intent)
        }
    }
}
