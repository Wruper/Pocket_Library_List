package adding_books

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

class AddBookMenu: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_book)

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

     private fun onClickListener() {
        val manual: CardView = findViewById(R.id.manual)
        val camera: CardView = findViewById(R.id.camera)

        manual.setOnClickListener {
            val intent = Intent(this, Manual::class.java)
            startActivity(intent)
        }

         camera.setOnClickListener{
             val intent = Intent(this, Camera::class.java)
             startActivity(intent)
         }
    }
}