package models
import com.google.gson.annotations.SerializedName


class BookshelveVolumeModels {

    @SerializedName("totalItems")
    val totalItems: Int = 0

    @SerializedName("items")
    val items: List<Item>? = null
}

    class Item {

        @SerializedName("volumeInfo")
        val volumeInfo: VolumeInfo? = null

    }

   class VolumeInfo {
        @SerializedName("title")
        val title: String = ""

        @SerializedName("authors")
        val authors: List<String>? = null

        @SerializedName("publisher")
        val publisher: String = ""

        @SerializedName("publishedDate")
        val publishedDate: String = ""


        @SerializedName("pageCount")
        val pageCount: Int = 0

        @SerializedName("categories")
        val categories: List<String>? = null

       @SerializedName("industryIdentifiers")
       val industryIdentifiers: List<IndustryIdentifiers>? = null


       @SerializedName("imageLinks")
       val imageLinks: ImageLinks? = null

       class ImageLinks{
           @SerializedName("thumbnail")
           val thumbnail: String = ""
       }



   }

class IndustryIdentifiers {
   @SerializedName("identifier")
    val identifier: String = ""
}




