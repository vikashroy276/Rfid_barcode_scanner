package com.mespl.emp_asset_mgmtapp.restapi

import com.google.gson.GsonBuilder
import com.mespl.emp_asset_mgmtapp.utils.CacheUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiClient {
    private var BASE_URL = ""
    private var retrofit: Retrofit? = null
    private var apiService: ApiService? = null

    fun refreshRetrofit() {
        apiService = null
        retrofit = null
    }

    val client: ApiService?
        get() {
            if (retrofit == null) {
                BASE_URL = getBaseURL()

                val gson = GsonBuilder()
                    .setLenient()
                    .create()


                val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
                    .callTimeout(5, TimeUnit.SECONDS)
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)

                httpClient.addInterceptor(object : Interceptor {
                    @Throws(IOException::class)
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val request: Request = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .build()
                        return chain.proceed(request)
                    }
                })

                retrofit = Retrofit.Builder()
//                    .baseUrl("http://192.168.2.8:7015/api/empAsset/")
                    .baseUrl(BASE_URL+"/api/empAsset/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(getUnsafeOkHttpClient())
                    .build()

                apiService = retrofit!!.create(ApiService::class.java)
            }
            return apiService
        }
    fun getUnsafeOkHttpClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
            }
        )

        val interceptor = HttpLoggingInterceptor()
        run { interceptor.setLevel(HttpLoggingInterceptor.Level.BODY) }

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(interceptor)
            .build()
    }
    private fun getBaseURL(): String {
        val storedUrl = CacheUtils.getBASEURL()
        return if (storedUrl.isNullOrEmpty()) {
            "http://" // Provide a default URL if needed
        } else {
            storedUrl
        }
    }
}
