package com.example.snapkit.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.snapkit.databinding.FragmentImageViewerBinding

class ImageViewerFragment : Fragment() {
    private lateinit var binding: FragmentImageViewerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImageViewerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImageView()
    }

    /**
     * Set the ImageViewer to the image that was selected in the thumbnail gallery.
     */
    fun setImageView() {
        if (arguments != null) {
            var filePath = ImageViewerFragmentArgs.fromBundle(arguments!!).filePath
            var imageViewer = binding.imageViewer

            Glide.with(imageViewer.context)
                .load(filePath)
                .fitCenter()
                .into(imageViewer)
        }
    }
}