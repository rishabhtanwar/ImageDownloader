package com.example.imagedownloadertest.ui

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.imagedownloadertest.data.model.Resource
import com.example.imagedownloadertest.R
import com.example.imagedownloadertest.data.model.ImageListResponse
import com.example.imagedownloadertest.databinding.ActivityMainBinding
import com.example.imagedownloadertest.util.ConnectivityHelper
import com.example.imagedownloadertest.util.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()
    private lateinit var activityMainBinding: ActivityMainBinding
    private var dialog: ProgressDialog? = null
    private var rvAdapter: RvAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        rvAdapter = RvAdapter()
        activityMainBinding.recyclerView.adapter = rvAdapter
        setObserver()
        if (!ConnectivityHelper.checkConnectivity(this)){
            showToast("Please check your internet connection and come back again!!")
            return
        }
        viewModel.getImageListResponse()

    }

    private fun setObserver() {
        viewModel.imageListResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    hideProgress()
                  if(it.data?.isNotEmpty()==true){
                      updateRvAdapter(it.data)
                  }else{
                      showToast("Not able to fetch the data. Please try again!!")
                  }
                }

                is Resource.Error -> {
                    showToast(it.error)
                    hideProgress()
                }

                is Resource.Loading -> {
                    showProgress()
                }
            }
        }

    }


    private fun updateRvAdapter(imageListResponse: ImageListResponse){
      rvAdapter?.updateList(imageListResponse)
    }

    private fun showProgress() {
        dialog = ProgressDialog(this)
        dialog?.setMessage("Loading...")
        dialog?.show()
    }

    private fun hideProgress() {
        dialog?.hide()
        dialog?.cancel()
    }

}