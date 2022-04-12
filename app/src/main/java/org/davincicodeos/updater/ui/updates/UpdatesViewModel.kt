package org.davincicodeos.updater.ui.updates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UpdatesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is updates Fragment"
    }
    val text: LiveData<String> = _text
}