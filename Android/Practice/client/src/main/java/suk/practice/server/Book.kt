package suk.practice.server

import android.os.Parcel
import android.os.Parcelable

/**
 * @author SuK
 * @time 2020/4/1 16:27
 * @des
 */
data class Book(
    var name: String? = null,
    var price: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeValue(price)
    }

    fun readFromParcel(dest: Parcel) = with(dest) {
        name = readString()
        price = readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}