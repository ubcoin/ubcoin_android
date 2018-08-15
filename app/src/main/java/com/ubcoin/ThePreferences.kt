package com.ubcoin

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.ubcoin.model.TemporaryUser
import com.ubcoin.model.response.User

/**
 * Created by Yuriy Aizenberg
 */

private const val KEY_TOKEN: String = "TOKEN"
private const val KEY_COOKIE: String = "COOKIE"
private const val KEY_WV_COOKIE: String = "WV_COOKIE"
private const val KEY_CURRENT_USER: String = "CURR_USER"
private const val KEY_CURRENT_USER_PREFS: String = "CURR_USER_PREFS"
private const val TAG: String = "ThePreferences"


class ThePreferences {

    init {
        val theApplication = TheApplication.instance
        instance = theApplication.getSharedPreferences(theApplication.packageName, 0)
    }

    companion object {
        lateinit var instance: SharedPreferences
            private set
    }

    fun getToken(): String? = instance.getString(KEY_TOKEN, null)

    fun setToken(token: String?) = instance.edit().putString(KEY_TOKEN, token).apply()

    fun getCookie(): String? = instance.getString(KEY_COOKIE, null)

    fun setCookie(token: String?) = instance.edit().putString(KEY_COOKIE, token).apply()

    fun getWVCookie(): String? = instance.getString(KEY_WV_COOKIE, null)

    fun setWVCookie(token: String?) = instance.edit().putString(KEY_WV_COOKIE, token).apply()

    fun setCurrentUser(user: User) {
        val stringProfile = Gson().toJson(user)
        instance.edit().putString(KEY_CURRENT_USER, stringProfile).apply()
    }

    fun getCurrentUser(): User? {
        val stringUser = instance.getString(KEY_CURRENT_USER, null)
        if (stringUser == null || stringUser.isEmpty()) return null
        return try {
            Gson().fromJson(stringUser, User::class.java)
        } catch (e: Exception) {
            Log.e(TAG, """${e.message}""", e)
            null
        }
    }

    fun clearProfile() {
        instance.edit().putString(KEY_CURRENT_USER, null).apply()
    }

    fun setCurrentPreferences(temporaryUser: TemporaryUser) {
        instance.edit().putString(KEY_CURRENT_USER_PREFS, Gson().toJson(temporaryUser)).apply()
    }

    fun getCurrentPreferences(): TemporaryUser {
        val stringPrefs = instance.getString(KEY_CURRENT_USER_PREFS, null)
        if (stringPrefs == null || stringPrefs.isEmpty()) {
            return instantiateCurrentPrefs()
        }
        return try {
            Gson().fromJson(stringPrefs, TemporaryUser::class.java)
        } catch (e: Exception) {
            return instantiateCurrentPrefs()
        }
    }

    private fun instantiateCurrentPrefs(): TemporaryUser {
        val temporaryUser = TemporaryUser(null, null, null)
        setCurrentPreferences(temporaryUser)
        return temporaryUser
    }

    fun clearPrefs() {
        instance.edit().putString(KEY_CURRENT_USER_PREFS, null).apply()
    }
}
