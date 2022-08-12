package tk.kvakva.collectgpspoints.ui.yamap

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import tk.kvakva.collectgpspoints.GeoPoint
import tk.kvakva.collectgpspoints.GeoPointRepository
import tk.kvakva.collectgpspoints.GeoPointRoomDatabase

class YaMapViewModel(val app: Application) : AndroidViewModel(app) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is yamap Fragment"
    }
    val text: LiveData<String> = _text
    val geoPointRepository: GeoPointRepository =
        GeoPointRepository(GeoPointRoomDatabase.getDatabase(app.applicationContext).geoPointDao())

    //val points: LiveData<List<GeoPoint>> = GeoPointRoomDatabase.getDatabase(app.applicationContext).geoPointDao().getAllLd()
    val listGeoPointsFromRep =
        geoPointRepository.allGeoPointEntries.asLiveData()

    val dateList: LiveData<Array<String>> =
        geoPointRepository.getAllDates.asLiveData()

    val dateOfDay: MutableLiveData<String> = MutableLiveData()
    val listOfGeoPointsAtTheDate: LiveData<List<GeoPoint>> =
        Transformations.switchMap(dateOfDay){
            geoPointRepository.loadAllInThisDay(it).asLiveData()
        }
        //geoPointRepository.loadAllInThisDay.asLiveData()

    fun setDate(date: String){
        if(date.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")))
            dateOfDay.value=date
        else
            Log.e(TAG, "setDate: date ($date) not match XXXX-XX-XX" )
    }



}

private const val TAG = "YaMapViewModel"