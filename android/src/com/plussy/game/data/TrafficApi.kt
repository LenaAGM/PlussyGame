package com.plussy.game.data

import retrofit2.Call
import retrofit2.http.GET

interface TrafficApi {
    @GET("CX67h1bP")
    fun getInfo(): Call<String>
}