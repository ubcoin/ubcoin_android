package com.ubcoin.view.deal_description

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.maps.model.LatLng
import com.rengwuxian.materialedittext.MaterialEditText
import com.ubcoin.R
import com.ubcoin.model.Currency
import com.ubcoin.model.ItemPurchaseDto
import com.ubcoin.model.response.Location
import com.ubcoin.model.response.MarketItem
import com.ubcoin.network.DataProvider
import com.ubcoin.utils.*
import com.ubcoin.utils.filters.FiltersHolder
import com.ubcoin.view.filter.SelectableView

class PurchaseMainView: LinearLayout {

    private lateinit var llAddressInput: LinearLayout
    private lateinit var etAddress: MaterialEditText
    private lateinit var etItemPrice: MaterialEditText
    private lateinit var selectETH: SelectableView
    private lateinit var selectUBC: SelectableView
    private lateinit var btnConfirm: Button
    private lateinit var tvBalance: TextView
    private lateinit var tvETHCommision: TextView
    var activity: Activity? = null

    public fun setCreatePurchaseListener(listener: OnCreatePurchase){
        this.listener = listener
    }

    var listener: OnCreatePurchase? = null

    var currency: Currency = Currency.UBC

    public fun IsAddressInputEnabled(isEnabled: Boolean)
    {
        if(isEnabled)
            llAddressInput.visibility = View.VISIBLE
        else
            llAddressInput.visibility = View.GONE
    }

    var marketItem: MarketItem? = null
        set(value) {
            field = value
            initView()
        }

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(attrs: AttributeSet?) {
        inflate(context, R.layout.view_purchase_main, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        llAddressInput = findViewById(R.id.llAddressInput)
        etAddress = findViewById(R.id.etAddress)
        etItemPrice = findViewById(R.id.etItemPrice)
        selectETH = findViewById(R.id.selectETH)
        selectUBC = findViewById(R.id.selectUBC)
        btnConfirm = findViewById(R.id.btnConfirm)
        tvBalance = findViewById(R.id.tvBalance)
        tvETHCommision = findViewById(R.id.tvETHCommision)
        tvBalance.text = context.getString(R.string.text_your_balance) + " " + (ProfileHolder.balance?.effectiveAmount ?: .0).moneyFormat()

        val function = { _: SelectableView, arg: String, index: Int, isSelected: Boolean ->
            if (isSelected) {
                if(index == 0) {
                    currency = Currency.UBC
                    selectETH.changeSelectionVisual(false)
                    btnConfirm.text = context.getString(R.string.text_confirm_in_ubc)
                    tvBalance.text = context.getString(R.string.text_your_balance) + " " + (ProfileHolder.balance?.effectiveAmount ?: .0).moneyFormat()
                    tvETHCommision.visibility = View.GONE
                }
                else {
                    currency = Currency.ETH
                    selectUBC.changeSelectionVisual(false)
                    btnConfirm.text = context.getString(R.string.text_confirm_in_eth)
                    tvBalance.text = context.getString(R.string.text_your_balance) + " " + (ProfileHolder.balance?.effectiveAmountETH ?: .0).moneyFormatETH()
                    tvETHCommision.visibility = View.VISIBLE
                }

                var price = marketItem!!.price
                var amount = ProfileHolder.balance?.effectiveAmount
                if(currency == Currency.ETH) {
                    price = marketItem!!.priceETH
                    amount = ProfileHolder.balance?.effectiveAmountETH
                }


                if(amount == null || price == null || amount!! < price!!)
                    tvBalance.setTextColor(context.resources.getColor(R.color.red))
                else
                    tvBalance.setTextColor(Color.parseColor("#403d45"))

            }
        }
        selectUBC.selectionCallback = function
        selectETH.selectionCallback = function
        selectUBC.isSelected = true
        selectUBC.changeSelectionVisual(true)

        btnConfirm.setOnClickListener {

            if(llAddressInput.visibility == View.VISIBLE) {
                if (etAddress.text.toString() == null || etAddress.text.toString().length == 0) {
                    if (activity != null) {
                        activity?.run {
                            MaterialDialog.Builder(this).title(R.string.error).content("Location address missing").build().show()
                        }
                    }
                    return@setOnClickListener
                }
            }

            var price = marketItem!!.price
            var amount = ProfileHolder.balance?.effectiveAmount
            var text = context.getString(R.string.text_not_enough_ubc)
            var priceText = (marketItem?.price ?: .0).moneyFormat() + " UBC"

            if(currency == Currency.ETH)
            {
                price = marketItem!!.priceETH
                amount = ProfileHolder.balance?.effectiveAmountETH
                text = context.getString(R.string.text_not_enough_eth)
                priceText = marketItem?.priceETH!!.moneyRoundedFormatETH() + " ETH"
            }

            if(amount == null || price == null || amount!! < price!!)
            {
                val materialDialog = MaterialDialog.Builder(context)
                        .content(text)
                        .title(context.getString(R.string.text_please_top_up_your_balance))
                        .positiveText(context.getString(R.string.text_ok))
                        .positiveColor(ContextCompat.getColor(context, R.color.greenMainColor))
                        .onPositive { dialog, _ -> dialog.dismiss() }
                        .build()
                materialDialog!!.show()
            }
            else
            {
                val materialDialog = MaterialDialog.Builder(context)
                        .content(context.getString(R.string.text_confirm_purchace))
                        .title(priceText + " " + context.getString(R.string.text_will_be_blocked))
                        .positiveText(context.getString(R.string.confirm))
                        .negativeText(context.getString(R.string.cancel))
                        .positiveColor(ContextCompat.getColor(context, R.color.greenMainColor))
                        .negativeColor(ContextCompat.getColor(context, R.color.greenMainColor))
                        .onPositive { dialog, _ -> buyItem()
                            dialog.dismiss() }
                        .onNegative { dialog, _ -> dialog.dismiss() }
                        .build()
                materialDialog!!.show()
            }
        }
    }

    fun buyItem(){
        if(listener != null)
            listener!!.purchase(currency, etAddress.text.toString())
    }

    fun initView(){
        etItemPrice.setText((marketItem?.price ?: .0).moneyFormat() + " UBC / " + marketItem?.priceETH!!.moneyRoundedFormatETH() + " ETH")

        var price = marketItem!!.price
        var amount = ProfileHolder.balance?.effectiveAmount
        if(currency == Currency.ETH) {
            price = marketItem!!.priceETH
            amount = ProfileHolder.balance?.effectiveAmountETH
        }

        if(amount != null) {
            if (amount!! < price!!)
                tvBalance.setTextColor(context.resources.getColor(R.color.red))
            else
                tvBalance.setTextColor(Color.parseColor("#403d45"))
        }
    }

    interface OnCreatePurchase{
        fun purchase(currency: Currency, comment: String)
    }
}