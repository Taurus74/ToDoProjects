package com.tausoft.todoprojects.exchange

import com.tausoft.todoprojects.data.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface ExchangeAPI {
    @GET("good_locations.php")
    fun getGoodShops(@Query("start") start: Long): Single<List<GoodShops>>

    @GET("good_tasks.php")
    fun getGoodTasks(@Query("start") start: Long): Single<List<GoodTasks>>

    @GET("good_users.php")
    fun getGoodUsers(@Query("start") start: Long): Single<List<GoodUsers>>

    @GET("goods.php")
    fun getGoods(@Query("start") start: Long): Single<List<Goods>>

    @GET("images.php")
    fun getImages(@Query("start") start: Long): Single<List<GoodImage>>

    @GET("shop_users.php")
    fun getShopUsers(@Query("start") start: Long): Single<List<ShopUsers>>

    @GET("shops.php")
    fun getShops(@Query("start") start: Long): Single<List<Shops>>

    @GET("task_users.php")
    fun getTaskUsers(@Query("start") start: Long): Single<List<TaskUsers>>

    @GET("tasks.php")
    fun getTasks(@Query("start") start: Long): Single<List<Task>>

    @GET("users.php")
    fun getUsers(@Query("start") start: Long): Single<List<Users>>

    @POST("tasks.php")
    @FormUrlEncoded
    fun postTasks(
        @Field("ts_id") ts_id: String,
        @Field("ts_name") ts_name : String,
        @Field("ts_type") ts_type: Int,
        @Field("ts_mark") ts_mark: Boolean,
        @Field("ts_grade") ts_grade: Int,
        @Field("ts_percent") ts_percent: Int,
        @Field("ts_parent") ts_parent : String,
        @Field("ts_date") ts_date : Int,
        @Field("ts_note") ts_note : String,
        @Field("ts_createdAt") ts_createdAt: Long,
        @Field("ts_modifiedAt") ts_modifiedAt: Long,
        @Field("ts_isDeleted") ts_isDeleted: Boolean,
    ): Completable

    @POST("users.php")
    @FormUrlEncoded
    fun postUsers(
        @Field("us_account") us_account: String,
        @Field("us_owner") us_owner: Boolean,
        @Field("us_createdAt") us_createdAt: Long,
        @Field("us_modifiedAt") us_modifiedAt: Long,
        @Field("us_isDeleted") us_isDeleted: Boolean
    ): Completable

    @POST("task_users.php")
    @FormUrlEncoded
    fun postTaskUsers(
        @Field("tu_task_id") tu_taskId: String,
        @Field("tu_user_id") tu_userId: String,
        @Field("tu_createdAt") tu_createdAt: Long,
        @Field("tu_modifiedAt") tu_modifiedAt: Long,
        @Field("tu_isDeleted") tu_isDeleted: Boolean
    ): Completable

}