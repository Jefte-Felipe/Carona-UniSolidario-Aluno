package com.tcc.unisolidaria.api

import com.tcc.unisolidaria.models.FCMBody
import com.tcc.unisolidaria.models.FCMResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IFCMApi {

    @Headers(
        "Content-Type:application/json",
        "AAAAfTKz8G4:APA91bEzyAmotNqdG9bbqcDhcCLSCipEbGZe3O0YK0hgXzUZMSqB2K7PJVXKV40PoJY5awhF5vXpYrDiC2WIVT6iKefAHSzNubZSUw-fXaXKeAbKia_niJ9TeBlrsXdsCiHxRN9EWgeJ"
    )
    @POST("fcm/send")
    fun send(@Body body: FCMBody): Call<FCMResponse>


}