package com.example.cameraapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.cameraapp.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var imageCapture: ImageCapture?=null
    private lateinit var outputDirectory: File

    private val appViewModel: AppViewModel by lazy{
        ViewModelProviders.of(this).get(AppViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        outputDirectory = getOutputDirectory()
        binding.cntPhotosView.text = appViewModel.cntPhotos.toString()


        //ask permission
        val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Toast.makeText(applicationContext, "Done", Toast.LENGTH_LONG).show()
                appViewModel.readyForPhoto= true
                startCamera()
            } else {
                Toast.makeText(applicationContext, "Need camera permission", Toast.LENGTH_LONG).show()
            }
        }

        //make photo
        binding.photoBtn.setOnClickListener{
            if (appViewModel.readyForPhoto){
                binding.cntPhotosView.text = appViewModel.addCntPhoto().toString()
                savePhoto()

            } else {
                requestCameraPermission.launch(android.Manifest.permission.CAMERA)
            }
        }

        //hide grid
        binding.gridBtn.setOnClickListener{
            if (appViewModel.gridVisible){
                //hide
                binding.cameraGrid.alpha = 0.0F
            } else {
                //show
                binding.cameraGrid.alpha = 1.0F
            }
            appViewModel.gridVisible = !appViewModel.gridVisible
        }

        //заполнение activity
        if (appViewModel.readyForPhoto){
            startCamera()
        }

        //get permission
        if (!appViewModel.readyForPhoto) requestCameraPermission.launch(android.Manifest.permission.CAMERA)

    }

    private fun getOutputDirectory(): File{
        val mediaDir = externalMediaDirs.firstOrNull()?.let{mFile ->
            File(mFile, resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }

        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun startCamera(){

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val cameraProvider:ProcessCameraProvider = cameraProviderFuture.get()

        cameraProviderFuture.addListener({
            val preview = Preview.Builder()
                .build()
                .also {
                    mPreview->mPreview.setSurfaceProvider(binding.camera.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            }catch (e:Exception){
                Toast.makeText(applicationContext, "Error open camera", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))

    }

    fun savePhoto(){
        val imageCapture = imageCapture?:return
        val file = File(
            outputDirectory,
            "camera_app-"+SimpleDateFormat(appViewModel.FILE_NAME_FORMAT,
                Locale.getDefault()).format(System.currentTimeMillis())+".jpg")

        val outputOption = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOption, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(file)

                    Toast.makeText(
                        this@MainActivity,
                        "Photo saved to $savedUri",
                        Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        applicationContext,
                        "Error save photo",
                        Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}