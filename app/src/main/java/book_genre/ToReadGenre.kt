package book_genre

import adapters.CategoryAdapter
import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocket_library_list.R
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

class ToReadGenre : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_list)

        val recyclerView = findViewById<RecyclerView>(R.id.categories_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        retrieveToReadBooks(recyclerView)
    }

    class OnError : Callback {
        fun handleMessage(): Boolean {
            Log.e("onError", "ERROR")
            return false
        }
    }

    /*
    This function checks if a retrieved books has a category or not. If the book
    does not have a category, instead of null, in the RecyclerViewer the book will be listed in
    "No category" section.
    */
    private fun checkIfNoCategory(category: String): String {
        return if (category == "null") {
            "No category"
        } else {
            category
        }
    }

    /*
    This function retrieves End-users Google OAuth token, that is used for API requests
    just like retrieves all books in To Read bookshelf.
    */
    private fun retrieveToReadBooks(layout: RecyclerView) {
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
                        result.result.getString(AccountManager.KEY_AUTHTOKEN)!!,
                        layout
                    )
                }
            },                        // Callback called when a token is successfully acquired
            Handler().apply { OnError() }       // Callback called if an error occurs
        )
    }

    /*
    Retrieves all books in End-user To Read bookshelf
    */
    private fun getData(baseUrl: String, token: String, layout: RecyclerView) {

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
        val call = service.getBookshelvesBooks("2")


        call.enqueue(object : retrofit2.Callback<BookshelvesVolumeModels> {
            override fun onResponse(
                call: Call<BookshelvesVolumeModels>,
                response: Response<BookshelvesVolumeModels>
            ) {
                if (response.code() == 200) {
                    val volumes: BookshelvesVolumeModels = response.body()
                    //Creates an ArrayList where all retrieved book categories will be inserted.
                    val categoryList = ArrayList<String>()
                    for (i in 0 until volumes.totalItems) {
                        //Checks if the book has a category.
                        categoryList.add(
                            checkIfNoCategory
                                (volumes.items!![i].volumeInfo?.categories?.get(0).toString())
                        )
                    }
                    if (categoryList.size <= 1) {
                        runOnUiThread {
                            layout.adapter = CategoryAdapter(categoryList, "2")
                        }
                    }
                    //If the list has more than one item, then distinct is used, which removes
                    // items that have been repeated.
                    else {
                        val newCategoryList: ArrayList<String> = categoryList.distinct()
                                as ArrayList<String>
                        runOnUiThread {
                            layout.adapter = CategoryAdapter(newCategoryList, "2")
                        }
                    }

                }
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