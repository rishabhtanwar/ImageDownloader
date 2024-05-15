package com.example.imagedownloadertest.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.imagedownloadertest.R
import com.example.imagedownloadertest.data.model.ImageListResponseItem
import com.example.imagedownloadertest.databinding.ItemLayoutBinding
import com.example.imagedownloadertest.image_loader.ImageLoader

class RvAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var imageListData:ArrayList<ImageListResponseItem> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_layout, parent, false
        )
        return ImageVH(binding)
    }

    override fun getItemCount(): Int = imageListData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ImageVH).bindData(imageListData.get(position))
    }

    fun updateList(imageListData:List<ImageListResponseItem>){
        this.imageListData.clear()
        this.imageListData.addAll(imageListData)
        notifyDataSetChanged()
    }

  inner  class ImageVH(private val binding: ItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        fun bindData(imageListResponseItem: ImageListResponseItem){
            var imageUrl =""
            if (!imageListResponseItem.thumbnail?.domain.isNullOrEmpty() && !imageListResponseItem.thumbnail?.basePath.isNullOrEmpty() && !imageListResponseItem.thumbnail?.key.isNullOrEmpty()){
                imageUrl ="${imageListResponseItem.thumbnail?.domain}/${imageListResponseItem.thumbnail?.basePath}/0/${imageListResponseItem.thumbnail?.key}"
            }
            if (imageUrl.isEmpty()){
              binding.itemImageView.setImageResource(R.drawable.placeholder_image)
                return
            }
         ImageLoader.with(binding.root.context).load(binding.itemImageView,imageUrl,imageListResponseItem.thumbnail?.id?:"")
        }
    }
}