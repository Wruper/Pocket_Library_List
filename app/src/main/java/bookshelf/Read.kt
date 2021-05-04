package bookshelf

import adapters.ReadListView
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
import models.BookshelvesVolumeModels
import interfaces.Interface
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.security.auth.callback.Callback


class Read : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_book_screen)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        retrieveReadBooks(recyclerView)
    }

    class OnError : Callback {
        fun handleMessage(msg: Message?): Boolean {
            Log.e("onError", "ERROR")
            return false
        }
    }

    //Retrieves End-users OAuth token in order to use Google Book API with users information
    private fun retrieveReadBooks(layout: RecyclerView) {
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

    //Retrieves data from "Read" bookshelf
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
        val call = service.getBookshelvesBooks("4")


        call.enqueue(object : retrofit2.Callback<BookshelvesVolumeModels> {
            override fun onResponse(
                    call: Call<BookshelvesVolumeModels>,
                    response: Response<BookshelvesVolumeModels>
            ) {
                if (response.code() == 200) {
                    val volumes: BookshelvesVolumeModels = response.body()

                    runOnUiThread {

                        layout.adapter = ReadListView(volumes)

                    }
                }
            }

            override fun onFailure(call: Call<BookshelvesVolumeModels>?, t: Throwable?) {
                Toast.makeText(applicationContext, "An ERROR occurred", Toast.LENGTH_LONG).show()
            }
        })
    }

}






















