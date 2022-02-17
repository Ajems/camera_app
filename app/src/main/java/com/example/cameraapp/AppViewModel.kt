package com.example.cameraapp

import androidx.lifecycle.ViewModel

class AppViewModel: ViewModel() {

    var cntPhotos = 0
    var readyForPhoto = false
    val FILE_NAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    var gridVisible = true


    fun addCntPhoto(): Int{
        cntPhotos ++
        return cntPhotos
    }
}
