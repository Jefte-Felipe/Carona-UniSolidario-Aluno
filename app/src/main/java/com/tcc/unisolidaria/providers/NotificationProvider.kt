package com.tcc.unisolidaria.providers


import com.tcc.unisolidaria.api.IFCMApi
import com.tcc.unisolidaria.api.RetrofitClient
import com.tcc.unisolidaria.models.FCMBody
import com.tcc.unisolidaria.models.FCMResponse
import retrofit2.Call

class NotificationProvider {

    private val URL = "https://fcm.googleapis.com"

    fun sendNotification(body: FCMBody): Call<FCMResponse> {
        return RetrofitClient.getClient(URL).create(IFCMApi::class.java).send(body)
    }

}