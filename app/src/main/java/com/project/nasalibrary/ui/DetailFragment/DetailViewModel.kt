package com.project.nasalibrary.ui.detailFragment

import androidx.lifecycle.ViewModel


class DetailViewModel : ViewModel() {

    fun correctDateFormat(originalDateText : String ):String
    {
        val indexOfT = originalDateText.indexOf("T")
        val correctedDate = originalDateText.substring(0, indexOfT)
        return  correctedDate
    }
}