package com.ubcoin.fragment.profile

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.rengwuxian.materialedittext.MaterialEditText
import com.squareup.picasso.Picasso
import com.ubcoin.R
import com.ubcoin.ThePreferences
import com.ubcoin.activity.IActivity
import com.ubcoin.activity.MainActivity
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.fragment.market.MarketListFragment
import com.ubcoin.model.TemporaryUser
import com.ubcoin.model.response.User
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.CircleTransformation
import com.ubcoin.utils.ImeDoneActionHandler
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.view.menu.MenuBottomView
import com.ubcoin.view.menu.MenuItems
import retrofit2.Response

/**
 * Created by Yuriy Aizenberg
 */
class ProfileSettingsFragment : BaseFragment() {

    private lateinit var user: User
    private lateinit var temporaryUser: TemporaryUser

    private var savedCountry: Country? = null

    private lateinit var imgProfilePhoto: ImageView
    private lateinit var edtSettingsName: MaterialEditText
    private lateinit var edtSettingsEmail: MaterialEditText
    private lateinit var txtSettingsCountry: TextView
    private lateinit var imgSettingsCountry: ImageView
    private lateinit var llSettingsCountry: View
    private lateinit var edtProfileSettingsCountry: MaterialEditText
    private lateinit var txtProfileSettingsBalance: TextView

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        if (ProfileHolder.user != null) {
            user = ProfileHolder.user!!
        } else {
            activity?.onBackPressed()
            return
        }
        temporaryUser = ThePreferences().getCurrentPreferences()

        view.findViewById<View>(R.id.llHeaderRight).setOnClickListener {
            onDoneClick()
        }

        view.findViewById<View>(R.id.llSettingsCountrySelect).setOnClickListener {
            selectCountry()
        }

        view.findViewById<View>(R.id.llSettingsLanguageSelect).setOnClickListener {
            selectLanguage()
        }

        view.findViewById<View>(R.id.btnLogout).setOnClickListener {
            processLogout()
        }


        imgProfilePhoto = view.findViewById(R.id.imgProfilePhoto)
        edtSettingsName = view.findViewById(R.id.edtSettingsName)
        edtSettingsEmail = view.findViewById(R.id.edtSettingsEmail)

        txtSettingsCountry = view.findViewById(R.id.txtSettingsCountry)
        imgSettingsCountry = view.findViewById(R.id.imgSettingsCountry)
        llSettingsCountry = view.findViewById(R.id.llSettingsCountry)

        edtProfileSettingsCountry = view.findViewById(R.id.edtProfileSettingsCountry)
        txtProfileSettingsBalance = view.findViewById(R.id.txtProfileSettingsBalance)

        edtSettingsName.setOnEditorActionListener(object : ImeDoneActionHandler() {
            override fun onActionCall() {
                hideKeyboard()
            }
        })

        setupAvatar()

        edtSettingsName.setText(user.name)
        edtSettingsEmail.setText(user.email)

        setCountry(temporaryUser.country)

    }

    private fun setCountry(country: Country?) {
        if (country == null) {
            txtSettingsCountry.setText(R.string.country)
            llSettingsCountry.visibility = View.GONE
            imgSettingsCountry.setImageDrawable(null)
        } else {
            txtSettingsCountry.text = country.name
            val flag = country.flag
            if (flag == -1) {
                llSettingsCountry.visibility = View.GONE
                imgSettingsCountry.setImageDrawable(null)
            } else {
                llSettingsCountry.visibility = View.VISIBLE
                imgSettingsCountry.setImageResource(flag)
            }
        }
    }

    private fun setupAvatar() {
        val avatarUrl = user.avatarUrl
        if (avatarUrl == null || avatarUrl.isBlank()) {
            imgProfilePhoto.setImageResource(R.drawable.img_profile_default)
        } else {
            Picasso.get().load(avatarUrl)
                    .resizeDimen(R.dimen.detailsSubProfileHeight, R.dimen.detailsSubProfileHeight)
                    .centerCrop()
                    .transform(CircleTransformation())
                    .placeholder(R.drawable.img_profile_default)
                    .error(R.drawable.img_profile_default)
                    .into(imgProfilePhoto)
        }
    }


    private fun selectLanguage() {

    }

    private fun selectCountry() {
        val dialog = CountryPicker.Builder()
                .with(activity!!)
                .canSearch(true)
                .listener {
                    savedCountry = it
                    setCountry(savedCountry)
                }.build()
        dialog.showDialog(childFragmentManager)
    }

    private fun onDoneClick() {
        showProgressDialog("Wait please", "Updating profile")
        val userName = edtSettingsName.text.toString().trim()
        DataProvider.updateProfileEmailAndName(user.email!!, userName,
                object : SilentConsumer<Response<Unit>> {
                    override fun onConsume(t: Response<Unit>) {
                        hideProgressDialog()
                        user.name = userName
                        ProfileHolder.user = user
                        activity?.onBackPressed()
                    }
                },
                object : SilentConsumer<Throwable> {
                    override fun onConsume(t: Throwable) {
                        hideProgressDialog()
                        handleException(t)
                    }
                }
        )
    }


    private fun processLogout() {
        showProgressDialog("Wait please", "Logout")
        DataProvider.logout(
                object : SilentConsumer<Response<Unit>> {
                    override fun onConsume(t: Response<Unit>) {
                        hideProgressDialog()
                        ThePreferences().clearProfile()
                        ThePreferences().clearPrefs()
                        ProfileHolder.user = null
                        //todo refactored
                        getSwitcher()?.clearBackStack()?.addTo(MarketListFragment::class.java)
                        ((activity as IActivity).getFooter() as MenuBottomView).activate(MenuItems.MARKET)
                    }
                },
                object : SilentConsumer<Throwable> {
                    override fun onConsume(t: Throwable) {
                        handleException(t)
                    }
                })
    }

    override fun handleException(t: Throwable) {
        hideProgressDialog()
        super.handleException(t)
    }


    override fun getLayoutResId() = R.layout.fragment_profile_details

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun getHeaderText() = R.string.account_settings

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

    override fun isFooterShow() = false

}