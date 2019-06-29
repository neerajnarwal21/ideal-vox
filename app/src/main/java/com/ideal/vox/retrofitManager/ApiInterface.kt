package com.ideal.vox.retrofitManager

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
interface ApiInterface {

    @Multipart
    @POST("register")
    fun signupUser(@Part("name") name: RequestBody,
                   @Part("mobile_number") mobile: RequestBody,
                   @Part("email") email: RequestBody,
                   @Part("password") password: RequestBody,
                   @Part("c_password") confPassword: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("login")
    fun login(@Part("email") email: RequestBody,
              @Part("password") password: RequestBody)
            : Call<JsonObject>

    @POST("profile")
    fun getProfile(): Call<JsonObject>

    @Multipart
    @POST("social_login")
    fun socialLogin(@Part("name") name: RequestBody,
                    @Part("email") email: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("confirm_otp")
    fun confirmOTP(@Part("user_id") userId: RequestBody,
                   @Part("otp") otp: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("resend_otp")
    fun resendOTP(@Part("user_id") userId: RequestBody,
                  @Part("email") email: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("forgot_password")
    fun forgotPassword(@Part("mobile_number") mobile: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("reset_password")
    fun resetPassword(@Part("user_id") userId: RequestBody,
                      @Part("otp") otp: RequestBody,
                      @Part("password") password: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("send_otp_to_login_user")
    fun resendOTPToPhone(@Part("mobile_number") phone: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("update_phone_number")
    fun updatePhone(@Part("otp") otp: RequestBody,
                    @Part("mobile_number") mobile: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("update_avatar")
    fun updateDP(@Part fileDP: MultipartBody.Part?)
            : Call<JsonObject>

    @GET("get_expertises")
    fun getExpertise(): Call<JsonObject>

    @GET("get_cities")
    fun getCities(): Call<JsonObject>


    @Multipart
    @POST("become_photographer")
    fun becomePhotographer(@Part("expertise") expertise: RequestBody?,
                           @Part("experience_in_year") year: RequestBody,
                           @Part("experience_in_months") month: RequestBody,
                           @Part("dob") dob: RequestBody,
                           @Part("gender") gender: RequestBody,
                           @Part("address") password: RequestBody,
                           @Part("state") state: RequestBody,
                           @Part("city") city: RequestBody,
                           @Part("pin") pinCode: RequestBody,
                           @Part("lat") lat: RequestBody,
                           @Part("lng") lng: RequestBody,
                           @Part("otp") otp: RequestBody?,
                           @Part("about") about: RequestBody?,
                           @Part("user_type") userType: RequestBody? = null,
                           @Part("youtube_link") youtube: RequestBody? = null,
                           @Part("instagram_link") instagramLink: RequestBody? = null,
                           @Part("facebook_link") facebookLink: RequestBody? = null)
            : Call<JsonObject>

    @Multipart
    @POST("update_account_information")
    fun updateBankDetails(@Part("account_name") name: RequestBody,
                          @Part("account_number") accNo: RequestBody,
                          @Part("ifsc_code") ifsc: RequestBody,
                          @Part pic: MultipartBody.Part?)
            : Call<JsonObject>

    @Multipart
    @POST("update_price")
    fun updatePrice(@Part("day_price") day: RequestBody,
                    @Part("night_price") night: RequestBody,
                    @Part("full_day_price") fullDay: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("update_profile")
    fun updateProfile(@Part("name") name: RequestBody)
            : Call<JsonObject>

    @GET("get_accessories/{user_id}")
    fun allAccessories(@Path("user_id") userId: Int): Call<JsonObject>

    @GET("deactivate_user")
    fun deactivateAccount(): Call<JsonObject>

    @Multipart
    @POST("activate_user")
    fun reactivateAccount(@Part("email") email: RequestBody): Call<JsonObject>

    @GET("get_categories")
    fun allCategories(): Call<JsonObject>

    @Headers("Cache-Control: no-cache")
    @GET("get_user_categories/{user_id}")
    fun userCategories(@Path("user_id") userId: Int): Call<JsonObject>

    @Multipart
    @POST("add_user_category")
    fun addCategory(@Part("category") category: RequestBody,
                    @Part("price") price: RequestBody)
            : Call<JsonObject>

    @GET("delete_user_category/{id}")
    fun deleteCategory(@Path("id") accId: Int): Call<JsonObject>

    @Headers("Cache-Control: no-cache")
    @GET("get_views_count/{user_id}")
    fun viewsCount(@Path("user_id") userId: Int): Call<JsonObject>

    @Headers("Cache-Control: no-cache")
    @GET("update_views_count/{user_id}")
    fun updateViewsCount(@Path("user_id") userId: Int): Call<JsonObject>

    @Headers("Cache-Control: no-cache")
    @GET("get_reviews/{user_id}")
    fun reviewsList(@Path("user_id") userId: Int): Call<JsonObject>

    @Multipart
    @POST("add_call_log")
    fun addCalllog(@Part("user_id") userId: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("add_review")
    fun addReview(@Part("user_id") userId: RequestBody,
                  @Part("rating") rating: RequestBody,
                  @Part("reviews") review: RequestBody?)
            : Call<JsonObject>

    @GET("delete_accessories/{acc_id}")
    fun deleteAccessory(@Path("acc_id") accId: Int): Call<JsonObject>

    @GET("get_accessory_categories")
    fun getAccessoryCategories(): Call<JsonObject>

    @Multipart
    @POST("add_accessories")
    fun updateAccessories(@Part("name") name: RequestBody,
                          @Part("model") make: RequestBody,
                          @Part("category") category: RequestBody,
                          @Part pic: MultipartBody.Part?)
            : Call<JsonObject>


    @Multipart
    @POST("create_album")
    fun createAlbum(@Part("name") name: RequestBody)
            : Call<JsonObject>

    @GET("get_albums/{user_id} ")
    fun allAlbums(@Path("user_id") userId: Int): Call<JsonObject>

    @Multipart
    @POST("rename_album/{album_id} ")
    fun renameAlbum(@Path("album_id") albumId: Int,
                    @Part("name") name: RequestBody): Call<JsonObject>

    @GET("get_album_pics/{album_id} ")
    fun allAlbumPics(@Path("album_id") albumId: Int): Call<JsonObject>


    @Multipart
    @POST("add_album_picture")
    fun addPic(@Part("album_id") albumId: RequestBody,
               @Part pic: MultipartBody.Part?)
            : Call<JsonObject>

    @GET("delete_album_picture/{pic_id}")
    fun deletePicture(@Path("pic_id") picId: Int): Call<JsonObject>

    @GET("get_photographers_near/{lat}/{lng}/{distance}")
    fun mapPhotographers(@Path("lat") lat: Double,
                         @Path("lng") lng: Double,
                         @Path("distance") distance: Int): Call<JsonObject>

    @Multipart
    @POST("get_photographers_by_cities")
    fun citywisePhotographers(@Part("city") city: RequestBody): Call<JsonObject>

    @GET("get_photographers")
    fun allPhotographers(@Query("page") page: Int,
                         @Query("q") query: String?,
                         @Query("expertise") exp: String?,
                         @Query("min_price") minPrice: Int?,
                         @Query("max_price") maxPrice: Int?): Call<JsonObject>

    @GET("get_slider_pictures")
    fun headerPics(@Query("device_token") token: String?): Call<JsonObject>

    @Multipart
    @POST("get_schedules/{user_id}")
    fun getSchedule(@Path("user_id") userId: Int,
                    @Part("from_date") fromDate: RequestBody,
                    @Part("to_date") toDate: RequestBody): Call<JsonObject>

    @Multipart
    @POST("update_schedules")
    fun updateSchedule(@Part("user_id") userId: RequestBody,
                       @Part("schedule") schedule: RequestBody): Call<JsonObject>
}