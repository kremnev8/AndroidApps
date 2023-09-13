package com.izonesie.simplenotes

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Pair
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.izonesie.simplenotes.util.IMediaRequestCallback
import com.izonesie.simplenotes.util.TakePictureWithUriReturnContract
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object PhotoProvider : DialogInterface.OnClickListener {

    private const val FILES_REQUEST_CODE = 1
    private const val CAMERA_REQUEST_CODE = 2

    private val IMAGE_PERMISSION by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            return@lazy Manifest.permission.READ_MEDIA_IMAGES
        else
            return@lazy Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private lateinit var mainActivity: AppCompatActivity

    private var lastRequester: IMediaRequestCallback? = null

    private lateinit var mediaDialog: AlertDialog
    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var takePicture: ActivityResultLauncher<Uri>


    fun init(activity: AppCompatActivity) {
        mainActivity = activity

        getContent = activity.registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (lastRequester != null && uri != null) {
                val newUri = getTmpFileUri()
                uri.copyTo(newUri)
                lastRequester!!.onMediaSelected(newUri.toString())
                lastRequester = null
            }
        }

        takePicture = activity.registerForActivityResult(
            TakePictureWithUriReturnContract()
        ) { result: Pair<Boolean, Uri> ->
            if (!result.first) {
                File(result.second.toString()).delete()
                return@registerForActivityResult
            }
            if (lastRequester != null) {
                lastRequester!!.onMediaSelected(result.second.toString())
                lastRequester = null
            }
        }

        mediaDialog = createMediaDialog();
    }

    private fun Uri.copyTo(file: Uri) {
        mainActivity.contentResolver.openInputStream(this).use { input ->
            mainActivity.contentResolver.openOutputStream(file).use { output ->
                input!!.copyTo(output!!)
            }
        }
    }

    private fun createMediaDialog(): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(mainActivity)
        builder.setTitle(R.string.add_media_label)
            .setItems(R.array.add_photo_dialog_options, this)
        return builder.create()
    }

    fun requestMedia(callback: IMediaRequestCallback) {
        lastRequester = callback
        mediaDialog.show()
    }

    override fun onClick(dialog: DialogInterface, index: Int) {
        if (index == 0)
            tryTakePhoto()
        else if (index == 1)
            tryPickPhoto()

        dialog.dismiss()
    }

    private fun isGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(mainActivity, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun tryPickPhoto() {
        if (isGranted(IMAGE_PERMISSION)) {
            getContent.launch("image/*")
        } else {
            requestPhotoPermission()
        }
    }

    private fun tryTakePhoto() {
        if (isGranted(Manifest.permission.CAMERA)) {
            tryTakePicture()
        } else {
            requestCameraPermission()
        }
    }

    private fun tryTakePicture() {
        try {
            takePicture.launch(getTmpFileUri())
        } catch (e: Exception) {
            Log.e("ERROR", e.toString())
        }
    }

    private fun requestPhotoPermission() {
        ActivityCompat.requestPermissions(
            mainActivity,
            arrayOf(IMAGE_PERMISSION),
            FILES_REQUEST_CODE
        )
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            mainActivity,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == FILES_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && lastRequester != null) {
            getContent.launch("image/*")
        } else if (requestCode == CAMERA_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && lastRequester != null) {
            tryTakePicture()
        }
    }

    @Throws(IOException::class)
    private fun getTmpFileUri(): Uri {
        val path = File(mainActivity.filesDir, "camera")
        path.mkdirs()

        val tmpFile = File.createTempFile("photo", ".jpg", path)
        tmpFile.createNewFile()

        return FileProvider.getUriForFile(
            mainActivity,
            BuildConfig.APPLICATION_ID + ".provider",
            tmpFile
        )
    }
}