package com.woowahan.data.util

import com.google.gson.Gson
import com.woowahan.data.apiservice.BanchanDetailApiService
import com.woowahan.data.apiservice.MainDishBanchanApiService
import com.woowahan.data.entity.ApiIsNotSuccessful
import com.woowahan.data.entity.ApiStatusCodeNotOk
import com.woowahan.data.entity.BanchanDetailEntity
import com.woowahan.data.entity.MainDishBanchanEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.HttpURLConnection

@OptIn(ExperimentalCoroutinesApi::class)
class RetrofitResponseConvertUtilTest {
    private lateinit var mockServer: MockWebServer
    private lateinit var mockRetrofit: Retrofit

    @Before
    fun setUpMockService(){
        mockServer = MockWebServer()
        mockServer.start()
        mockRetrofit = Retrofit.Builder().apply {
            baseUrl(mockServer.url(""))
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }

    @After
    fun downMockServer(){
        mockServer.shutdown()
    }

    private fun readResponse(fileName: String): String{
        return File("src/test/java/com/resources/$fileName").readText()
    }

    @Test
    fun getDataWithStatusCode_successResponse_equals() = runTest {
        //Given
        val responseJson = "{ " +
                "\"statusCode\": 200, " +
                "\"body\": [] " +
                "}"
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)
        val mockMainBanchanService = mockRetrofit.create(MainDishBanchanApiService::class.java)

        //When
        val actualResult = RetrofitResponseConvertUtil.getData(mockMainBanchanService.fetchMainDishBanchans())

        //Then
        val expected = Gson().fromJson(responseJson, MainDishBanchanEntity::class.java)
        assertThat(actualResult).isEqualTo(expected)
    }

    @Test
    fun getDataWithStatusCode_statusCodeNotOkResponse_throwStatusCodeNotOk() = runTest {
        //Given
        val statusCode = 500
        val responseJson = "{ " +
                "\"statusCode\": $statusCode, " +
                "\"body\": [] " +
                "}"
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)
        val mockMainBanchanService = mockRetrofit.create(MainDishBanchanApiService::class.java)

        //When
        val res = mockMainBanchanService.fetchMainDishBanchans()
        val actualResult = Assertions.catchThrowable {
            RetrofitResponseConvertUtil.getData(res, res.body()?.statusCode)
        }

        //Then
        assertThat(actualResult)
            .isInstanceOf(ApiStatusCodeNotOk::class.java)
            .isEqualTo(ApiStatusCodeNotOk(statusCode))
    }

    @Test
    fun getDataWithStatusCode_responseCodeNotOk_throwResponseCodeNotOk() = runTest {
        //Given
        val responseJson = "{ " +
                "\"statusCode\": 200, " +
                "\"body\": [] " +
                "}"
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_FORBIDDEN)
            setBody(responseJson)
        }
        mockServer.enqueue(response)
        val mockMainBanchanService = mockRetrofit.create(MainDishBanchanApiService::class.java)

        //When
        val res = mockMainBanchanService.fetchMainDishBanchans()
        val actualResult = Assertions.catchThrowable {
            RetrofitResponseConvertUtil.getData(res, res.body()?.statusCode)
        }

        //Then
        val expected = ApiIsNotSuccessful(res.message())
        assertThat(actualResult)
            .isInstanceOf(ApiIsNotSuccessful::class.java)
            .isEqualTo(expected)
    }

    @Test
    fun getDataWithStatusCode_responseCodeNotOkAndStatusCodeNotOk_throwResponseCodeNotOk() = runTest {
        //Given
        val responseJson = "{ " +
                "\"statusCode\": 500, " +
                "\"body\": [] " +
                "}"
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_FORBIDDEN)
            setBody(responseJson)
        }
        mockServer.enqueue(response)
        val mockMainBanchanService = mockRetrofit.create(MainDishBanchanApiService::class.java)

        //When
        val res = mockMainBanchanService.fetchMainDishBanchans()
        val actualResult = Assertions.catchThrowable {
            RetrofitResponseConvertUtil.getData(res, res.body()?.statusCode)
        }

        //Then
        val expected = ApiIsNotSuccessful(res.message())
        assertThat(actualResult)
            .isInstanceOf(ApiIsNotSuccessful::class.java)
            .isEqualTo(expected)
    }

    @Test
    fun getData_successResponse_equals() = runTest {
        //Given
        val responseJson = readResponse("detail_success.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(responseJson)
        }
        mockServer.enqueue(response)
        val mockBanchanDetailService = mockRetrofit.create(BanchanDetailApiService::class.java)

        //When
        val actualResult = mockBanchanDetailService.fetchBanchanDetail("").body()

        //Then
        val expected = Gson().fromJson(responseJson, BanchanDetailEntity::class.java)
        assertThat(actualResult)
            .isNotNull
            .isEqualTo(expected)
    }

    @Test
    fun getData_responseCodeNotOk_throwResponseCodeNotOk() = runTest {
        //Given
        val responseJson = readResponse("detail_success.json")
        val response = MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_FORBIDDEN)
            setBody(responseJson)
        }
        mockServer.enqueue(response)
        val mockBanchanDetailService = mockRetrofit.create(BanchanDetailApiService::class.java)

        //When
        val res = mockBanchanDetailService.fetchBanchanDetail("")
        val actualResult = Assertions.catchThrowable {
            RetrofitResponseConvertUtil.getData(res)
        }

        //Then
        val expected = ApiIsNotSuccessful(res.message())
        assertThat(actualResult)
            .isInstanceOf(ApiIsNotSuccessful::class.java)
            .isEqualTo(expected)
    }

}