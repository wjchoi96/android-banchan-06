package com.woowahan.data.datasource

import com.google.gson.Gson
import com.woowahan.data.apiservice.BestBanchanApiService
import com.woowahan.data.apiservice.MainDishBanchanApiService
import com.woowahan.data.apiservice.SideDishBanchanApiService
import com.woowahan.data.apiservice.SoupDishBanchanApiService
import com.woowahan.data.entity.BestBanchanEntity
import com.woowahan.data.entity.MainDishBanchanEntity
import com.woowahan.data.entity.SideDishBanchanEntity
import com.woowahan.data.entity.SoupDishBanchanEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
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
    private lateinit var banchansDataSource: BanchansRetrofitDataSourceImpl

    @Before
    fun setUpBanchansRetrofitDataSourceImpl(){
        mockServer = MockWebServer()
        mockServer.start()
        mockRetrofit = Retrofit.Builder().apply {
            baseUrl(mockServer.url(""))
            addConverterFactory(GsonConverterFactory.create())
        }.build()

        banchansDataSource = BanchansRetrofitDataSourceImpl(
            mockRetrofit.create(BestBanchanApiService::class.java),
            mockRetrofit.create(MainDishBanchanApiService::class.java),
            mockRetrofit.create(SoupDishBanchanApiService::class.java),
            mockRetrofit.create(SideDishBanchanApiService::class.java)
        )
    }

    private
    fun readResponse(fileName: String): String{
        return File("src/test/java/com/resources/$fileName").readText()
    }

    @After fun downMockServer(){
        mockServer.shutdown()
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

        //When
        val actualResult = banchansDataSource.fetchBestBanchans()

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
    fun fetchMainDishBanchans_mockServerSuccessRequest_isNotNullAndEquals() = runTest {
        //Given
        val responseJson = readResponse("main_dish_success.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = banchansDataSource.fetchMainDishBanchans()

        //Then
        val expected = Gson().fromJson(responseJson, MainDishBanchanEntity::class.java)
        assertThat(actualResult).isNotNull.isEqualTo(expected)
    }

    @Test
    fun fetchMainDishBanchans_mockServerFailRequest_isEquals() = runTest {
        //Given
        val responseJson = readResponse("main_dish_fail.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = banchansDataSource.fetchMainDishBanchans()

        //Then
        val expected = Gson().fromJson(responseJson, MainDishBanchanEntity::class.java)
        assertThat(actualResult).isEqualTo(expected)
    }


    @Test
    fun fetchSoupDishBanchans_mockServerSuccessRequest_isNotNullAndEquals() = runTest {
        //Given
        val responseJson = readResponse("soup_dish_success.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = banchansDataSource.fetchSoupDishBanchans()

        //Then
        val expected = Gson().fromJson(responseJson, SoupDishBanchanEntity::class.java)
        assertThat(actualResult).isNotNull.isEqualTo(expected)
    }

    @Test
    fun fetchSoupDishBanchans_mockServerFailRequest_isEquals() = runTest {
        //Given
        val responseJson = readResponse("soup_dish_fail.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = banchansDataSource.fetchSoupDishBanchans()

        //Then
        val expected = Gson().fromJson(responseJson, SoupDishBanchanEntity::class.java)
        assertThat(actualResult).isEqualTo(expected)
    }

    @Test
    fun fetchSideDishBanchans_mockServerSuccessRequest_isNotNullAndEquals() = runTest {
        //Given
        val responseJson = readResponse("side_dish_success.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = banchansDataSource.fetchSideDishBanchans()

        //Then
        val expected = Gson().fromJson(responseJson, SideDishBanchanEntity::class.java)
        assertThat(actualResult).isNotNull.isEqualTo(expected)
    }

    @Test
    fun fetchSideDishBanchans_mockServerFailRequest_isEquals() = runTest {
        //Given
        val responseJson = readResponse("side_dish_fail.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = banchansDataSource.fetchSideDishBanchans()

        //Then
        val expected = Gson().fromJson(responseJson, SideDishBanchanEntity::class.java)
        assertThat(actualResult).isEqualTo(expected)
    }



}