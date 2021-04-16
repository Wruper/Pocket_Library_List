package adding_books
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.pocket_library_list.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.zxing.integration.android.IntentIntegrator
import com.squareup.picasso.Picasso

class Camera: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_add)
        val btn_scan: Button = findViewById(R.id.btnScan)
        retrieveBasicInfo()


        btn_scan.setOnClickListener{
            val scanner = IntentIntegrator(this)
            scanner.setBarcodeImageEnabled(true)
            scanner.initiateScan()

        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val txt_res: TextView = findViewById(R.id.txtValue)
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                    txt_res.text = result.contents

                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

}