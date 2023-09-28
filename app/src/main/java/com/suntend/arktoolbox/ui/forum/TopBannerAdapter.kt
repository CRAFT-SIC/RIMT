package com.suntend.arktoolbox.ui.forum

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.suntend.arktoolbox.R
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class TopBannerAdapter : BaseBannerAdapter<TopBannerEntity>() {

    override fun bindData(holder: BaseViewHolder<TopBannerEntity>, data: TopBannerEntity, position: Int, pageSize: Int) {
        var  imageView=holder.itemView.findViewById<ImageView>(R.id.iv_cover)
     if (data.url!=null){
           Glide.with(holder.itemView.context).load(data.url).into(imageView)
       }
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_forum_top_banner;
    }
}