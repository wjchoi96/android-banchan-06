package com.woowahan.data.datasource

import com.google.gson.Gson
import com.woowahan.data.apiservice.BestBanchanApiService
import com.woowahan.data.apiservice.MainDishBanchanApiService
import com.woowahan.data.apiservice.SideDishBanchanApiService
import com.woowahan.data.apiservice.SoupDishBanchanApiService
import com.woowahan.data.entity.BestBanchanEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.HttpURLConnection


@OptIn(ExperimentalCoroutinesApi::class)
class BanchansRetrofitDataSourceImplTest{

    private lateinit var mockServer: MockWebServer
    private lateinit var realRetrofit: Retrofit
    private lateinit var mockRetrofit: Retrofit
    private val baseUrl = "https://api.codesquad.kr/onban/"

    @Before
    fun initRetrofit(){
        realRetrofit = Retrofit.Builder().apply {
            baseUrl(baseUrl)
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

    @Before fun setUpMockServer(){
        mockServer = MockWebServer()
        mockServer.start()
        mockRetrofit = Retrofit.Builder().apply {
            baseUrl(mockServer.url(""))
            addConverterFactory(GsonConverterFactory.create())
        }.build()

    }

    private fun readResponse(fileName: String): String{
        return File("src/test/java/com/resources/$fileName").readText()
    }

    @Test
    fun fetchBestBanchans_mockServerSuccessRequest_isNotNullAndEquals() = runTest {
        //Given
        val responseJson = readResponse("best_success.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)
        val service = mockRetrofit.create(BestBanchanApiService::class.java)

        //When
        val actualResult = service.fetchBestBanchans().body()

        //Then
        val expected = Gson().fromJson(responseJson, BestBanchanEntity::class.java)
        assertThat(actualResult).isNotNull.isEqualTo(expected)
    }

    @Test
    fun fetchBestBanchans_mockServerFailRequest_isEquals() = runTest {
        //Given
        val responseJson = readResponse("best_fail.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)
        val service = mockRetrofit.create(BestBanchanApiService::class.java)

        //When
        val actualResult = service.fetchBestBanchans().body()

        //Then
        val expected = Gson().fromJson(responseJson, BestBanchanEntity::class.java)
        assertThat(actualResult).isEqualTo(expected)
    }

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