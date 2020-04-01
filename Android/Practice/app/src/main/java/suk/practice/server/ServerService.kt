package suk.practice.server

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * @author SuK
 * @time 2020/4/1 16:40
 * @des
 */
class ServerService : Service() {
    override fun onBind(intent: Intent?): IBinder? = object : IServiceManager.Stub() {

        override fun addBook(book: Book) {
            Log.d("aidl", "addBook:$book")
        }

    }
}