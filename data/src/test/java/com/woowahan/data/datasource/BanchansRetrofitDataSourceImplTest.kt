package com.woowahan.data.datasource

import com.google.gson.Gson
import com.woowahan.data.apiservice.BestBanchanApiService
import com.woowahan.data.apiservice.MainDishBanchanApiService
import com.woowahan.data.apiservice.SideDishBanchanApiService
import com.woowahan.data.apiservice.SoupDishBanchanApiService
import com.woowahan.data.entity.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.EOFException
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
    fun fetchBestBanchans_mockServerFailRequest_throwApiStatusCodeNotOk() {
        //Given
        val responseJson = readResponse("best_fail.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = catchThrowable {
            runTest { banchansDataSource.fetchBestBanchans() }
        }

        //Then
        assertThat(actualResult)
            .isInstanceOf(ApiStatusCodeNotOk::class.java)
    }

    @Test
    fun fetchBestBanchans_mockServerResponseCodeNotOk_throwApiIsNotSuccessful() {
        //Given
        val responseJson = readResponse("best_success.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_FORBIDDEN)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = catchThrowable {
            runTest { banchansDataSource.fetchBestBanchans() }
        }

        //Then
        assertThat(actualResult)
            .isInstanceOf(ApiIsNotSuccessful::class.java)
    }

    @Test
    fun fetchBestBanchans_mockServerEmptyBody_throwEOFException() {
        //Given
        val responseJson = ""
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = catchThrowable {
            runTest { banchansDataSource.fetchBestBanchans() }
        }

        //Then
        assertThat(actualResult)
            .isInstanceOf(EOFException::class.java)
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
    fun fetchMainDishBanchans_mockServerFailRequest_throwApiStatusCodeNotOk() {
        //Given
        val responseJson = readResponse("main_dish_fail.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = catchThrowable {
            runTest { banchansDataSource.fetchMainDishBanchans() }
        }

        //Then
        assertThat(actualResult)
            .isInstanceOf(ApiStatusCodeNotOk::class.java)
    }

    @Test
    fun fetchMainDishBanchans_mockServerResponseCodeNotOk_throwApiIsNotSuccessful() {
        //Given
        val responseJson = readResponse("main_dish_success.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_FORBIDDEN)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = catchThrowable {
            runTest { banchansDataSource.fetchMainDishBanchans() }
        }

        //Then
        assertThat(actualResult)
            .isInstanceOf(ApiIsNotSuccessful::class.java)
    }

    @Test
    fun fetchMainBanchans_mockServerEmptyBody_throwEOFException() {
        //Given
        val responseJson = ""
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = catchThrowable {
            runTest { banchansDataSource.fetchMainDishBanchans() }
        }

        //Then
        assertThat(actualResult)
            .isInstanceOf(EOFException::class.java)
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
    fun fetchSoupDishBanchans_mockServerFailRequest_throwApiStatusCodeNotOk() {
        //Given
        val responseJson = readResponse("soup_dish_fail.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = catchThrowable {
            runTest { banchansDataSource.fetchSoupDishBanchans() }
        }

        //Then
        assertThat(actualResult)
            .isInstanceOf(ApiStatusCodeNotOk::class.java)
    }

    @Test
    fun fetchSoupDishBanchans_mockServerResponseCodeNotOk_throwApiIsNotSuccessful() {
        //Given
        val responseJson = readResponse("soup_dish_success.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_FORBIDDEN)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = catchThrowable {
            runTest { banchansDataSource.fetchSoupDishBanchans() }
        }

        //Then
        assertThat(actualResult)
            .isInstanceOf(ApiIsNotSuccessful::class.java)
    }

    @Test
    fun fetchSoupBanchans_mockServerEmptyBody_throwEOFException() {
        //Given
        val responseJson = ""
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = catchThrowable {
            runTest { banchansDataSource.fetchSoupDishBanchans() }
        }

        //Then
        assertThat(actualResult)
            .isInstanceOf(EOFException::class.java)
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
    fun fetchSideDishBanchans_mockServerFailRequest_throwApiStatusCodeNotOk() {
        //Given
        val responseJson = readResponse("side_dish_fail.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = catchThrowable {
            runTest { banchansDataSource.fetchSideDishBanchans() }
        }

        //Then
        assertThat(actualResult)
            .isInstanceOf(ApiStatusCodeNotOk::class.java)
    }

    @Test
    fun fetchSideDishBanchans_mockServerResponseCodeNotOk_throwApiIsNotSuccessful() {
        //Given
        val responseJson = readResponse("side_dish_success.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_FORBIDDEN)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = catchThrowable {
            runTest { banchansDataSource.fetchSideDishBanchans() }
        }

        //Then
        assertThat(actualResult)
            .isInstanceOf(ApiIsNotSuccessful::class.java)
    }

    @Test
    fun fetchSideBanchans_mockServerEmptyBody_throwEOFException() {
        //Given
        val responseJson = ""
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)

        //When
        val actualResult = catchThrowable {
            runTest { banchansDataSource.fetchSideDishBanchans() }
        }

        //Then
        assertThat(actualResult)
            .isInstanceOf(EOFException::class.java)
    }



}