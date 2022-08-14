package com.woowahan.data.datasource

import com.google.gson.Gson
import com.woowahan.data.apiservice.BestBanchanApiService
import com.woowahan.data.entity.BestBanchanEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
    private lateinit var mockRetrofit: Retrofit

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


}