package org.davincicodeos.updater.ui.flashing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FlashingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is flashing Fragment"
    }
    val text: LiveData<String> = _text
}