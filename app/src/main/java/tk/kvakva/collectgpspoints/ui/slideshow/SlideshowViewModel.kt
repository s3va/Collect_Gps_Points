package tk.kvakva.collectgpspoints.ui.slideshow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import tk.kvakva.collectgpspoints.GeoPointRepository
import tk.kvakva.collectgpspoints.GeoPointRoomDatabase

class SlideshowViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text


    val listGeoPoints = GeoPointRoomDatabase.getDatabase(application).geoPointDao().getAllFl()
    val listGeoPointsFromRep =
        GeoPointRepository(
            GeoPointRoomDatabase.getDatabase(application.applicationContext).geoPointDao()
        ).allGeoPointEntries.asLiveData()

}