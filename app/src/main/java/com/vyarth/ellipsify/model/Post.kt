package com.vyarth.ellipsify.model

import android.os.Parcel
import android.os.Parcelable

data class Post(
    val id: String = "",
    val author: String = "",
    val authorId: String = "",
    val pseudoName: String = "",
    val content: String = "",
    val timestamp: Long = 0,
    val likes: Int = 0,
    var likedBy: List<String> = listOf(),
    val comments: List<Comment> = listOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readInt(),
        parcel.createStringArrayList() ?: listOf(),
        mutableListOf<Comment>().apply {
            parcel.readList(this, Comment::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(author)
        parcel.writeString(authorId)
        parcel.writeString(pseudoName)
        parcel.writeString(content)
        parcel.writeLong(timestamp)
        parcel.writeInt(likes)
        parcel.writeStringList(likedBy)
        parcel.writeList(comments)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}
