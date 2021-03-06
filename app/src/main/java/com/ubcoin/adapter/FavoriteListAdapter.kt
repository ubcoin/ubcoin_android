package com.ubcoin.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.model.response.MarketItem
import com.ubcoin.utils.CollectionExtensions
import com.ubcoin.utils.moneyFormat
import com.ubcoin.view.rating.RatingBarView
import kotlin.math.roundToInt

/**
 * Created by Yuriy Aizenberg
 */
class FavoriteListAdapter(context: Context) : BaseRecyclerAdapter<MarketItem, FavoriteListAdapter.VHolder>(context) {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): VHolder {
        return VHolder(inflate(R.layout.item_market_in_favorite, p0))
    }

    override fun onBindViewHolder(vHolder: VHolder, p1: Int) {
        val item = getItem(p1)
        val images = item.images
        if (CollectionExtensions.nullOrEmpty(images)) {
            vHolder.imgFavoriteItemLogo.setImageResource(R.drawable.img_photo_placeholder)
        } else {
            val dimensionPixelSize = context.resources.getDimensionPixelSize(R.dimen.marketInFavoriteHeightImage)
            GlideApp.with(context).load(images!![0])
                    .override(dimensionPixelSize, dimensionPixelSize)
                    .centerCrop()
                    .transform(RoundedCorners(10))
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.img_photo_placeholder)
                    .error(R.drawable.img_photo_placeholder)
                    .into(vHolder.imgFavoriteItemLogo)
//            Picasso.get().load(images!![0])
//                    .resize(dimensionPixelSize, dimensionPixelSize)
//                    .centerCrop()
//                    .onlyScaleDown()
//                    .transform(RoundedCornersTransform())
//                    .memoryPolicy(MemoryPolicy.NO_CACHE)
//                    .networkPolicy(NetworkPolicy.NO_CACHE)
//                    .placeholder(R.drawable.img_photo_placeholder)
//                    .error(R.drawable.img_photo_placeholder)
//                    .into(vHolder.imgFavoriteItemLogo)
        }
        vHolder.txtFavoriteItemName.text = item.title
        vHolder.txtFavoriteItemPrice.text = (item.price ?: .0).moneyFormat() + " UBC"
        vHolder.ratingBar.setRating(item.user?.rating?.roundToInt() ?: 0)
        bindTouchListener(vHolder.itemView, vHolder.adapterPosition, item)
    }


    class VHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {
        val imgFavoriteItemLogo: ImageView = findView(R.id.imgFavoriteItemLogo)
        val txtFavoriteItemPrice: TextView = findView(R.id.txtFavoriteItemPrice)
        val txtFavoriteItemName: TextView = findView(R.id.txtFavoriteItemName)
        val ratingBar: RatingBarView = findView(R.id.ratingBarView)
    }

}
