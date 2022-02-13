package com.example.cameraapp

import androidx.lifecycle.ViewModel

class AppViewModel: ViewModel() {

    var cntPhotos = 0
    var readyForPhoto = false
    val FILE_NAME_FORMAT = "yy-MM-dd-HH-mm-ss-SSS"
    var gridVisible = true

    companion object{
        private const val CAMERA_PERMISSON_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
    }

    fun addCntPhoto(): Int{
        cntPhotos ++
        return cntPhotos
    }
}