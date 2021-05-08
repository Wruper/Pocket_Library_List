package adding_books

import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

import com.example.pocket_library_list.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.zxing.integration.android.IntentIntegrator
import com.squareup.picasso.Picasso
import interfaces.Interface
import models.BookshelvesVolumeModels
import models.SearchedBooksModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.security.auth.callback.Callback

class Camera : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_add)
        val scanButton: Button = findViewById(R.id.btnScan)
        val submitButton: Button = findViewById(R.id.sumbitButton)
        retrieveBasicInfo()
        addValueToSpinner()

        submitButtonStatus(submitButton)

        scanButton.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setOrientationLocked(false)
            scanner.setBarcodeImageEnabled(true)
            scanner.initiateScan()
        }

        submitButton.setOnClickListener {
            retrieveAuthToken()
        }
    }

    /*
    This function disables or enables the "Submit" button depending on value in isbnText
    since the value for that TextView changes when End-user scans an ISBN bar code.
    */
    private fun submitButtonStatus(btn_submit: Button) {
        val isbnText = findViewById<TextView>(R.id.isbnValue)
        btn_submit.isEnabled = isbnText.text != this.getString(R.string.scan_msg)
    }

    private fun addValueToSpinner() {
        val spinner = findViewById<Spinner>(R.id.spinner)
        val arrayList = arrayListOf("Read", "Currently reading", "To read")
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
    }

    /*
    Retrieves the End-Users basic information as the users Google accounts full name and
    the users avatars image link, that  is used in Picasso to display it.
    */
    @SuppressLint("SetTextI18n")
    private fun retrieveBasicInfo() {
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personPhoto = acct.photoUrl
            val avatar: ImageView = findViewById(R.id.profilePic)
            val text: TextView = findViewById(R.id.name)
            text.text = " $personName"
            Picasso.get().load(personPhoto).into(avatar)
        }
    }

    /*
    This function retrieves a Toast message if the scanned ISBN bar code was a success or not.
    If the scan was a success, then the submit button is enabled.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val txtResult: TextView = findViewById(R.id.isbnValue)
        val submitButton: Button = findViewById(R.id.sumbitButton)
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                    txtResult.text = result.contents
                    submitButtonStatus(submitButton)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun getSpinnerValue(): String {
        val spinner = findViewById<View>(R.id.spinner) as Spinner

        return when (spinner.selectedItem.toString()) {
            "Read" -> "4"
            "Currently reading" -> "3"
            "To read" -> "2"
            else -> "Error" // This will never happen, since the spinner will always have 3 values.
        }
    }

    private fun getISBNTextValue(): String {
        val isbn = findViewById<TextView>(R.id.isbnValue)
        return isbn.text.toString()
    }

    class OnError : Callback {
        fun handleMessage(): Boolean {
            Log.e("onError", "ERROR")
            return false
        }
    }

    private fun retrieveAuthToken() {
        val am = AccountManager.get(this)
        val accounts = am.getAccountsByType("com.google")
        val options = Bundle()
        var myAccount: Account? = null

        for (i in accounts.indices) {
            if (accounts[i].type == "com.google") myAccount = accounts[i]
        }

        am.getAuthToken(
            myAccount,                            // Account retrieved using getAccountsByType()
            "Manage your tasks",    // Auth scope
            options,                              // Authenticator-specific options
            this,                         // Your activity
            { result ->
                result.result.getString(AccountManager.KEY_AUTHTOKEN)?.let {
                    getData(
                        "https://www.googleapis.com/books/v1/",
                        result.result.getString(AccountManager.KEY_AUTHTOKEN)!!
                    )
                }
            },                        // Callback called when a token is successfully acquired
            Handler().apply { Manual.OnError() }       // Callback called if an error occurs
        )
    }

    /*
    Finds the book by ISBN number, using the scanned ISBN value and the selected
    spinner, when "Submit" was pressed.
    */
    private fun getData(baseUrl: String, token: String) {

        val logging = HttpLoggingInterceptor()
        logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
        val httpClient = OkHttpClient.Builder().addInterceptor(logging)

        httpClient.addInterceptor { chain ->
            val request: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            println(token)
            chain.proceed(request)
        }

        val retrofit: Retrofit =
            Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .client(httpClient.build())
                .build()

        val service = retrofit.create(Interface::class.java)

        val call = service.getBooksInfo(getISBNTextValue())

        call.enqueue(object : retrofit2.Callback<SearchedBooksModel> {
            override fun onResponse(
                call: Call<SearchedBooksModel>,
                response: Response<SearchedBooksModel>
            ) {
                if (response.code() == 200) {
                    val volumes: SearchedBooksModel = response.body()
                    println(volumes.items!![0].id)
                    postData(baseUrl, token, volumes.items[0].id)

                }
            }

            override fun onFailure(call: Call<SearchedBooksModel>?, t: Throwable?) {
                Toast.makeText(
                    applicationContext, "The ISBN numbers is either invalid" +
                            "or cannot be found on the database.", Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /*
    Posts the retrieved book in the selected bookshelf.
    */
    private fun postData(baseUrl: String, token: String, bookID: String) {
        val logging = HttpLoggingInterceptor()
        logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
        val httpClient = OkHttpClient.Builder().addInterceptor(logging)

        httpClient.addInterceptor { chain ->
            val request: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            println(token)
            chain.proceed(request)
        }

        val retrofit: Retrofit =
            Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .client(httpClient.build())
                .build()

        val service = retrofit.create(Interface::class.java)

        val call = service.postNewBook(getSpinnerValue(), bookID)
        call.enqueue(object : retrofit2.Callback<BookshelvesVolumeModels> {
            override fun onResponse(
                call: Call<BookshelvesVolumeModels>,
                response: Response<BookshelvesVolumeModels>
            ) {
                Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<BookshelvesVolumeModels>?, t: Throwable?) {
                Toast.makeText(
                    applicationContext, "Something went wrong," +
                            "please check your internet connection", Toast.LENGTH_LONG
                ).show()
            }

        })
    }
}