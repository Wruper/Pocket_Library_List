package adding_books

import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.pocket_library_list.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
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


class Manual : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manual_add)

        val submitButton = findViewById<Button>(R.id.submitISBN)
        val editText = findViewById<EditText>(R.id.isbnInputField)

        /*Each time a number has been inputted in editText, the editText field is checked
        and the function "submitButtonStatus" is called.*/
        val tw: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                submitButtonStatus(submitButton)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                submitButtonStatus(submitButton)
            }

            override fun afterTextChanged(s: Editable) {
                submitButtonStatus(submitButton)
            }
        }

        editText.addTextChangedListener(tw)
        submitButtonStatus(submitButton)

        submitButton.setOnClickListener {
            searchForBookId()
        }

        retrieveBasicInfo()
        addValueToSpinner()
    }

    /*
    This function disables or enables the "Submit" button depending on value in editText.
    This function main idea is to not allow the End-user to submit an empty or half finished isbn
    number. The End-user is only able to input ISBN-13.
     */
    private fun submitButtonStatus(btn_submit: Button) {
        val isbn = findViewById<EditText>(R.id.isbnInputField)
        println(isbn.text.count())
        btn_submit.isEnabled = (isbn.text.count() > 12)
    }

    /*
    This function retrieves the selected bookshelf's number, that is later used in the API call.
     */
    private fun getSpinnerValue(): String {
        val spinner = findViewById<View>(R.id.spinner) as Spinner

        return when (spinner.selectedItem.toString()) {
            "Read" -> "4"
            "Currently reading" -> "3"
            "To Read" -> "3"
            else -> "Error" // This is never going to happen, since the spinner has only 3 values.
        }
    }

    private fun getISBNTextValue(): String {
        val isbn = findViewById<EditText>(R.id.isbnInputField)
        return isbn.text.toString()
    }


    /*Retrieves the End-Users basic information as the users Google accounts full name and
     the users avatars image link, that  is used in Picasso to display it.*/
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

    private fun addValueToSpinner() {
        val spinner = findViewById<Spinner>(R.id.spinner)
        val arrayList = arrayListOf("Read", "Currently reading", "To Read")
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
    }

    class OnError : Callback {
        fun handleMessage(): Boolean {
            Log.e("onError", "ERROR")
            return false
        }
    }

    /*
    This function retrieves End-users Google OAuth token, that is used for API requests
    just like retrieving books information and posting the retrieved data in the selected bookshelf.
     */
    private fun searchForBookId() {
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
            Handler().apply { OnError() }       // Callback called if an error occurs
        )
    }

    /*
    Finds the book by ISBN number, using the End-users inputted ISBN value and the selected
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
                    postData(baseUrl, token, volumes.items!![0].id)
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
                Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
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



