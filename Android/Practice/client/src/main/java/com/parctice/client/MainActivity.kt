package com.parctice.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import suk.practice.server.Book
import suk.practice.server.IBookScanner
import suk.practice.server.IServiceManager
import java.net.Socket

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun bind(view: View) {
Socket("",123)
        bindService(
            Intent().setAction("server.ServerService").setPackage("suk.practice"),
            object : ServiceConnection {
                override fun onServiceDisconnected(p0: ComponentName?) {
                }

                override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                    val serviceManager = IServiceManager.Stub.asInterface(p1)
                    serviceManager.addBook(Book("金瓶梅", 99))
                    serviceManager.registerScanner(object : IBookScanner.Stub() {
                        override fun onScanBook(books: MutableList<Book>) {
                            Log.d("aidl", books.toString())
                        }

                    })
                }

            },
            BIND_AUTO_CREATE
        )
    }
}