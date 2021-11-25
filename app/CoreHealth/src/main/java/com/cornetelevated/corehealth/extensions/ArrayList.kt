package com.cornetelevated.corehealth.extensions

fun <T> ArrayList<T>.squeeze(maxSize: Int): ArrayList<T> {
    return if (this.size > maxSize) {
        val temp = this
        temp.reverse()
        val drawable: ArrayList<T> = ArrayList(temp.subList(0, (maxSize - 1)))
        drawable.reverse()
        drawable
    } else {
        this
    }
}