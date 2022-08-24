package org.davincicodeos.updater

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileDescriptor

data class SelectFileParams(
    val fileMimeType: String
)

interface FileSelectionEntryPoint {
    val fileSelectionOwner: Fragment

    fun onFileSelected(fileName: String?, fileDescriptor: FileDescriptor?)
}

class SelectFileResultContract : ActivityResultContract<SelectFileParams, Uri?>() {

    override fun createIntent(context: Context, input: SelectFileParams): Intent =
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setTypeAndNormalize(input.fileMimeType)
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
        val fileName = fileUri?.path?.let {
            val path = it.replace("/document/primary:", "/storage/emulated/0/")
            File(path).name
        }
        val fileDescriptor = fileUri?.let {
            fileSelectionEntryPoint.fileSelectionOwner
                .requireContext()
                .contentResolver
                .openFileDescriptor(it, "r")
                ?.fileDescriptor
        }

        fileSelectionEntryPoint.onFileSelected(fileName, fileDescriptor)
    }
}