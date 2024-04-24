package com.vyarth.ellipsify.model

import android.os.Parcel
import android.os.Parcelable

data class Comment(
    val id: String = "",
    val postId: String = "",
    val author: String = "",
    val authorId: String = "",
    val content: String = "",
    val timestamp: Long = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(postId)
        parcel.writeString(author)
        parcel.writeString(authorId)
        parcel.writeString(content)
        parcel.writeLong(timestamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}
