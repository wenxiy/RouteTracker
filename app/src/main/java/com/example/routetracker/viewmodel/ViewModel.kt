package com.example.routetracker.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class TrackingViewModel : ViewModel() {
    private val _pathPoints = MutableLiveData<List<LatLng>>(mutableListOf())
    val pathPoints: MutableLiveData<List<LatLng>> = _pathPoints

    fun addPoint(point: LatLng) {
        val currentList = _pathPoints.value?.toMutableList()?: mutableListOf()
        currentList.add(point)
        _pathPoints.value = currentList
    }

    fun clearPath() {
        _pathPoints.value = mutableListOf()
    }

    fun setPath(path: List<LatLng>) {
        _pathPoints.value = path.toMutableList()
    }
}