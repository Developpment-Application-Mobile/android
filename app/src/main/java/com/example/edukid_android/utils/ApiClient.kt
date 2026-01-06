package com.example.edukid_android.utils

import com.example.edukid_android.models.AddChildRequest
import com.example.edukid_android.models.UpdateChildRequest
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
   // private const val BASE_URL = "https://tractile-trang-adaptively.ngrok-free.dev/"
    // private const val BASE_URL = "https://accessorial-zaida-soggily.ngrok-free.dev/"
    private const val BASE_URL = "https://preterrestrial-georgann-recappable.ngrok-free.dev/"


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
     * Update an existing child's information
     * @param parentId The ID of the parent
     * @param kidId The ID of the child to update
     * @param name Child's updated name
     * @param age Child's updated age
     * @param avatarEmoji Child's updated avatar
     * @return Result containing ParentResponse on success or error message on failure
     */
    suspend fun updateChild(
        parentId: String,
        kidId: String,
        name: String,
        age: Int,
        avatarEmoji: String?
    ): Result<ParentResponse> {
        return try {
            val request = UpdateChildRequest(name = name, age = age, avatarEmoji = avatarEmoji)
            val response = apiService.updateChild(parentId, kidId, request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() 
                    ?: "Update child failed: ${response.code()}"
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
    ): Result<ChildResponse> {
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
    ): Result<ChildResponse> {
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

    // Get child review with AI-generated analysis
    suspend fun getChildReview(
        parentId: String,
        kidId: String
    ): Result<com.example.edukid_android.models.ChildReviewResponse> {
        return try {
            android.util.Log.d("ApiClient", "getChildReview - parentId: $parentId, kidId: $kidId")
            val response = apiService.getChildReview(parentId, kidId, emptyMap())
            android.util.Log.d("ApiClient", "getChildReview - Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                android.util.Log.d("ApiClient", "getChildReview - Success! Response body received")
                android.util.Log.d("ApiClient", "getChildReview - pdfBase64 present: ${body.pdfBase64 != null}, length: ${body.pdfBase64?.length ?: 0}")
                Result.success(body)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to fetch child review: ${response.code()}"
                android.util.Log.e("ApiClient", "getChildReview - Failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("ApiClient", "getChildReview - Exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Generate a puzzle for a child
     * @param parentId The ID of the parent
     * @param kidId The ID of the child
     * @param type Puzzle type (image, word, number, sequence, pattern) - optional
     * @param difficulty Difficulty level (easy, medium, hard) - optional
     * @param gridSize Grid size for the puzzle - optional
     * @param topic Topic for the puzzle - optional
     * @return Result containing ChildResponse on success or error message on failure
     */
    suspend fun generatePuzzle(
        parentId: String,
        kidId: String?,
        type: String? = null,
        difficulty: String? = null,
        gridSize: Int? = null,
        topic: String? = null
    ): Result<ChildResponse> {
        return try {
            // Validate required parameters
            if (kidId.isNullOrBlank()) {
                return Result.failure(Exception("Kid ID is required"))
            }
            
            android.util.Log.d("ApiClient", "generatePuzzle - parentId: $parentId, kidId: $kidId, type: $type, difficulty: $difficulty, gridSize: $gridSize, topic: $topic")
            val request = com.example.edukid_android.models.GeneratePuzzleRequest(
                type = type?.lowercase(),
                difficulty = difficulty?.lowercase(),
                gridSize = gridSize,
                topic = topic?.takeIf { it.isNotBlank() }
            )
            android.util.Log.d("ApiClient", "generatePuzzle - Request body: $request")
            val response = apiService.generatePuzzle(parentId, kidId, request)
            android.util.Log.d("ApiClient", "generatePuzzle - Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            
            if (response.isSuccessful && response.body() != null) {
                val raw = response.body()!!.string()
                android.util.Log.d("ApiClient", "generatePuzzle - Raw response: $raw")
                val gson = Gson()
                
                // Try parsing as full ChildResponse
                try {
                    val childResp = gson.fromJson(raw, ChildResponse::class.java)
                    if (childResp != null) {
                        android.util.Log.d("ApiClient", "generatePuzzle - Success! Parsed as ChildResponse")
                        return Result.success(childResp)
                    }
                } catch (e: Exception) {
                    android.util.Log.d("ApiClient", "generatePuzzle - Failed to parse as ChildResponse: ${e.message}")
                }

                // Try parsing as single PuzzleResponse
                try {
                    val puzzleResp = gson.fromJson(raw, com.example.edukid_android.models.PuzzleResponse::class.java)
                    if (puzzleResp != null) {
                        android.util.Log.d("ApiClient", "generatePuzzle - Parsed as single PuzzleResponse, synthesizing ChildResponse")
                        val synthesizedChild = ChildResponse(
                            name = "",
                            age = 0,
                            level = "",
                            avatarEmoji = null,
                            quizzes = emptyList(),
                            score = 0,
                            id = kidId
                        )
                        return Result.success(synthesizedChild)
                    }
                } catch (e: Exception) {
                    android.util.Log.d("ApiClient", "generatePuzzle - Failed to parse as PuzzleResponse: ${e.message}")
                }

                Result.failure(Exception("Unexpected response format"))
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Generate puzzle failed: ${response.code()}"
                android.util.Log.e("ApiClient", "generatePuzzle - Failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("ApiClient", "generatePuzzle - Exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Submit puzzle solution
     * @param parentId The ID of the parent
     * @param kidId The ID of the child
     * @param puzzleId The ID of the puzzle
     * @param positions Array of piece positions (indices)
     * @param timeSpent Time spent in seconds
     * @return Result containing ChildResponse on success or error message on failure
     */
    suspend fun submitPuzzle(
        parentId: String,
        kidId: String,
        puzzleId: String,
        positions: List<Int>,
        timeSpent: Int
    ): Result<ChildResponse> {
        return try {
            android.util.Log.d("ApiClient", "submitPuzzle - parentId: $parentId, kidId: $kidId, puzzleId: $puzzleId, positions: $positions, timeSpent: $timeSpent")
            val request = com.example.edukid_android.models.SubmitPuzzleRequest(
                positions = positions,
                timeSpent = timeSpent
            )
            android.util.Log.d("ApiClient", "submitPuzzle - Request body: $request")
            val response = apiService.submitPuzzle(parentId, kidId, puzzleId, request)
            android.util.Log.d("ApiClient", "submitPuzzle - Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            
            if (response.isSuccessful && response.body() != null) {
                android.util.Log.d("ApiClient", "submitPuzzle - Success! Response body: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Submit puzzle failed: ${response.code()}"
                android.util.Log.e("ApiClient", "submitPuzzle - Failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("ApiClient", "submitPuzzle - Exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a quiz
     * @param parentId The ID of the parent
     * @param kidId The ID of the child
     * @param quizId The ID of the quiz to delete
     * @return Result containing Unit on success or error message on failure
     */
    suspend fun deleteQuiz(
        parentId: String,
        kidId: String,
        quizId: String
    ): Result<Unit> {
        return try {
            android.util.Log.d("ApiClient", "deleteQuiz - parentId: $parentId, kidId: $kidId, quizId: $quizId")
            val response = apiService.deleteQuiz(parentId, kidId, quizId)
            android.util.Log.d("ApiClient", "deleteQuiz - Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                android.util.Log.d("ApiClient", "deleteQuiz - Success!")
                Result.success(Unit)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Delete quiz failed: ${response.code()}"
                android.util.Log.e("ApiClient", "deleteQuiz - Failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("ApiClient", "deleteQuiz - Exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a puzzle
     * @param parentId The ID of the parent
     * @param kidId The ID of the child
     * @param puzzleId The ID of the puzzle to delete
     * @return Result containing Unit on success or error message on failure
     */
    suspend fun deletePuzzle(
        parentId: String,
        kidId: String,
        puzzleId: String
    ): Result<Unit> {
        return try {
            android.util.Log.d("ApiClient", "deletePuzzle - parentId: $parentId, kidId: $kidId, puzzleId: $puzzleId")
            val response = apiService.deletePuzzle(parentId, kidId, puzzleId)
            android.util.Log.d("ApiClient", "deletePuzzle - Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                android.util.Log.d("ApiClient", "deletePuzzle - Success!")
                Result.success(Unit)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Delete puzzle failed: ${response.code()}"
                android.util.Log.e("ApiClient", "deletePuzzle - Failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("ApiClient", "deletePuzzle - Exception: ${e.message}", e)
            Result.failure(e)
        }
    }
}

