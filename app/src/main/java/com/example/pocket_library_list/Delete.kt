package com.example.pocket_library_list

import adding_books.Camera
import adding_books.Manual
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class Delete: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_book)

        onClickListener()
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
