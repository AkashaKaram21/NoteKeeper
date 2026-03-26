package com.notekeeper.Retrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/*
* El NoteApi s'encarrega de conectar-se amb l'API
 */
class NoteAPI {
    companion object {
        private var mItemAPI: NoteService? = null

        @Synchronized
        fun API(): NoteService {
            if (mItemAPI == null) {

                val gsondateformat = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create()

                // Client HTTP insegur (només per desenvolupament)
                val unsafeOkHttpClient = getUnsafeOkHttpClient()

                mItemAPI = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gsondateformat))
                    .baseUrl("http://132.145.154.48:8081/")
                    .client(unsafeOkHttpClient) // Afegeix el client
                    .build()
                    .create(NoteService::class.java)
            }
            return mItemAPI!!
        }

        private fun getUnsafeOkHttpClient(): OkHttpClient {
            try {
                // Crea un trust manager que NO valida certificats
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                    }
                )

                // Instal·la el trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                return OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true } // Accepta qualsevol hostname
                    .build()

            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}