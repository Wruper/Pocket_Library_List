package models

import com.google.gson.annotations.SerializedName

class SearchedBooksModel {

    @SerializedName("items")
    val items: List<Item>? = null


    class Item {

        @SerializedName("id")
        val id: String = ""
    }
}
