package com.ubcoin.model.response.profile

import java.io.Serializable

/**
 * Created by Yuriy Aizenberg
 */
class Wallet(
        val number: Long?,
        val amountUBC: Double?,
        val amountOnHold: Double?
) : Serializable