package com.example.lab_week_10.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab_week_10.database.TotalObject

class TotalViewModel: ViewModel() {
    //Declare the LiveData object
    private val _total = MutableLiveData<TotalObject>()
    val total: LiveData<TotalObject> = _total

    //Initialize the LiveData object
    init {
        _total.postValue(TotalObject(0, ""))
    }

    //Increment the total value
    fun incrementTotal() {
        val currentValue = _total.value?.value ?: 0
        val newDate = java.util.Date().toString()
        _total.postValue(TotalObject(currentValue + 1, newDate))
    }

    //Set new total value
    fun setTotal(totalObject: TotalObject) {
        _total.postValue(totalObject)
    }
}