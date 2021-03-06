package com.ubcoin.network

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created by Yuriy Aizenberg
 */
class NullOrEmptyConvertFactory : Converter.Factory() {

    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        val delegate = retrofit!!.nextResponseBodyConverter<Any>(this, type!!, annotations!!)
        return Converter<ResponseBody, Any> {
            if (it.contentLength() != 0L && !hasEliminatedAnnotation(annotations))
                delegate.convert(it)
            else
                null
        }
    }

    private fun hasEliminatedAnnotation(annotations: Array<Annotation>?): Boolean {
        annotations?.forEach {
            if (it.annotationClass == EliminatedBody::class) return true
        }
        return false
    }

}