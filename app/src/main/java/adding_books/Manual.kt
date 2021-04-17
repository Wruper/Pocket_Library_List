package adding_books

import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.pocket_library_list.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.squareup.picasso.Picasso
import interfaces.Interface
import models.BookshelveVolumeModels
import models.SearchedBooksModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.security.auth.callback.Callback


class Manual: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manual_add)

        val submitButton = findViewById<Button>(R.id.submitISBN)

        submitButton.setOnClickListener {
            retrieveAuthToken()

        }

        retrieveBasicInfo()
        addValueToSpinner()


    }

    private fun getSpinnerValue(): String {
        val spinner = findViewById<View>(R.id.spinner) as Spinner

        return when (spinner.selectedItem.toString()) {
            "Read" -> "4"
            "Currently reading" -> "3"
            "Reading" -> "3"
            else -> "Error" // Edit this with toast
        }
    }


    private fun getISBNTextValue():String{
        val isbn = findViewById<EditText>(R.id.isbnInputField)
        return isbn.text.toString()
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

    private fun addValueToSpinner() {
            val spinner = findViewById<Spinner>(R.id.spinner)
            val arrayList = arrayListOf("Read", "Currently reading", "Reading")
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val spinnerValue = parent.getItemAtPosition(position).toString()
                    Toast.makeText(parent.context, "Selected: $spinnerValue", Toast.LENGTH_SHORT).show()
                }

                override  fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

    class OnError : Callback {
        fun handleMessage(msg: Message?): Boolean {
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
                Handler().apply { OnError() }       // Callback called if an error occurs
        )

    }

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
                    println("bebeebebbe")
                    val volumes: SearchedBooksModel = response.body()
                    postData(baseUrl,token, volumes.items!![0].id)

                }
            }

            override fun onFailure(call: Call<SearchedBooksModel>?, t: Throwable?) {
                Toast.makeText(applicationContext,"The ISBN numbers is either invalid" +
                        "or cannot be found on the database.",Toast.LENGTH_LONG).show()
            }

        })
    }


   private fun postData(baseUrl: String, token: String, bookID: String ){
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

        val call = service.postNewBook(getSpinnerValue(),bookID)
       call.enqueue(object : retrofit2.Callback<BookshelveVolumeModels> {
           override fun onResponse(
                   call: Call<BookshelveVolumeModels>,
                   response: Response<BookshelveVolumeModels>
           ) {
               Toast.makeText(applicationContext,"Success",Toast.LENGTH_LONG).show()
           }

           override fun onFailure(call: Call<BookshelveVolumeModels>?, t: Throwable?) {
               Toast.makeText(applicationContext,"Something went wrong," +
                       "please check your internet connection",Toast.LENGTH_LONG).show()
           }

       })
   }
}



