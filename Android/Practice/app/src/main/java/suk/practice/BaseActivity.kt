package suk.practice

import android.animation.*
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.animation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData

/**
 * @author SuK
 * @time 2020/3/21 13:58
 * @des
 */
abstract class BaseActivity : AppCompatActivity() {

    protected val tag = javaClass.simpleName

    fun getTask(): Int {
        Log.d(tag, "taskid:" + taskId)
        return taskId
    }

    fun animatior() {

    }

    fun threadPoolExecutor(){
        //        var i = 0
//        val executor = ThreadPoolExecutor(
//            2, 100,
//            0L, TimeUnit.MILLISECONDS,
//            LinkedBlockingQueue<Runnable>(),
//            ThreadFactory {
//                i++
//                Log.d(tag,"创建第${i}条线程" )
//                Thread(it).apply { name = "第${i}条线程" }
//            }
//        )

//        val executor = Executors.newScheduledThreadPool(2,ThreadFactory {
//            i++
//            Log.d(tag,"创建第${i}条线程" )
//            Thread(it).apply { name = "第${i}条线程" }
//        })

//        val executor = Executors.newCachedThreadPool(ThreadFactory {
//            synchronized(this) {
//                i++
//                Log.d(tag, "创建第${i}条线程")
//            }
//
//            Thread(it).apply { name = "第${i}条线程" }
//        })
//
//        for (i in 0 until 10) {
//            executor.execute {
//                while (true) {
////                    Log.d(tag,Thread.currentThread().name)
//                }
//            }
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        animatior()
    }
}