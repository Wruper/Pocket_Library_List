package com.example.pocket_library_list

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


class Delete: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.delete)

         val deleteButton = findViewById<Button>(R.id.deleteButton)

        deleteButton.setOnClickListener {
            getAuthTokenForDelete()

        }

        retrieveBasicInfo()
        addValueToCategorySpinner()

    }



private fun getBookTitleValue(): String{
    val spinner = findViewById<Spinner>(R.id.spinnerTitle)
    return spinner.selectedItem.toString()
}



private fun getCategorySpinnerValue(): String {
    val spinner = findViewById<Spinner?>(R.id.spinnerCategory)
    return when (spinner.selectedItem.toString()) {
        "Read" -> "4"
        "Currently reading" -> "3"
        "To read" -> "2"
        else -> "Error" // Edit this with toast
    }
}



private fun addValueToCategorySpinner() {

    val spinner = findViewById<Spinner>(R.id.spinnerCategory)
    val categoryArrayList = arrayListOf("Read", "Currently reading", "To read")
    val readList = ArrayList<String>()
    val currentlyReadingArrayList = ArrayList<String>()
    val readingArrayList = ArrayList<String>()
    val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            categoryArrayList)

    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.adapter = arrayAdapter
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            val spinnerValue = parent.getItemAtPosition(position).toString()
            Toast.makeText(parent.context, "Selected: $spinnerValue", Toast.LENGTH_SHORT).show()
            when (spinnerValue){
                "Read" -> getAuthTokenForCategoriesBooks(readList)
                "Currently reading" -> getAuthTokenForCategoriesBooks(currentlyReadingArrayList)
                "To read" -> getAuthTokenForCategoriesBooks(readingArrayList)

            }
        }

        override  fun onNothingSelected(parent: AdapterView<*>) {}
    }
}

    private fun addValueToBookNameSpinner(title: String, categoryArrayList: ArrayList<String>) {
        val spinner = findViewById<Spinner>(R.id.spinnerTitle)
        categoryArrayList.add(title)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryArrayList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val spinnerValue = parent.getItemAtPosition(position).toString()
                Toast.makeText(parent.context, "Selected: $spinnerValue", Toast.LENGTH_SHORT).show()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    fun getAuthTokenForCategoriesBooks(categoryArrayList: ArrayList<String>){
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
                        retrieveCategoriesBookTitles(
                                "https://www.googleapis.com/books/v1/",
                                result.result.getString(AccountManager.KEY_AUTHTOKEN)!!,
                                categoryArrayList
                        )
                    }
                },                        // Callback called when a token is successfully acquired
                Handler().apply { OnError() }       // Callback called if an error occurs
        )

    }


private fun retrieveCategoriesBookTitles(baseUrl: String, token: String, array: ArrayList<String>) {
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
    val call = service.getBookshelvesBooks(getCategorySpinnerValue())
    println(getCategorySpinnerValue())


    call.enqueue(object : retrofit2.Callback<BookshelvesVolumeModels> {
        override fun onResponse(
                call: Call<BookshelvesVolumeModels>,
                response: Response<BookshelvesVolumeModels>
        ) {
            if (response.code() == 200) {
                println("yesss")
                val volumes: BookshelvesVolumeModels = response.body()
                //Checks if the array is empty before inserting the values.
                // If there are values in the array, the array is emptied, so that the values from previous
                //call don't stack
                when {
                    array.size == 0 && volumes.totalItems > 0 -> {
                        for (i in 0 until volumes.totalItems) {
                            addValueToBookNameSpinner(volumes.items!![i].volumeInfo!!.title, array)
                        }
                        val deleteButton = findViewById<Button>(R.id.deleteButton)
                        deleteButton.isEnabled = true
                    }
                    volumes.totalItems == 0 -> { // prbaudit ja vel deleto unpec refresh
                        array.clear()
                        addValueToBookNameSpinner("None", array)
                        val deleteButton = findViewById<Button>(R.id.deleteButton)
                        deleteButton.isEnabled = false
                        // lai nepostotu ja nav book value


                    }
                    else -> {
                        array.clear()
                        for (i in 0 until volumes.totalItems) {
                            println(volumes.totalItems)
                            addValueToBookNameSpinner(volumes.items!![i].volumeInfo!!.title, array)
                        }
                        val deleteButton = findViewById<Button>(R.id.deleteButton)
                        deleteButton.isEnabled = true
                    }
                }


            }
        }


        override fun onFailure(call: Call<BookshelvesVolumeModels>?, t: Throwable?) {
            println("blaaa")
        }
    })
}

    @SuppressLint("SetTextI18n")
    private fun retrieveBasicInfo() {
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personPhoto = acct.photoUrl
            val avatar: ImageView = findViewById(R.id.profilePic)
            val text: TextView = findViewById(R.id.name)
            text.text = " $personName"
            Picasso.get().load(personPhoto).into(avatar);

        }
    }

class OnError : Callback {
    fun handleMessage(msg: Message?): Boolean {
        Log.e("onError", "ERROR")
        return false
    }
}

private fun getAuthTokenForDelete() {
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
                    onDeleteGetData(
                            "https://www.googleapis.com/books/v1/",
                            result.result.getString(AccountManager.KEY_AUTHTOKEN)!!
                    )
                }
            },                        // Callback called when a token is successfully acquired
            Handler().apply { OnError() }       // Callback called if an error occurs
    )

}

    private fun onDeleteGetData(baseUrl: String, token: String) {

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

        val call = service.getBooksInfo(getBookTitleValue())


        call.enqueue(object : retrofit2.Callback<SearchedBooksModel> {
            override fun onResponse(
                    call: Call<SearchedBooksModel>,
                    response: Response<SearchedBooksModel>
            ) {
                if (response.code() == 200) {
                    println("bebeebebbe")
                    val volumes: SearchedBooksModel = response.body()
                    deleteData(baseUrl, token, volumes.items!![0].id)

                }
            }

            override fun onFailure(call: Call<SearchedBooksModel>?, t: Throwable?) {
                Toast.makeText(applicationContext, "The ISBN numbers is either invalid" +
                        "or cannot be found on the database.", Toast.LENGTH_LONG).show()
            }

        })
    }



private fun deleteData(baseUrl: String, token: String, bookID: String){
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

    val call = service.removeNewBook(getCategorySpinnerValue(), bookID)
    call.enqueue(object : retrofit2.Callback<BookshelvesVolumeModels> {
        override fun onResponse(
                call: Call<BookshelvesVolumeModels>,
                response: Response<BookshelvesVolumeModels>
        ) {

            // edit this
            Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
            val intent = intent
            finish()
            startActivity(intent)
        }

        override fun onFailure(call: Call<BookshelvesVolumeModels>?, t: Throwable?) {
            Toast.makeText(applicationContext, "Something went wrong," +
                    "please check your internet connection", Toast.LENGTH_LONG).show()
        }

    })
}

}
