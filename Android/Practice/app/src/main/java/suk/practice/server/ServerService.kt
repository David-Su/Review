package suk.practice.server

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.*
import kotlin.concurrent.timer

/**
 * @author SuK
 * @time 2020/4/1 16:40
 * @des
 */
class ServerService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        val binder = object : IServiceManager.Stub() {

            val books = arrayListOf<Book>()

            override fun addBook(book: Book) {
                Log.d("aidl", "addBook:$book")
                books.add(book)

            }

            override fun addOutBook(book: Book?) {
            }

            override fun registerScanner(scanner: IBookScanner) {
                timer(period = 1000L) {
                    scanner.onScanBook(books)
                }
            }


        }
        Log.d("服务端",binder.hashCode().toString())
        return binder;
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)

    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)

    }
}