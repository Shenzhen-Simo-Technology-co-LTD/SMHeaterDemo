package com.simo.smheaterdemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simo.smheatersdk.SMHeaterModel

class HeaterModelProvider(private val devices: List<SMHeaterModel>) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HeaterViewModel::class.java)) {
            return HeaterViewModel(MutableLiveData(devices)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}