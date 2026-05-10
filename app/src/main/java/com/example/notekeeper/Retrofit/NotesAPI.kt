package com.example.notekeeper.Retrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class NotesAPI {
    companion object {
        private var mItemAPI: NotesService? = null

        @Synchronized
        fun API(): NotesService {
            if (mItemAPI == null) {

                val gsondateformat = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create()

                mItemAPI = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gsondateformat))
                    .baseUrl("http://129.80.194.172:8081/")
                    .build()
                    .create(NotesService::class.java)
            }
            return mItemAPI!!
        }
    }
}
