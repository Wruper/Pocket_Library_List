package com.example.pocket_library_list

import adding_books.AddBookMenu
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.squareup.picasso.Picasso


class MainMenu : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        retrieveBasicInfo()
        onClickListeners()

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

    private fun onClickListeners(){
        val categories: CardView = findViewById(R.id.categories)
        val addBook:CardView = findViewById(R.id.add)
        val delete: CardView = findViewById(R.id.delete)
        val shelf: CardView = findViewById(R.id.shelf)
        val stats: CardView = findViewById(R.id.stats)


        shelf.setOnClickListener{
            val intent = Intent(this, BookStatusSelect::class.java)
            startActivity(intent)
        }

        addBook.setOnClickListener{
            val intent = Intent(this, AddBookMenu::class.java)
            startActivity(intent)
        }

        delete.setOnClickListener{
            val intent = Intent(this, Delete::class.java)
            startActivity(intent)
        }
    }






}




