package com.example.mytodo.ui.subTask

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytodo.R
import com.example.mytodo.databinding.ListItemAttachmentBinding
import java.io.File





class AttachmentAdapter(val itemList: ArrayList<String>) :
    RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>() {
    class AttachmentViewHolder(private val binding: ListItemAttachmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(img: String) {
            Log.e("img", "$img")
//            binding.ivImage.setImageURI(img)
//            binding.ivImage.setImageURI(
//                Uri.parse(
//                    Environment.getExternalStorageDirectory().absolutePath.plus(img)
//                )
//            );
            val imgFile = File("omg")

            val imgBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

            // on below line we are setting bitmap to our image view.

            // on below line we are setting bitmap to our image view.
//            binding.ivImage.setImageBitmap(imgBitmap)
            Glide.with(binding.ivImage)
                .load(img)
                .centerCrop()
                .placeholder(R.drawable.ic_place_holder)
                .into(binding.ivImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AttachmentViewHolder(ListItemAttachmentBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.bind(itemList[position])
    }
}