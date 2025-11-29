package com.example.edukid_android.utils

import com.example.edukid_android.models.AddChildRequest
import com.example.edukid_android.models.ChildResponse
import com.example.edukid_android.models.CreateGiftRequest
import com.example.edukid_android.models.LoginRequest
import com.example.edukid_android.models.ParentResponse
import com.example.edukid_android.models.QRCodeResponse
import com.example.edukid_android.models.SignUpRequest
import com.example.edukid_android.models.SignUpResponse
import com.example.edukid_android.models.UpdateParentRequest
import com.example.edukid_android.models.GenerateQuizRequest
import com.example.edukid_android.models.SubmitAnswersRequest
import com.example.edukid_android.models.ForgotPasswordRequest
import com.example.edukid_android.models.ResetPasswordRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.DELETE

interface ApiService {
    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse>
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<SignUpResponse>
    
    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ResponseBody>
    
    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResponseBody>
    
    @POST("parents/{parentId}/kids")
    suspend fun addChild(
        @Path("parentId") parentId: String,
        @Body request: AddChildRequest
    ): Response<ParentResponse>
    
    @GET("parents/{parentId}/kids/{kidId}/qr")
    suspend fun getQRCode(
        @Path("parentId") parentId: String,
        @Path("kidId") kidId: String
    ): Response<QRCodeResponse>
    
    @GET("parents/child/{childId}")
    suspend fun getChildById(
        @Path("childId") childId: String
    ): Response<ChildResponse>

    @POST("parents/{parentId}/kids/{kidId}/quizzes")
    suspend fun generateQuiz(
        @Path("parentId") parentId: String,
        @Path("kidId") kidId: String,
        @Body request: GenerateQuizRequest
    ): Response<ResponseBody>

    @GET("parents/{parentId}/kids/{kidId}/gifts")
    suspend fun getGifts(
        @Path("parentId") parentId: String,
        @Path("kidId") kidId: String
    ): Response<List<com.example.edukid_android.models.GiftResponse>>

    @POST("parents/{parentId}/kids/{kidId}/gifts")
    suspend fun createGift(
        @Path("parentId") parentId: String,
        @Path("kidId") kidId: String,
        @Body request: CreateGiftRequest
    ): Response<ResponseBody>

    @DELETE("parents/{parentId}/kids/{kidId}/gifts/{giftId}")
    suspend fun deleteGift(
        @Path("parentId") parentId: String,
        @Path("kidId") kidId: String,
        @Path("giftId") giftId: String
    ): Response<ResponseBody>

    @POST("parents/{parentId}/kids/{kidId}/gifts/{giftId}/buy")
    suspend fun buyGift(
        @Path("parentId") parentId: String,
        @Path("kidId") kidId: String,
        @Path("giftId") giftId: String
    ): Response<ChildResponse>

    @POST("parents/{parentId}/kids/{kidId}/quizzes")
    suspend fun generateQuizBasedOnNeeds(
        @Path("parentId") parentId: String,
        @Path("kidId") kidId: String,
        @Body request: Map<String, String> = emptyMap()
    ): Response<ResponseBody>

//    @PATCH("parents/{parentId}/kids/{kidId}/quizzes/{quizId}")
//    suspend fun updateQuiz(
//        @Path("parentId") parentId: String,
//        @Path("kidId") kidId: String,
//        @Path("quizId") quizId: String,
//        @Body request: UpdateQuizRequest
//    ): Response<com.example.edukid_android.models.ChildResponse>

    @POST("parents/{parentId}/kids/{kidId}/quizzes/{quizId}/submit")
    suspend fun submitQuizAnswers(
        @Path("parentId") parentId: String,
        @Path("kidId") kidId: String,
        @Path("quizId") quizId: String,
        @Body request: SubmitAnswersRequest
    ): Response<ChildResponse>

    @DELETE("parents/{parentId}/kids/{kidId}")
    suspend fun deleteChild(
        @Path("parentId") parentId: String,
        @Path("kidId") kidId: String
    ): Response<Void>

    @PATCH("parents/{id}")
    suspend fun updateParent(
        @Path("id") id: String,
        @Body request: UpdateParentRequest
    ): Response<ParentResponse>

    @GET("parents/{id}")
    suspend fun getParent(
        @Path("id") id: String
    ): Response<ParentResponse>
}

