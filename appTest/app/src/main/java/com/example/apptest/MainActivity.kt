package com.example.apptest

import android.Manifest
import android.R.attr.text
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {
    //private val multiplePermissions = android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
    private lateinit var textForFileContent: TextView
    private lateinit var buttonForOpen: Button
    private lateinit var buttonForCreate: Button
    private val pathD : String = ""


    private companion object{
        //PERMISSION request constant, assign any value
        private const val STORAGE_PERMISSION_CODE = 100
        private const val TAG = "PERMISSION_TAG"

        private const val CHOOSE_FILE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textForFileContent = findViewById(R.id.textForFileContent)
        buttonForOpen = findViewById(R.id.buttonForOpen)
        buttonForCreate = findViewById(R.id.buttonForCreate)
        //обработчик нажатия кнопки buttonForCreate
        buttonForCreate.setOnClickListener {
            if (checkPermission()){
                createFile()
            }
            else{
                requestPermission()
            }
        }
        //обработчик нажатия кнопки buttonForOpen
        buttonForOpen.setOnClickListener{
            if (checkPermission()){
                openFile()
            } else{
                requestPermission()
            }

        }
    }


    private fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            try {
                Log.d(TAG, "requestPermission: try")
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)
            }
            catch (e: Exception){
                Log.e(TAG, "requestPermission: ", e)
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        }
        else{
            //Android is below 11(R)
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun checkPermission(): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            Environment.isExternalStorageManager()
        }
        else{
            //Android is below 11(R)
            val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun createFile(){
        //имя каталога
        val folderName = "user_restricted_folder"
        //название файла
        val nameFilen = "/default_file.txt"
        val file = File("${Environment.getExternalStorageDirectory()}/$folderName" +nameFilen)

        //создание файла в каталоге
        val fileCreated = file.createNewFile()

        if (fileCreated) {
            toast("Файл создан: ${file.absolutePath}")
            // запись в файл
            FileOutputStream(file).use { fileOut ->
                val text = "Содержимое файла ${file.absolutePath}"
                // перевод строки в байты
                val buffer = text.toByteArray()
                fileOut.write(buffer, 0, buffer.size)
                toast("Успешная запись в файл: ${file.absolutePath}")
            }
        } else {
            toast("Файл не удалось создать, или он уже существует")
        }
    }

    private fun openFile(){
        //корень хранилища данных
       // val externalStorage : File = Environment.getExternalStorageDirectory()
        //для записи дальнейшего пути
        val folderName = "user_restricted_folder"


        val nameFilen = "/default_file.txt"
        val file = File("${Environment.getExternalStorageDirectory()}/$folderName" +nameFilen)
        //для чтения байтов из файла

        try {
            FileInputStream(file).use { fileInp ->
            textForFileContent.text = fileInp.readBytes().toString(Charsets.UTF_8)
            }
        } catch (e : FileNotFoundException) {
            //println(e.message)
            textForFileContent.text = "Ошибка: " + e.message//Разрешение не было предоставлено!";
        }
        // вывод содержимого
       // textForFileContent.text = fileInp.readBytes().toString(Charsets.UTF_8)
    }
    private val storageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        Log.d(TAG, "storageActivityResultLauncher: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            if (Environment.isExternalStorageManager()){
                openFile()
            }
            else{
                toast("Разрешение к общему хранилищу не было получено")
            }
        }
        else{
            //Android is below 11(R)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty()){
                //check each permission if granted or not
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (write && read){
                    openFile()
                }
                else{
                    toast("Разрешение к общему хранилищу не было получено")
                }
            }
        }
    }

    private fun toast(message: kotlin.String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}