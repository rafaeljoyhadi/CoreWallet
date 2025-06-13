package com.example.corewallet.models

import android.os.Parcel
import android.os.Parcelable

data class Contact(
    val name: String,
    val accountNumber: String,
    val profilePicture: String? = null,
    val contactBalance: Double = 0.0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(accountNumber)
        parcel.writeString(profilePicture)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}