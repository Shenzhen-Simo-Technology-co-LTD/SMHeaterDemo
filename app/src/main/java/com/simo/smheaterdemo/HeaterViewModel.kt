package com.simo.smheaterdemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simo.smheatersdk.SMHeaterModel

class HeaterViewModel(val devices: MutableLiveData<List<SMHeaterModel>>): ViewModel() {

}