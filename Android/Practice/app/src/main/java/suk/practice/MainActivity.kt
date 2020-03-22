package suk.practice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getTask()

        var i = 0
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

        btn.setOnClickListener {
            val executor = Executors.newCachedThreadPool(ThreadFactory {
                synchronized(this) {
                    i++
                    Log.d(tag, "创建第${i}条线程")
                }

                Thread(it).apply { name = "第${i}条线程" }
            })

            for (i in 0 until 10) {
                executor.execute {
//                    while (true) {
////                    Log.d(tag,Thread.currentThread().name)
//                    }
                }
            }
        }

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
}
