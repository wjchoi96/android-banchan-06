package com.woowahan.data.datasource

import com.woowahan.data.apiservice.BestBanchanApiService
import com.woowahan.data.apiservice.MainDishBanchanApiService
import com.woowahan.data.apiservice.SideDishBanchanApiService
import com.woowahan.data.apiservice.SoupDishBanchanApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@OptIn(ExperimentalCoroutinesApi::class)
class BanchansRetrofitDataSourceImplTest{

    private lateinit var server: MockWebServer
//    val successResponse by lazy {
//        MockResponse().apply {
//            setResponseCode(HttpURLConnection.HTTP_OK)
////            val jsonText = readSuccessJson(context)
////            setBody(jsonText)
//        }
//    }
//
//    private fun readSuccessJson(context:Context):String{
//        return fileReader(context,"bestBanchanResponse.json")
//    }
//    private fun fileReader(context: Context, fileName:String):String{
//        // context.classLoader.getResource()로
//        // json 위치를 참조합니다. (test/resources)
//        val file = File(context.classLoader.getResource(fileName).file)
//        return file.bufferedReader().use {
//            val str = it.readText()
//            it.close()
//            str
//        }
//    }

    private lateinit var retrofit: Retrofit
    @Before
    fun initRetrofit(){
        retrofit = Retrofit.Builder().apply {
            baseUrl("https://api.codesquad.kr/onban/")
            client(OkHttpClient.Builder().apply {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }.build())
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }

    @Test
    @Test
    fun fetchBestBanchans_realServerRequest_isNotNull() = runTest {
        //Given
        val service = realRetrofit.create(BestBanchanApiService::class.java)

        //When
        val res = service.fetchBestBanchans()

        //Then
        println(res.body()?.toString())
        assertThat(res.body()?.body).isNotNull
    }

    @Test
    fun fetchMainDishBanchans_realServerRequest_isNotNull() = runTest {
        //Given
        val service = realRetrofit.create(MainDishBanchanApiService::class.java)

        //WHen
        val res = service.fetchMainDishBanchans()

        //Then
        println(res.body()?.toString())
        assertThat(res.body()?.body).isNotNull
    }

    @Test
    fun fetchSoupDishBanchans_realServerRequest_isNotNull() = runTest {
        //Given
        val service = realRetrofit.create(SoupDishBanchanApiService::class.java)

        //When
        val res = service.fetchSoupDishBanchans()

        //Then
        println(res.body()?.toString())
        assertThat(res.body()?.body).isNotNull
    }

    @Test
    fun fetchSideDishBanchans_realServerRequest_isNotNull() = runTest {
        //Given
        val service = realRetrofit.create(SideDishBanchanApiService::class.java)

        //When
        val res = service.fetchSideDishBanchans()

        //Then
        println(res.body()?.toString())
        assertThat(res.body()?.body).isNotNull
    }
}