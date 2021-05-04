package interfaces

import models.BookshelveVolumeModels
import models.SearchedBooksModel
import models.SelectedBookShelveVolumes
import retrofit2.Call
import retrofit2.http.*

interface Interface {

    @GET("mylibrary/bookshelves/{shelf}")
   fun getBookshelvesVolumeInfo (@Path("bookshelf") shelfType: String): Call<SelectedBookShelveVolumes>


    @GET("mylibrary/bookshelves/{shelf}/volumes")
   fun getBookshelvesBooks (@Path("bookshelf") shelfType: String): Call<BookshelveVolumeModels>


  @GET("volumes?")
  fun getBooksInfo(@Query("q") q: String):Call<SearchedBooksModel>



  @POST("mylibrary/bookshelves/{shelf}/addVolume?")
  fun postNewBook(@Path("bookshelf") shelfType: String, @Query ("volumeId") volumeId: String)
  : Call<BookshelveVolumeModels> // uztaisit modeli

  @POST("mylibrary/bookshelves/{shelf}/removeVolume?")
  fun removeNewBook(@Path("bookshelf") shelfType: String, @Query ("volumeId") volumeId: String)
            : Call<BookshelveVolumeModels>

}