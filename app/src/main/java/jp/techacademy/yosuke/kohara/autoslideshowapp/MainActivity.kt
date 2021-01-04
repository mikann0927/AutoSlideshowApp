package jp.techacademy.yosuke.kohara.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.widget.Button
import kotlinx.android.synthetic.*
import android.database.Cursor as Cursor1

open class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    var cursor = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        var resolver = contentResolver
        var cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )
        if (cursor!!.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =

                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)
        } else {
            next_button.setEnabled(false)
            back_button.setEnabled(false)
            start_button.setEnabled(false)
        }
        next_button.setOnClickListener {
            if (cursor.moveToNext()) {
            } else {
                cursor.moveToFirst()
            }
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)
        }

        back_button.setOnClickListener {
            if (cursor.moveToPrevious()) {
            } else {
                cursor.moveToLast()
            }
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)
        }

        var mTimer: Timer? = null

        // タイマー用の時間のための変数
        var mTimerSec = 0.0

        var mHandler = Handler()

            start_button.setOnClickListener {
                if (mTimer == null) {
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mTimerSec += 2.0
                            mHandler.post {
                                if (cursor.moveToNext()) {
                                } else {
                                    cursor.moveToFirst()
                                }
                                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                val id = cursor.getLong(fieldIndex)
                                val imageUri =
                                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                imageView.setImageURI(imageUri)
                            }
                        }
                    }, 2000, 2000) // 最初に始動させるまで2000ミリ秒、ループの間隔を2000ミリ秒 に設定

                    start_button.text = ("停止").toString()
                    next_button.setEnabled(false)
                    back_button.setEnabled(false)
                } else  {
                    mTimer!!.cancel()
                    start_button.text = ("再生").toString()
                    next_button.setEnabled(true)
                    back_button.setEnabled(true)
                    mTimer = null
                    mTimerSec = 0.0
                }
            }
            fun onDestroy() {
                super.onDestroy()
                cursor.close()
            }
        }
    }
