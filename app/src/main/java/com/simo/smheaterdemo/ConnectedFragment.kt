package com.simo.smheaterdemo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.simo.smheaterdemo.databinding.FragmentConnectedBinding
import com.simo.smheatersdk.*
import timber.log.Timber

class ConnectedFragment : DemoBaseFragment(), SMHeaterDelegate {
    var deviceReady = false

    lateinit var binding: FragmentConnectedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_connected, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            seekBar.max = maxTemperature - minTemperature
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    _targetTemperature = progress + minTemperature
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar?.let {
                        val temperature = it.progress + minTemperature
                        targetTemperature = temperature
                    }
                }
            })

            switchHeating.setOnCheckedChangeListener { buttonView, isChecked ->
                SMBLEManager.instance.currentDevice?.setHeatOnOff(isChecked)
            }
        }

        SMBLEManager.instance.currentDevice?.setHeatOnOff(true)
        showLoadingHUD()
//        CoroutineScope(Dispatchers.Main).launch {
//            delay(1500)
//            hideHUD()
//        }
    }

    var currentTemperature: Int = 0
        set(value) {
            if (value < 0) {
                binding.currentTemperatureLabel.text = "- -"
            } else {
                binding.currentTemperatureLabel.text = "${value} ℃"
            }
            field = value
        }

    var _targetTemperature: Int = minTemperature
        set(value) {
            var tV = 0
            if (value > maxTemperature) {
                binding.targetTemperatureLabel.text = "$maxTemperature ℃"
                tV = maxTemperature
            }else if (value < minTemperature) {
                binding.targetTemperatureLabel.text = "- - ℃"
                tV = 0
            }else {
                binding.targetTemperatureLabel.text = "$value ℃"
                tV = value
            }
            field = value
        }

    var targetTemperature: Int = minTemperature
        set(value) {
            val tV = if (value > maxTemperature) maxTemperature else value
            field = tV
            showMessageHUD("Set Target Temperature: ${tV}", 1000L)
            Timber.i("MotionEvent.ACTION_UP, Sync Target Temperature to Device")
            SMBLEManager.instance.currentDevice?.let {
                it.setTartgetTemperature(targetTemperature) { }
            }
            _targetTemperature = tV
        }

    override fun onStart() {
        super.onStart()
        SMBLEManager.instance.currentDevice?.addDelegate(this)
    }

    override fun onStop() {
        super.onStop()
        SMBLEManager.instance.currentDevice?.removeDelegate(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        SMBLEManager.instance.currentDevice?.disconnect()
    }

    /* SMHeaterDelegate */
    override fun didConnected(device: SMHeater) {
        hideHUD()
    }

    override fun didDisconnected(device: SMHeater) {
        hideHUD()
        findNavController().navigateUp()
    }

    @SuppressLint("SetTextI18n")
    override fun deviceStatusDidChanged(device: SMHeater) {
        if (!deviceReady) {
            deviceReady = true
            hideHUD()
        }

        binding.switchHeating.isChecked = device.isHeating
        if (device.isHeating) {
            binding.isHeatingLabel.text = "True"
            if (device.targetTemperature != 0) {
                _targetTemperature = device.targetTemperature
                binding.seekBar.progress = device.targetTemperature - minTemperature
            }
            if (device.currentTemperature != 0) {
                currentTemperature = device.currentTemperature
            }
        }else {
            binding.isHeatingLabel.text = "False"
            currentTemperature = -1
            _targetTemperature = -1
            binding.seekBar.progress = 0
        }

        binding.apply {
            ntcLabel.text = device.ntcState.toString()
            qcLabel.text = device.qcState.toString()
            pwmLabel.text = device.pwmState.toString()
            voltageLabel.text = device.inputMilVoltage.toString() + " mV"
        }
    }

    override fun didStartReconnect(device: SMHeater) {
        showLoadingHUD("Reconnecting...") {
            SMBLEManager.instance.cancelConnectDevice()
            findNavController().navigateUp()
        }
    }
}