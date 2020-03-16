package suk.practice

import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn.setOnClickListener {
            startActivity(Intent(this,MainActivity2::class.java))
            Handler().post {
//                while (true) {
//
//                }

                Log.d("startActivity","Handler1")
            }
            Log.d("startActivity","执行后")
        }

    }
}
