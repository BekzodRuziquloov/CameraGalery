package space.beka.cameragalery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import space.beka.cameragalery.catch.MySharePreference
import space.beka.cameragalery.databinding.ActivityMainBinding
import space.beka.cameragalery.objects.ImageCallBack
import space.beka.cameragalery.objects.MyLifecycleObserver
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime


class MainActivity : AppCompatActivity(), ImageCallBack {
    private lateinit var binding: ActivityMainBinding
    private lateinit var observer: MyLifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observer = MyLifecycleObserver(activityResultRegistry, this, this)
        lifecycle.addObserver(observer)

        binding.images.setOnClickListener {


            val popupMenu =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    PopupMenu(
                        this,
                        it,
                        Gravity.NO_GRAVITY,
                        R.style.popupBGStyle,
                        R.style.popupBGStyle1
                    )
                } else {
                    TODO("VERSION.SDK_INT < LOLLIPOP_MR1")
                }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(false)
            }
            popupMenu.inflate(R.menu.my_menu_rasimi_qosish)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true)
            }

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.kameradan -> {

                        cameraPermission()

                    }
                    R.id.galereyadan -> {

                        startActivityForResult(
                            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "image/*"
                            }, 1
                        )

                    }
                }
                true
            }

            popupMenu.show()


        }

    }


    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            binding.images.setImageURI(uri)

            MySharePreference.init(binding.root.context)

            val l = ArrayList<String>()
            l.addAll(MySharePreference.contactList)

            l.add(uri.toString())

            println(l)

            MySharePreference.contactList = l

            val inputStream = contentResolver?.openInputStream(uri)
            val localDateTime = LocalDateTime.now()
            val file = File(filesDir, "$localDateTime images.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()


        }
    }

    private fun cameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {

            requestPermissions()

        } else {

            observer.selectImage()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
    }

    override fun imageSelected(photoPath: String?) {
        binding.images.setImageURI(photoPath!!.toUri())

    }

}