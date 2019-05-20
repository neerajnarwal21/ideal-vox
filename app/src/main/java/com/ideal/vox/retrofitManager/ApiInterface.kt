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
                  @Part("mobile_number") mobile: RequestBody)
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
    @POST("update_avatar")
    fun updateDP(@Part fileDP: MultipartBody.Part?)
            : Call<JsonObject>

    @GET("get_expertises")
    fun getExpertise(): Call<JsonObject>

    @Multipart
    @POST("become_photographer")
    fun becomePhotographer(@Part("expertise") expertise: RequestBody,
                           @Part("experience_in_year") year: RequestBody,
                           @Part("experience_in_months") month: RequestBody,
                           @Part("dob") dob: RequestBody,
                           @Part("gender") gender: RequestBody,
                           @Part("address") password: RequestBody,
                           @Part("pin") pinCode: RequestBody,
                           @Part("lat") lat: RequestBody,
                           @Part("lng") lng: RequestBody)
            : Call<JsonObject>

    @Multipart
    @POST("update_account_information")
    fun updateBankDetails(@Part("account_name") name: RequestBody,
                          @Part("account_number") accNo: RequestBody,
                          @Part("ifsc_code") ifsc: RequestBody,
                          @Part pic: MultipartBody.Part?)
            : Call<JsonObject>

    @Multipart
    @POST("update_profile")
    fun updateProfile(@Part("name") name: RequestBody)
            : Call<JsonObject>


    @GET("get_accessories/{user_id} ")
    fun allAccessories(@Path("user_id") userId: Int): Call<JsonObject>

    @GET("delete_accessories/{acc_id} ")
    fun deleteAccessory(@Path("acc_id") accId: Int): Call<JsonObject>

    @Multipart
    @POST("add_accessories")
    fun updateAccessories(@Part("name") name: RequestBody,
                          @Part("model") make: RequestBody,
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

    @GET("get_photographers")
    fun allPhotographers(): Call<JsonObject>

}
