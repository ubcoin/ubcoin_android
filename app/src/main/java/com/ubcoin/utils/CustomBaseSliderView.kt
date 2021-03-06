package com.ubcoin.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.ubcoin.GlideApp
import com.ubcoin.GlideRequest
import java.io.File

/**
 * Created by Yuriy Aizenberg
 */
abstract class CustomBaseSliderView protected constructor(context: Context) : BaseSliderView(context) {

    private var mUrl: String? = null
    private var mFile: File? = null
    private var mRes: Int = 0
    private val mLoadListener: BaseSliderView.ImageLoadListener? = null

    override fun image(url: String): BaseSliderView {
        mUrl = url
        return super.image(url)
    }

    override fun image(file: File): BaseSliderView {
        mFile = file
        return super.image(file)
    }

    override fun image(res: Int): BaseSliderView {
        mRes = res
        return super.image(res)
    }

    protected fun bindEventAndShow(v: View, targetImageView: ImageView?, disableCacheAndStore: Boolean, height: Int, width: Int, context: Context) {
        if (!disableCacheAndStore) {
            super.bindEventAndShow(v, targetImageView)
            return
        }

        val me = this

        v.setOnClickListener {
            if (mOnSliderClickListener != null) {
                mOnSliderClickListener.onSliderClick(me)
            }
        }

        if (targetImageView == null)
            return

        mLoadListener?.onStart(me)

        val glide = GlideApp.with(context)

        // val p = if (picasso != null) picasso else Picasso.get()
        val requestCreator: GlideRequest<Drawable>
        requestCreator = when {
            url != null -> glide.load(mUrl)
            mFile != null -> glide.load(mFile!!)
            mRes != 0 -> glide.load(mRes)
            else -> return
        }

        if (requestCreator == null) {
            return
        }

        // To Disable Image Caching
        /*   requestCreator.networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE);
        requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE);
*/
        if (empty != 0) {
            requestCreator.placeholder(empty)
        }

        if (error != 0) {
            requestCreator.error(error)
        }

        when (scaleType) {
            BaseSliderView.ScaleType.Fit -> requestCreator.fitCenter()
            BaseSliderView.ScaleType.FitCenterCrop -> requestCreator.fitCenter().centerCrop()
            BaseSliderView.ScaleType.CenterCrop -> requestCreator.override(width, height).centerCrop()
            BaseSliderView.ScaleType.CenterInside -> requestCreator.override(width, height).centerInside()
        }

        requestCreator.listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                mLoadListener?.onEnd(false, me)
                if (v.findViewById<View>(com.daimajia.slider.library.R.id.loading_bar) != null) {
                    v.findViewById<View>(com.daimajia.slider.library.R.id.loading_bar).visibility = View.INVISIBLE
                }
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                if (v.findViewById<View>(com.daimajia.slider.library.R.id.loading_bar) != null) {
                    v.findViewById<View>(com.daimajia.slider.library.R.id.loading_bar).visibility = View.INVISIBLE
                }
                return false
            }

        })
        requestCreator.into(targetImageView)
    }
}



