package interfaces

import models.BookshelvesVolumeModels
import models.SearchedBooksModel
import models.SelectedBookShelveVolumes
import retrofit2.Call
import retrofit2.http.*

interface Interface {

    @GET("mylibrary/bookshelves/{shelf}")
    fun getBookshelvesVolumeInfo(@Path("shelf") shelfType: String): Call<SelectedBookShelveVolumes>

    @GET("mylibrary/bookshelves/{shelf}/volumes")
    fun getBookshelvesBooks(@Path("shelf") shelfType: String): Call<BookshelvesVolumeModels>

    @GET("volumes?")
    fun getBooksInfo(@Query("q") q: String): Call<SearchedBooksModel>

    @POST("mylibrary/bookshelves/{shelf}/addVolume?")
    fun postNewBook(@Path("shelf") shelfType: String, @Query("volumeId") volumeId: String)
            : Call<BookshelvesVolumeModels>

    @POST("mylibrary/bookshelves/{shelf}/removeVolume?")
    fun removeNewBook(@Path("shelf") shelfType: String, @Query("volumeId") volumeId: String)
            : Call<BookshelvesVolumeModels>

}