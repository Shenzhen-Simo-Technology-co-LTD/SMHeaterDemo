package com.simo.smheaterdemo

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.LayoutDirection
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.HeaderViewListAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simo.smheaterdemo.databinding.FragmentSearchBinding
import com.simo.smheaterdemo.databinding.HeaterItemBinding
import com.simo.smheatersdk.BLELog
import com.simo.smheatersdk.SMBLEManager
import com.simo.smheatersdk.SMHeater
import com.simo.smheatersdk.SMHeaterModel
import timber.log.Timber

class SearchFragment : DemoBaseFragment() {
    lateinit var binding: FragmentSearchBinding
    val deviceData: MutableList<SMHeaterModel> = mutableListOf()
    private lateinit var adapter: SearchAdapter
    lateinit var viewModel: HeaterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        setupViews()
    }

    override fun onResume() {
        super.onResume()
        startSearchingDevice()
    }

    private fun setupViews() {
        adapter = SearchAdapter(HeaterItemListener {
            didSelectHeater(it)
        })

        val vmFactory = HeaterModelProvider(deviceData)
        viewModel = ViewModelProvider(this, vmFactory).get(HeaterViewModel::class.java)
        viewModel.devices.observe(viewLifecycleOwner, Observer {
            Timber.i("Observer list changed ${it.size} and submitList")
            adapter.submitList(it)
        })

        val layoutManager = LinearLayoutManager(requireActivity())
        layoutManager.orientation = RecyclerView.VERTICAL

        binding.apply {

            resultListView.adapter = adapter
            resultListView.layoutManager = layoutManager
        }

        adapter.submitList(deviceData)
    }

    private fun startSearchingDevice() {
        Timber.d("startSearchingDevice")
        showLoadingHUD("Searching...")
        deviceData.clear()
        adapter.submitList(deviceData)
        SMBLEManager.instance.scanSMDevice(1000L, 0) {
            deviceData.clear()
            deviceData.addAll(it)
            adapter.submitList(deviceData)
            binding.apply {
                if (it.size > 0) {
                    hideHUD()
                }else {
                    if (!isShowedLoading) {
                        showLoadingHUD("Searching")
                    }
                }
            }
            viewModel.devices.value = deviceData.toList()
        }.scanTimeoutCallback {
            BLELog.d("Searching timeout, stop search.")
            hideHUD()
        }
    }

    override fun onPause() {
        super.onPause()
        SMBLEManager.instance.stopScanSMDevice()
        hideHUD()
    }

    override fun onDestroy() {
        super.onDestroy()
        SMBLEManager.instance.stopScanSMDevice()
        hideHUD()
    }

    private fun didSelectHeater(heaterModel: SMHeaterModel) {
        SMBLEManager.instance.stopScanSMDevice()
        Timber.i("didSelectDevice ${heaterModel.name}\nSN:${heaterModel.snCodeDisplay}")
        showLoadingHUD("Connecting...")
        SMBLEManager.instance.connectDevice(heaterModel.device!!) { glbleDevice: SMHeater?, isSuccess: Boolean, errCode: Int, errDesc: String? ->
            hideHUD()
            if (isSuccess) {
                gotoConnected()
            }else {
                showMessageHUD("Connect failed, " + errDesc)
            }
        }
    }

    private fun gotoConnected() {
        findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToConnectedFragment())
    }
}

class SearchAdapter(val clickListener: HeaterItemListener) : ListAdapter<SMHeaterModel, SearchAdapter.HeaterItem>(HeaterListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaterItem {
        return HeaterItem.from(parent)
    }

    override fun onBindViewHolder(holder: HeaterItem, position: Int) {
        val heaterModel = getItem(position)
        holder.bind(heaterModel, clickListener)
    }

    class HeaterItem(val binding: HeaterItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: SMHeaterModel, listener: HeaterItemListener) {
            binding.model = model
            binding.listener = listener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): HeaterItem {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HeaterItemBinding.inflate(layoutInflater, parent, false)
                return HeaterItem(binding)
            }
        }
    }
}

class HeaterListDiffCallback : DiffUtil.ItemCallback<SMHeaterModel>() {
    override fun areItemsTheSame(oldItem: SMHeaterModel, newItem: SMHeaterModel): Boolean {
        return oldItem.snCode == newItem.snCode
    }

    override fun areContentsTheSame(oldItem: SMHeaterModel, newItem: SMHeaterModel): Boolean {
        return oldItem == newItem
    }
}

class HeaterItemListener(val clickListener: (model: SMHeaterModel) -> Unit) {
    fun onClick(model: SMHeaterModel) {
        clickListener(model)
    }
}

class CommonItemClickListener<T>(val clickListener: (model: T) -> Unit) {
    fun onClick(model: T) {
        clickListener(model)
    }
}

@BindingAdapter("wifiLevelImage")
fun ImageView.setWifiLevelImage(item: Int) {
    setImageResource(when (item) {
        0 -> R.drawable.wifi_0
        1 -> R.drawable.wifi_1
        2 -> R.drawable.wifi_2
        3 -> R.drawable.wifi_3
        else -> R.drawable.wifi_0
    })

}

