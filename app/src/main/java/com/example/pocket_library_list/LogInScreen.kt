package com.example.pocket_library_list

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope


class LogInScreen : AppCompatActivity() {

    companion object {
        //This activities request code
        const val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        // Values that the app retrieves when, End-user tries to log into his Google account.
        // *) Scope -> The app requests End-users permission to use Google Book related information.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope("https://www.googleapis.com/auth/books"))
                .build()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in_page)

        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val btnClick = findViewById<SignInButton>(R.id.logIn)
        val btnLogOut = findViewById<Button>(R.id.logOut)

        isNetworkConnected(this)

        btnClick.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, REQUEST_CODE)
        }

        btnLogOut.setOnClickListener {
            mGoogleSignInClient.revokeAccess()
        }
    }

    //Checks if the app has an internet connection.
    private fun isNetworkConnected(context: Context): Network? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetwork
    }

    //This function handles the result of the End-User logging in using his Google account.
    private fun handleSignInResult() {
        if(isNetworkConnected(this) == null){
            Toast.makeText(applicationContext, "Please check your internet connection " +
                    "before trying to log in.", Toast.LENGTH_LONG).show()
        }
        else{
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
        }

    }


    override fun onActivityResult(requestcode: Int, resultcode: Int, data: Intent?) {
        super.onActivityResult(requestcode, resultcode, data)
        if (requestcode == REQUEST_CODE) {
            handleSignInResult()
        }
    }


}













