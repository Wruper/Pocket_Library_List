package book_genre

import adapters.CategoryAdapter
import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocket_library_list.R
import interfaces.Interface
import models.BookshelveVolumeModels
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.security.auth.callback.Callback

class ToReadGenre: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_list)

        val recyclerView = findViewById<RecyclerView>(R.id.categories_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        retrieveAuthToken(recyclerView)

    }


class OnError : Callback {
    fun handleMessage(msg: Message?): Boolean {
        Log.e("onError", "ERROR")
        return false
    }
}

    private fun checkIfNoCategory(category: String): String {
        return if(category == "null"){
            "No category"
        }
        else{
            category
        }
    }


private fun retrieveAuthToken(layout:RecyclerView) {
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

private fun getData(baseUrl: String, token: String, layout:RecyclerView) {

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


    call.enqueue(object : retrofit2.Callback<BookshelveVolumeModels> {
        override fun onResponse(
                call: Call<BookshelveVolumeModels>,
                response: Response<BookshelveVolumeModels>
        ) {
            if (response.code() == 200) {
                val volumes: BookshelveVolumeModels = response.body()
                //ieliek visus itemus listaaa
                val categoryList = ArrayList<String>()
                for(i in 0 until volumes.totalItems){
                    //parbauda vai nav book bez zanra
                    categoryList.add(checkIfNoCategory
                    (volumes.items!![i].volumeInfo?.categories?.get(0).toString()))
                }
                //ja ir tikai 1 vai nav itemu, tad nelieto distinct
                if(categoryList.size <= 1){
                    runOnUiThread {

                        layout.adapter = CategoryAdapter(categoryList,"2")

                    }
                }
                //ja ir vairak par 1 itemu, izmantojoam distinct
                else{
                    // iznem atkartojosos itemus
                    val newCategoryList: ArrayList<String> = categoryList.distinct()
                            as ArrayList<String>

                    runOnUiThread {

                        layout.adapter = CategoryAdapter(newCategoryList,"2")

                    }
                }

            }
        }


        override fun onFailure(call: Call<BookshelveVolumeModels>?, t: Throwable?) {
            println("blaaa")
        }
    })
}

}