package suk.practice

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData

/**
 * @author SuK
 * @time 2020/3/21 13:58
 * @des
 */
abstract class BaseActivity : AppCompatActivity() {

    protected val tag = javaClass.simpleName

    fun getTask(): Int {
        Log.d(tag,"taskid:"+taskId)
        return taskId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MutableLiveData<String>().observe()
    }
}