package org.davincicodeos.updater

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import java.io.FileDescriptor

data class SelectFileParams(
    val fileMimeType: String
)

interface FileSelectionEntryPoint {
    val fileSelectionOwner: Fragment

    fun onFileSelected(fileDescriptor: FileDescriptor?)
}

class SelectFileResultContract : ActivityResultContract<SelectFileParams, Uri?>() {

    override fun createIntent(context: Context, data: SelectFileParams): Intent =
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setTypeAndNormalize(data.fileMimeType)
        }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? = when (resultCode) {
        Activity.RESULT_OK -> intent?.data
        else -> null
    }
}

class StorageAccessFrameworkInteractor(private val fileSelectionEntryPoint: FileSelectionEntryPoint) {
    private val selectFileLauncher: ActivityResultLauncher<SelectFileParams> =
        fileSelectionEntryPoint.fileSelectionOwner
            .registerForActivityResult(SelectFileResultContract()) { uri ->
                onFileSelectionFinished(uri)
            }

    fun beginSelectingFile(selectFileParams: SelectFileParams) =
        selectFileLauncher.launch(selectFileParams)

    private fun onFileSelectionFinished(fileUri: Uri?) {
        val fileDescriptor = fileUri?.let { uri ->
            fileSelectionEntryPoint.fileSelectionOwner
                .requireContext()
                .contentResolver
                .openFileDescriptor(uri, "r")
                ?.fileDescriptor
        }

        fileSelectionEntryPoint.onFileSelected(fileDescriptor)
    }
}