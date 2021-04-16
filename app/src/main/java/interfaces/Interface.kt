package interfaces

import models.BookshelveVolumeModels
import models.SearchedBooksModel
import models.SelectedBookShelveVolumes
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Interface {

    @GET("mylibrary/bookshelves/{shelf}")
   fun getBookshelvesVolumeInfo (@Path("shelf") shelfType: String): Call<SelectedBookShelveVolumes>


    @GET("mylibrary/bookshelves/{shelf}/volumes")
   fun getBookshelvesBooks (@Path("shelf") shelfType: String): Call<BookshelveVolumeModels>


  @GET("volumes?")
  fun getBooksInfo(@Query("q") q: String):Call<SearchedBooksModel>

}