package bookshelf


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

class BookshelfMenu: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.book_status_select)
        onClickListener()
        retrieveBasicInfo()
    }

    //Retrieves End-users full name and avatar image.
    @SuppressLint("SetTextI18n")
    private fun retrieveBasicInfo() {
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personPhoto = acct.photoUrl
            val avatar: ImageView = findViewById(R.id.profilePic)
            val text: TextView = findViewById(R.id.name)
            text.text = "$personName"
            Picasso.get().load(personPhoto).into(avatar)
        }
    }


   private fun onClickListener(){
        val toRead: CardView = findViewById(R.id.toRead)
        val read: CardView = findViewById(R.id.read)
        val currentlyReading: CardView = findViewById(R.id.currentlyReading)

        read.setOnClickListener{
            val intent = Intent(this, Read::class.java)
            startActivity(intent)
        }

       toRead.setOnClickListener{
           val intent = Intent(this, ToRead::class.java)
           startActivity(intent)
       }

       currentlyReading.setOnClickListener{
           val intent = Intent(this, CurrentlyReading::class.java)
           startActivity(intent)
       }
    }
}
