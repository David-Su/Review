package suk.practice

import android.content.UriMatcher
import android.os.Bundle
import android.util.Log
import android.view.ViewConfiguration
import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*

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

    fun hashMap(){
        HashMap<Int,String>().put(1,"1")
    }

    fun invacation(): Sercvice {
        return Proxy.newProxyInstance(Sercvice::class.java.classLoader, arrayOf(Sercvice::class.java),
            InvocationHandler{ any: Any, method: Method, args: Array<Any> ->
                Log.d("Proxy",method.name)
                Log.d("Proxy",args.toString())
            }) as Sercvice
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
        Test().lengthOfLongestSubstring("abcabcbb")
        ViewConfiguration.get(this).scaledTouchSlop
    }
}