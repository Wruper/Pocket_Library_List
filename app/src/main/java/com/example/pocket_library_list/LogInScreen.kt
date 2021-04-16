package com.example.pocket_library_list

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task


class LogInScreen: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/books"))
            .build()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in_page)

        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        val btnClick = findViewById<SignInButton>(R.id.logIn)
        val btnLogOut = findViewById<Button>(R.id.logOut)

        btnClick.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 2)

        }

        btnLogOut.setOnClickListener {
            mGoogleSignInClient.revokeAccess()
        }
    }
        private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
            try {
                val account = completedTask.getResult(ApiException::class.java)
                // Signed in successfully, show authenticated UI.
                val intent = Intent(this, MainMenu::class.java)
                startActivity(intent)
            } catch (e: ApiException) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
            }
        }


        override fun onActivityResult(requestcode: Int, resultcode: Int, data: Intent?) {
            super.onActivityResult(requestcode, resultcode, data)
            if (requestcode == 2) {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }

    }













