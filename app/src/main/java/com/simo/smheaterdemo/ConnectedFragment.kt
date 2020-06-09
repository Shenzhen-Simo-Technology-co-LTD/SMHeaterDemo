package com.simo.smheaterdemo

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
        }
    }

    var currentTemperature: Int = 0
        set(value) {
            if (value < minTemperature) {
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

    override fun deviceStatusDidChanged(device: SMHeater) {
        if (device.targetTemperature != 0) {
            _targetTemperature = device.targetTemperature
            binding.seekBar.progress = device.targetTemperature - minTemperature
        }
        if (device.currentTemperature != 0) {
            currentTemperature = device.currentTemperature
        }
    }

    override fun didStartReconnect(device: SMHeater) {
        showLoadingHUD("Reconnecting...")
    }
}