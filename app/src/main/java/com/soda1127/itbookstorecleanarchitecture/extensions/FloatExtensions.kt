package com.soda1127.itbookstorecleanarchitecture.extensions

import android.content.res.Resources

fun Float.fromDpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}
