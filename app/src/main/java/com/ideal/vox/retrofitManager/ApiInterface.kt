package com.ideal.vox.retrofitManager

import com.google.gson.JsonArray
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
    @POST("Profile/Update_User")
    fun updateUser(@Part("Name") name: RequestBody,
                   @Part("Father_name") fName: RequestBody,
                   @Part("Dob") dob: RequestBody,
                   @Part("Sex") gender: RequestBody,
                   @Part("Mobileno") mobile: RequestBody,
                   @Part("Emailid") email: RequestBody,
                   @Part("Password") password: RequestBody,
                   @Part("Address") address: RequestBody,
                   @Part("City") city: RequestBody,
                   @Part("State") state: RequestBody)
            : Call<JsonArray>

    @GET("get_accessories/{user_id} ")
    fun allAccessories(@Path("user_id") userId: Int): Call<JsonObject>

    @Multipart
    @POST("add_accessories")
    fun updateAccessories(@Part("name") name: RequestBody,
                          @Part("model") make: RequestBody,
                          @Part pic: MultipartBody.Part?)
            : Call<JsonObject>

    @Multipart
    @POST("update_profile")
    fun updateProfile(@Part("name") name: RequestBody)
            : Call<JsonObject>


    @Multipart
    @POST("customer-mobile-register")
    fun submitPhoneNumber(@Part("mobile_number") mobile: RequestBody)
            : Call<JsonObject>


    @Multipart
    @POST("customer-registration-loan")
    fun formStep4(@Part("customer_loans_id") loanId: RequestBody?,
                  @Part("params") loandata: RequestBody,
                  @Part sign: MultipartBody.Part?): Call<JsonObject>

    @GET
    fun searchIfsc(@Url url: String): Call<JsonObject>
}
