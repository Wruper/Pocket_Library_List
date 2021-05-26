package com.example.pocket_library_list

import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.squareup.picasso.Picasso
import interfaces.Interface
import models.BookshelvesVolumeModels
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.security.auth.callback.Callback

class Stats : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stats)

        retrieveReadBooksData()
        retrieveToReadBooksData()
        retrieveCurrentlyReadingBooksData()
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
            text.append(" $personName")
            Picasso.get().load(personPhoto).into(avatar)
        }
    }

    private fun calculateReadingTime(pages: Int): String {
        return "%.2f".format(((pages * 1.5) / 60) / 24)
    }

    class OnError : Callback {
        fun handleMessage(): Boolean {
            Log.e("onError", "ERROR")
            return false
        }
    }

    private fun retrieveReadBooksData() {
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
                        getReadData(
                                "https://www.googleapis.com/books/v1/",
                                result.result.getString(AccountManager.KEY_AUTHTOKEN)!!
                        )
                    }
                },                        // Callback called when a token is successfully acquired
                Handler().apply { OnError() }       // Callback called if an error occurs
        )

    }

    private fun getReadData(baseUrl: String, token: String) {

        val logging = HttpLoggingInterceptor()
        logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
        val httpClient = OkHttpClient.Builder().addInterceptor(logging)

        httpClient.addInterceptor { chain ->
            val request: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            chain.proceed(request)
        }

        val retrofit: Retrofit =
                Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(baseUrl)
                        .client(httpClient.build())
                        .build()

        val service = retrofit.create(Interface::class.java)
        val call = service.getBookshelvesBooks("4")

        call.enqueue(object : retrofit2.Callback<BookshelvesVolumeModels> {
            override fun onResponse(
                    call: Call<BookshelvesVolumeModels>,
                    response: Response<BookshelvesVolumeModels>
            ) {
                if (response.code() == 200) {

                    val volumes: BookshelvesVolumeModels = response.body()
                    val readBookCount = findViewById<TextView>(R.id.readBooksCount)
                    val readPageCount = findViewById<TextView>(R.id.readPages)
                    val readTime = findViewById<TextView>(R.id.timeSpentReading)
                    var pageCount = 0

                    for (i in 0 until volumes.totalItems) {
                        pageCount += volumes.items!![i].volumeInfo!!.pageCount
                    }

                    readBookCount.append(" " + volumes.totalItems.toString())
                    readPageCount.append(" $pageCount")
                    readTime.append(" " + calculateReadingTime(pageCount) + " days")

                }
            }

            override fun onFailure(call: Call<BookshelvesVolumeModels>?, t: Throwable?) {
                Toast.makeText(applicationContext, "An ERROR occurred", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun retrieveToReadBooksData() {
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
                        getToReadData(
                                "https://www.googleapis.com/books/v1/",
                                result.result.getString(AccountManager.KEY_AUTHTOKEN)!!
                        )
                    }
                },                        // Callback called when a token is successfully acquired
                Handler().apply { OnError() }       // Callback called if an error occurs
        )
    }

    private fun getToReadData(baseUrl: String, token: String) {

        val logging = HttpLoggingInterceptor()
        logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
        val httpClient = OkHttpClient.Builder().addInterceptor(logging)

        httpClient.addInterceptor { chain ->
            val request: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            chain.proceed(request)
        }

        val retrofit: Retrofit =
                Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(baseUrl)
                        .client(httpClient.build())
                        .build()

        val service = retrofit.create(Interface::class.java)
        val call = service.getBookshelvesBooks("2")

        call.enqueue(object : retrofit2.Callback<BookshelvesVolumeModels> {
            override fun onResponse(
                    call: Call<BookshelvesVolumeModels>,
                    response: Response<BookshelvesVolumeModels>
            ) {
                if (response.code() == 200) {

                    val volumes: BookshelvesVolumeModels = response.body()
                    val toReadBookCount = findViewById<TextView>(R.id.toReadBooksCount)
                    val toReadPageCount = findViewById<TextView>(R.id.pagesLeftToRead)
                    val toReadTime = findViewById<TextView>(R.id.neededTime)
                    var pageCount = 0

                    for (i in 0 until volumes.totalItems) {
                        pageCount += volumes.items!![i].volumeInfo!!.pageCount
                    }

                    toReadBookCount.append(" " + volumes.totalItems.toString())
                    toReadPageCount.append(" $pageCount")
                    toReadTime.append(" " + calculateReadingTime(pageCount) + " days")
                }
            }

            override fun onFailure(call: Call<BookshelvesVolumeModels>?, t: Throwable?) {
                Toast.makeText(applicationContext, "An ERROR occurred", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun retrieveCurrentlyReadingBooksData() {
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
                        getCurrentlyReadingData(
                                "https://www.googleapis.com/books/v1/",
                                result.result.getString(AccountManager.KEY_AUTHTOKEN)!!
                        )
                    }
                },                        // Callback called when a token is successfully acquired
                Handler().apply { OnError() }       // Callback called if an error occurs
        )

    }

    private fun getCurrentlyReadingData(baseUrl: String, token: String) {

        val logging = HttpLoggingInterceptor()
        logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
        val httpClient = OkHttpClient.Builder().addInterceptor(logging)

        httpClient.addInterceptor { chain ->
            val request: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            chain.proceed(request)
        }

        val retrofit: Retrofit =
                Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(baseUrl)
                        .client(httpClient.build())
                        .build()

        val service = retrofit.create(Interface::class.java)
        val call = service.getBookshelvesBooks("3")

        call.enqueue(object : retrofit2.Callback<BookshelvesVolumeModels> {
            override fun onResponse(
                    call: Call<BookshelvesVolumeModels>,
                    response: Response<BookshelvesVolumeModels>
            ) {
                if (response.code() == 200) {

                    val volumes: BookshelvesVolumeModels = response.body()
                    val currentlyReadingBookCount = findViewById<TextView>(R.id.nowReadingBooks)

                    currentlyReadingBookCount.append(" " + volumes.totalItems.toString())
                }
            }

            override fun onFailure(call: Call<BookshelvesVolumeModels>?, t: Throwable?) {
                Toast.makeText(applicationContext, "An ERROR occurred", Toast.LENGTH_LONG).show()
            }
        })
    }

}