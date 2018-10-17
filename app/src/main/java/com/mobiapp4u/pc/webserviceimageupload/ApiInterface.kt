package com.mobiapp4u.pc.webserviceimageupload

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {
    @FormUrlEncoded
    @POST("upload.php")
    fun uploadImageToserver(@Field("title") title:String, @Field("image") image:String):Observable<ImageClass>
}