package book_categories

import adapters.BooksByGenre
import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocket_library_list.R
import interfaces.Interface
import models.BookshelvesVolumeModels
import models.Item
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import bookshelf.Read


class SelectedCatBooks : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_book_screen)

        val intent = intent
        // Retrieves the bookshelf from previous view.
        val shelfType = intent.extras!!.getString("shelfType")

        var category = ""
        /* Checks if the category, that came from the previous view, is a "No category"
        category or not.If it is, then the category is changed back to null. This is done
        so that later if the End-user wants to see books that have no category, the books
        which are have no category (null) can be found. */
        category = if (intent.extras!!.getString("bookCategory") == "No category") {
            "null"
        } else {
            intent.extras!!.getString("bookCategory").toString()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        getBookInfo(recyclerView, shelfType, category)
    }

    /*
    This function retrieves End-users Google OAuth token, that is used for API requests
    to retrieve book information.
    */
    private fun getBookInfo(layout: RecyclerView, shelfType: String?, category: String?) {
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
                        layout,
                        shelfType,
                        category
                    )
                }
            },                        // Callback called when a token is successfully acquired
            Handler().apply { Read.OnError() }       // Callback called if an error occurs
        )

    }

    /*
    Retrieves all books that are in the specified category and bookshelf.
    */
    private fun getData(
        baseUrl: String, token: String, layout: RecyclerView,
        shelfType: String?, category: String?
    ) {

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
        val call = service.getBookshelvesBooks(shelfType!!)

        call.enqueue(object : retrofit2.Callback<BookshelvesVolumeModels> {
            override fun onResponse(
                call: Call<BookshelvesVolumeModels>,
                response: Response<BookshelvesVolumeModels>
            ) {
                if (response.code() == 200) {
                    val volumes: BookshelvesVolumeModels = response.body()
                    /*
                    An ArrayList has been made to store all books in selected category.
                     */
                    val bookList = ArrayList<Item>()

                    for (i in 0 until volumes.totalItems) {
                        if (volumes.items!![i].volumeInfo?.categories?.get(0) == category)
                            bookList.add(volumes.items[i])
                        else if (category == "null" && volumes.items[i].volumeInfo?.categories == null) {
                            bookList.add(volumes.items[i])
                        }
                    }
                    runOnUiThread {
                        layout.adapter = BooksByGenre(bookList)
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
