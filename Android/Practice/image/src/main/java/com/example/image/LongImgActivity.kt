package com.example.image

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

/**
 * @Author SuK
 * @Des 长图显示
 * @Date 2023/9/11
 */
class LongImgActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_long_img)
        val longImgView = findViewById<LongImgView>(R.id.liv)
        val inputStream = assets.open("long_pic.jpg")
        longImgView.setInputStream(inputStream)
    }
}