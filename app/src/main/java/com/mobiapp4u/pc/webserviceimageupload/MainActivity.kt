package com.mobiapp4u.pc.webserviceimageupload

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.Toast
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {
    val IMAGE_REQ = 111
    var bitmap:Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_choose_image.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent,IMAGE_REQ)

        }
        btn_upload_image.setOnClickListener {
            val image = imageToString()
            val title = image_title.text.toString()

            val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiInterface.uploadImageToserver(title,image)
            call.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                     val imageClass = it
                        Toast.makeText(this,"Serever Response:"+imageClass.response ,Toast.LENGTH_SHORT).show()
                        imageV_selected.visibility = View.GONE
                        image_title.visibility = View.GONE
                        btn_choose_image.isEnabled = true
                        btn_upload_image.isEnabled = false
                    }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_REQ && resultCode == Activity.RESULT_OK && data!= null){
            val uri = data.data
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            imageV_selected.setImageBitmap(bitmap)
            imageV_selected.visibility = View.VISIBLE
            image_title.visibility = View.VISIBLE
            btn_choose_image.isEnabled = false
            btn_upload_image.isEnabled = true

        }
    }
    private fun imageToString():String{
        val byteArrayOutputStream = ByteArrayOutputStream()
       bitmap!!.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream)
       val imageByte:ByteArray = byteArrayOutputStream.toByteArray()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getEncoder().encodeToString(imageByte)
        } else {
            android.util.Base64.encodeToString(imageByte,android.util.Base64.DEFAULT)
        }


    }
}
