package com.example.snapkit.camera

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.snapkit.database.MediaFile
import com.example.snapkit.database.getDatabase
import com.example.snapkit.generateImageFile
import com.example.snapkit.getDCIMDirectory
import com.example.snapkit.getImageFromMediaStore
import com.example.snapkit.scanForMediaFiles
import com.otaliastudios.cameraview.PictureResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    // If the user has clicked on the capture image button.
    private val _captureImage = MutableLiveData<Boolean>()
    val captureImageState: LiveData<Boolean>
        get() = _captureImage

    // If the file is in the process of being written to the image directory.
    private val _savingFile = MutableLiveData<Boolean>()
    val savingFile: LiveData<Boolean>
        get() = _savingFile

    // If the user wants to navigate to the Image Gallery fragment.
    private val _navigateToGallery = MutableLiveData<Boolean>()
    val navigateToGallery: LiveData<Boolean>
        get() = _navigateToGallery

    /**
     * Set _captureImage to true when capture button is clicked.
     */
    fun onCaptureButtonClicked() {
        _captureImage.value = true
    }

    /**
     * Set _captureImage to false when capture button event is finished.
     */
    fun onCaptureButtonFinished() {
        _captureImage.value = false
    }

    /**
     * Set _navigateToGallery to true when capture button is clicked.
     */
    fun onGalleryButtonClicked() {
        _navigateToGallery.value = true
    }

    /**
     * Set _navigateToGallery to false when navigating to the Gallery fragment is done.
     */
    fun onGalleryButtonFinished() {
        _navigateToGallery.value = false
    }

    /**
     * Write the image file to the image directory and set _savingFile to true.
     */
    fun storeFile(imageResult: PictureResult) {
        _savingFile.value = true

        // Write image to the DCIM directory.
        try {
            var imageDirectory = getDCIMDirectory()
            var imageFile = generateImageFile(imageDirectory!!)
            imageResult.let {
                it.toFile(imageFile) {
                    //TODO: Store in DB here
                    scanForMediaFiles(getApplication(), arrayOf(imageFile.path), ::insertFileToCache)
                    storeFileComplete()
                }
            }
        } catch (e: Exception) {
            Log.e("CameraViewModel", e.message)
        }
    }

    private fun insertFileToCache(context: Context, filePath: String) {
        var imageFile = getImageFromMediaStore(context, filePath)
        var db = getDatabase(context).mediaFileDao()
        CoroutineScope(Job()).launch {
            imageFile?.let {
                db.insertMediaFile(MediaFile(it.filePath, it.dateCreated, it.timeCreated, it.dateTakenLong))
            }
        }
    }

    /**
     * Set _inImagePreviewState to true when the image file is done saving to the Media Storage.
     */
    private fun storeFileComplete() {
        _savingFile.value = false
    }
}