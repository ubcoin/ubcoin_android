package com.ubcoin.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ubcoin.R
import com.ubcoin.model.response.SingleTransaction
import com.ubcoin.utils.moneyFormat
import com.ubcoin.utils.moneyFormatETH
import com.ubcoin.utils.toDate
import com.ubcoin.utils.toTransactionDate
import java.util.*

/**
 * Created by Yuriy Aizenberg
 */
class TransactionsAdapter(context: Context) : BaseRecyclerAdapter<SingleTransaction, TransactionsAdapter.VHolder>(context) {

    private val positiveColor = ContextCompat.getColor(context, R.color.greenMainColor)
    private val negativeColor = ContextCompat.getColor(context, R.color.activeTabTextColor)
    private val rejectedColor = ContextCompat.getColor(context, R.color.itemStatusBlock)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): VHolder {
        return VHolder(inflate(R.layout.item_transaction, p0))
    }

    override fun onBindViewHolder(vHolder: VHolder, p1: Int) {
        val transaction = getItem(p1)
        var date = transaction.createdDate.toDate()
        if (date == null) {
            date = Date()
        }
        vHolder.txtItemTransactionDate.text = date.toTransactionDate()
        vHolder.txtItemTransactionAmount.text = transaction.getStringValue(context)

        if (transaction.isPositive())
            vHolder.txtItemTransactionAmount.setTextColor(positiveColor)
        else
            vHolder.txtItemTransactionAmount.setTextColor(negativeColor)

        if (transaction.isRejected())
            vHolder.txtItemTransactionAmount.setTextColor(rejectedColor)

        if (transaction.isPending() || transaction.isRejected()) {
            vHolder.imgItemTransactionPending.visibility = View.VISIBLE

            if (transaction.isRejected())
                vHolder.imgItemTransactionPending.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fail))
            else
                vHolder.imgItemTransactionPending.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pend))
        }
        else
            vHolder.imgItemTransactionPending.visibility = View.GONE
    }


    class VHolder(itemView: View) : BaseRecyclerAdapter.VHolder(itemView) {
        val txtItemTransactionDate = findView(R.id.txtItemTransactionDate) as TextView
        val txtItemTransactionAmount = findView(R.id.txtItemTransactionAmount) as TextView
        val imgItemTransactionPending = findView(R.id.imgItemTransactionPending) as ImageView
    }


}