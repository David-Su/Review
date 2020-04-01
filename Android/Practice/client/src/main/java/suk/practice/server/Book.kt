package suk.practice.server

import android.os.Parcel
import android.os.Parcelable

/**
 * @author SuK
 * @time 2020/4/1 16:27
 * @des
 */
data class Book(
    val name: String,
    val price: Int
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeInt(price)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Book> = object : Parcelable.Creator<Book> {
            override fun createFromParcel(source: Parcel): Book =
                Book(source)
            override fun newArray(size: Int): Array<Book?> = arrayOfNulls(size)
        }
    }
}