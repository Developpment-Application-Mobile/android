package com.example.edukid_android.utils

import com.example.edukid_android.models.AddChildRequest
import com.example.edukid_android.models.ChildResponse
import com.example.edukid_android.models.LoginRequest
import com.example.edukid_android.models.ParentResponse
import com.example.edukid_android.models.QRCodeResponse
import com.example.edukid_android.models.SignUpRequest
import com.example.edukid_android.models.SignUpResponse
import com.example.edukid_android.models.UpdateParentRequest
import com.example.edukid_android.models.GenerateQuizRequest
import com.example.edukid_android.models.QuizResponse
import com.example.edukid_android.models.SubmitAnswersRequest
import com.example.edukid_android.models.ForgotPasswordRequest
import com.example.edukid_android.models.ResetPasswordRequest
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
   private const val BASE_URL = "https://tractile-trang-adaptively.ngrok-free.dev/"
   // private const val BASE_URL = "https://accessorial-zaida-soggily.ngrok-free.dev/"


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("ngrok-skip-browser-warning", "true")
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .callTimeout(120, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    /**
     * Sign up a new parent account
     * @param name Full name of the parent
     * @param email Email address
     * @param password Password
     * @return Result containing SignUpResponse on success or error message on failure
     */
    suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Result<SignUpResponse> {
        return try {
            val request = SignUpRequest(name = name, email = email, password = password)
            val response = apiService.signUp(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() 
                    ?: "Sign up failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Login with email and password
     * @param email Email address
     * @param password Password
     * @return Result containing SignUpResponse on success or error message on failure
     */
    suspend fun login(
        email: String,
        password: String
    ): Result<SignUpResponse> {
        return try {
            val request = LoginRequest(email = email, password = password)
            val response = apiService.login(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() 
                    ?: "Login failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Request password reset email
     * @param email Email address
     * @return Result containing success message or error message on failure
     */
    suspend fun forgotPassword(
        email: String
    ): Result<String> {
        return try {
            val request = ForgotPasswordRequest(email = email)
            val response = apiService.forgotPassword(request)
            
            if (response.isSuccessful) {
                Result.success("Password reset email sent successfully")
            } else {
                val errorMessage = response.errorBody()?.string() 
                    ?: "Failed to send reset email: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Reset password with token
     * @param token Reset token from email
     * @param newPassword New password
     * @return Result containing success message or error message on failure
     */
    suspend fun resetPassword(
        token: String,
        newPassword: String
    ): Result<String> {
        return try {
            val request = ResetPasswordRequest(
                token = token,
                newPassword = newPassword
            )
            val response = apiService.resetPassword(request)
            
            if (response.isSuccessful) {
                Result.success("Password reset successfully")
            } else {
                val errorMessage = response.errorBody()?.string() 
                    ?: "Failed to reset password: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Add a child to a parent's account
     * @param parentId The ID of the parent
     * @param name Child's name
     * @param age Child's age
     * @param level Child's starting level
     * @return Result containing ParentResponse on success or error message on failure
     */
    suspend fun addChild(
        parentId: String,
        name: String,
        age: Int,
//        level: String
        avatarEmoji : String?
    ): Result<ParentResponse> {
        return try {
            val request = AddChildRequest(name = name, age = age, level = "1" , avatarEmoji = avatarEmoji)
            val response = apiService.addChild(parentId, request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() 
                    ?: "Add child failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get QR code for a child
     * @param parentId The ID of the parent
     * @param kidId The ID of the child
     * @return Result containing QRCodeResponse on success or error message on failure
     */
    suspend fun getQRCode(
        parentId: String,
        kidId: String
    ): Result<QRCodeResponse> {
        return try {
            val response = apiService.getQRCode(parentId, kidId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() 
                    ?: "Get QR code failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get child by ID from QR scan
     * @param childId The ID of the child
     * @return Result containing ChildResponse on success or error message on failure
     */
    suspend fun getChildById(
        childId: String
    ): Result<ChildResponse> {
        return try {
            val response = apiService.getChildById(childId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() 
                    ?: "Get child failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateParent(
        id: String,
        name: String,
        email: String,
        password: String
    ): Result<ParentResponse> {
        return try {
            val request = UpdateParentRequest(name = name, email = email, password = password)
            val response = apiService.updateParent(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Update parent failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getParent(
        id: String
    ): Result<ParentResponse> {
        return try {
            val response = apiService.getParent(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Get parent failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteChild(
        parentId: String,
        kidId: String
    ): Result<String> {
        return try {
            val response = apiService.deleteChild(parentId, kidId)
            if (response.isSuccessful) {
                Result.success("Child deleted successfully")
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Delete child failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generate a quiz for a child using AI
     */
    suspend fun generateQuiz(
        parentId: String,
        kidId: String,
        subject: String,
        difficulty: String,
        nbrQuestions: Int,
        topic: String?
    ): Result<com.example.edukid_android.models.ChildResponse> {
        return try {
            val request = GenerateQuizRequest(
                subject = subject.lowercase(),
                difficulty = difficulty.lowercase(),
                nbrQuestions = nbrQuestions,
                topic = topic?.takeIf { it.isNotBlank() }
            )
            val response = apiService.generateQuiz(parentId, kidId, request)
            if (response.isSuccessful && response.body() != null) {
                val raw = response.body()!!.string()
                val gson = Gson()
                // Try parsing as full ChildResponse
                try {
                    val childResp = gson.fromJson(raw, ChildResponse::class.java)
                    if (childResp != null) {
                        return Result.success(childResp)
                    }
                } catch (_: Exception) { /* fall through */ }

                // Try parsing as single QuizResponse
                try {
                    val quizResp = gson.fromJson(raw, QuizResponse::class.java)
                    if (quizResp != null) {
                        val synthesizedChild = ChildResponse(
                            name = "",
                            age = 0,
                            level = "",
                            avatarEmoji = null,
                            quizzes = listOf(quizResp),
                            score = 0,
                            id = kidId
                        )
                        return Result.success(synthesizedChild)
                    }
                } catch (_: Exception) { /* fall through */ }

                Result.failure(Exception("Unexpected response format"))
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Generate quiz failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generate a quiz based on kid's needs (sends empty body)
     */
    suspend fun generateQuizBasedOnNeeds(
        parentId: String,
        kidId: String
    ): Result<com.example.edukid_android.models.ChildResponse> {
        return try {
            val response = apiService.generateQuizBasedOnNeeds(parentId, kidId)
            if (response.isSuccessful && response.body() != null) {
                val raw = response.body()!!.string()
                val gson = Gson()
                // Try parsing as full ChildResponse
                try {
                    val childResp = gson.fromJson(raw, ChildResponse::class.java)
                    if (childResp != null) {
                        return Result.success(childResp)
                    }
                } catch (_: Exception) { /* fall through */ }

                // Try parsing as single QuizResponse
                try {
                    val quizResp = gson.fromJson(raw, QuizResponse::class.java)
                    if (quizResp != null) {
                        val synthesizedChild = ChildResponse(
                            name = "",
                            age = 0,
                            level = "",
                            avatarEmoji = null,
                            quizzes = listOf(quizResp),
                            score = 0,
                            id = kidId
                        )
                        return Result.success(synthesizedChild)
                    }
                } catch (_: Exception) { /* fall through */ }

                Result.failure(Exception("Unexpected response format"))
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Generate quiz based on needs failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    suspend fun updateQuiz(
//        parentId: String,
//        kidId: String,
//        quizId: String,
//        answered: Int,
//        score: Int
//    ): Result<ChildResponse> {
//        return try {
//            val request = UpdateQuizRequest(answered = answered, score = score)
//            val response = apiService.updateQuiz(parentId, kidId, quizId, request)
//            if (response.isSuccessful && response.body() != null) {
//                Result.success(response.body()!!)
//            } else {
//                val errorMessage = response.errorBody()?.string() ?: "Update quiz failed: ${response.code()}"
//                Result.failure(Exception(errorMessage))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

    suspend fun submitQuizAnswers(
        parentId: String,
        kidId: String,
        quizId: String,
        answers: List<Int>
    ): Result<ChildResponse> {
        return try {
            android.util.Log.d("ApiClient", "submitQuizAnswers - parentId: $parentId, kidId: $kidId, quizId: $quizId, answers: $answers")
            val request = SubmitAnswersRequest(answers = answers)
            android.util.Log.d("ApiClient", "submitQuizAnswers - Request body: $request")
            val response = apiService.submitQuizAnswers(parentId, kidId, quizId, request)
            android.util.Log.d("ApiClient", "submitQuizAnswers - Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            if (response.isSuccessful && response.body() != null) {
                android.util.Log.d("ApiClient", "submitQuizAnswers - Success! Response body: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Submit answers failed: ${response.code()}"
                android.util.Log.e("ApiClient", "submitQuizAnswers - Failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("ApiClient", "submitQuizAnswers - Exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Get all gifts for a child
     */
    suspend fun getGifts(
        parentId: String,
        kidId: String
    ): Result<List<com.example.edukid_android.models.ShopItem>> {
        return try {
            val response = apiService.getGifts(parentId, kidId)
            if (response.isSuccessful && response.body() != null) {
                val gifts = response.body()!!.map { it.toGift() }
                Result.success(gifts)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Get gifts failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Create a gift for a child
     */
    suspend fun createGift(
        parentId: String,
        kidId: String,
        title: String,
        cost: Int
    ): Result<String> {
        return try {
            val request = com.example.edukid_android.models.CreateGiftRequest(title = title, cost = cost)
            val response = apiService.createGift(parentId, kidId, request)
            if (response.isSuccessful) {
                Result.success("Gift created successfully")
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Create gift failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete a gift for a child
     */
    suspend fun deleteGift(
        parentId: String,
        kidId: String,
        giftId: String
    ): Result<String> {
        return try {
            val response = apiService.deleteGift(parentId, kidId, giftId)
            if (response.isSuccessful) {
                Result.success("Gift deleted successfully")
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Delete gift failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Buy a gift for a child
     */
    suspend fun buyGift(
        parentId: String,
        kidId: String,
        giftId: String
    ): Result<ChildResponse> {
        return try {
            val response = apiService.buyGift(parentId, kidId, giftId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Buy gift failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    /**
     * Track progress for a quest (e.g. game completion)
     */
    suspend fun trackQuestProgress(
        parentId: String,
        kidId: String,
        questType: String,
        increment: Int = 1,
        points: Int = 0,
        isPerfect: Boolean = false
    ): Result<Unit> {
        return try {
            val request = mapOf(
                "questType" to questType,
                "progressIncrement" to increment,
                "pointsEarned" to points,
                "isPerfectScore" to isPerfect
            )
            val response = apiService.trackQuestProgress(parentId, kidId, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Track quest progress failed: ${response.code()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

