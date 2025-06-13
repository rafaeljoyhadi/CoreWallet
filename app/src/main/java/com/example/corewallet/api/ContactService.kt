package com.example.corewallet.api

import com.example.corewallet.api.request.AddContactRequest
import com.example.corewallet.api.response.AddContactResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ContactService {
    @POST("contact")
    fun addContact(@Body req: AddContactRequest): Call<AddContactResponse>
}