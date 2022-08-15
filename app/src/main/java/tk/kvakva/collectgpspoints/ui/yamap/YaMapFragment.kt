package tk.kvakva.collectgpspoints.ui.yamap

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.*
import tk.kvakva.collectgpspoints.GeoPoint
import tk.kvakva.collectgpspoints.R
import tk.kvakva.collectgpspoints.databinding.FragmentMapYaBinding


private const val TAG = "GalleryFragment"

class YaMapFragment : Fragment() {

    private val yaMapViewModel by navGraphViewModels<YaMapViewModel>(R.id.mobile_navigation)

    private val spinnerAdapter by lazy {
        Log.e(TAG, "lazy spinner addapter: ${yaMapViewModel.dateList.value?.toMutableList()}", )
        ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item,
            android.R.id.text1,
            yaMapViewModel.dateList.value?.toMutableList() ?: mutableListOf()
        )
    }

    val geoObjectTapListener = GeoObjectTapListener { event: GeoObjectTapEvent ->
        val selectionMetadata = event.geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)

        if (selectionMetadata != null) {
            binding.mapview.map.selectGeoObject(selectionMetadata.id, selectionMetadata.layerId)
        }
        true
    }

    val mapObjectTapListener: MapObjectTapListener =
        MapObjectTapListener { mapObject: MapObject, point: Point ->

            true
        }

    private var _binding: FragmentMapYaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var polylineMapObject: PolylineMapObject? = null
    private var placeMarkMapObject: PlacemarkMapObject? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        MapKitFactory.initialize(context)


        //val yaMapViewModel =
        //    ViewModelProvider(this)[YaMapViewModel::class.java]

        _binding = FragmentMapYaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.mapview.map.run {
            move(
                CameraPosition(
                    Point(55.751574, 37.573856),
                    11.0f,
                    0.0f,
                    0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0f),
                null
            )
            isIndoorEnabled = true
//            isNightModeEnabled = true


//            addIndoorStateListener(
//                object : IndoorStateListener {
//                    override fun onActivePlanFocused(p0: IndoorPlan) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onActivePlanLeft() {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onActiveLevelChanged(p0: String) {
//                        TODO("Not yet implemented")
//                    }
//
//                }
//            )
        }
//        TODO: place it in onClick()
//        MapKitFactory.getInstance().createUserLocationLayer(binding.mapview.mapWindow).apply {
//
//            isVisible=true
//            isHeadingEnabled=true
//
//        }

//        lifecycleScope.launch(Dispatchers.IO) {
//            GeoPointRoomDatabase.getDatabase(requireContext().applicationContext).geoPointDao()
//                .getAll().forEach {
//                    //galleryViewModel.points.value?.forEach {
//                    Log.e(TAG, "onCreateView: $it")
//                    requireActivity().runOnUiThread {
//                        binding.mapview.map.mapObjects.addPlacemark(
//                            Point(it.lat, it.lon)
//                        )
//                    }
//                }
//        }
//        binding.mapview.map.mapObjects.addPolyline(
//            Polyline(
//                galleryViewModel.listGeoPointsFromRep.value?.map {
//                    Point(it.lat, it.lon)
//                }?.toMutableList() ?: listOf()
//            )
//        )

        //yaMapViewModel.listGeoPointsFromRep.observe(viewLifecycleOwner) { listGeoPoint: List<GeoPoint> ->
        yaMapViewModel.listOfGeoPointsAtTheDate.observe(viewLifecycleOwner) { listGeoPoint: List<GeoPoint> ->
            Log.e(TAG, "^^^^^observer yaMapViewModel.listGeoPointsFromRe: $listGeoPoint selectedDate: ${yaMapViewModel.dateOfDay.value}")
            val pList = listGeoPoint.map {
                Point(it.lat, it.lon)
            }

            if (pList.isNotEmpty())
                if(placeMarkMapObject!=null)
                   placeMarkMapObject?.let { binding.mapview.map.mapObjects.remove(it) }
                placeMarkMapObject=binding.mapview.map.mapObjects.addPlacemark(
                    pList.last(),
//                ViewProvider(
//                    TextView(requireContext()).apply {
//                        text = "oiuoiu oiuoiu"
//                    }
//                )
                    //com.yandex.runtime.image.ImageProvider.fromResource(
                    //    requireContext(),
                    //    R.drawable.photo
                    //),
                    //IconStyle().setAnchor(PointF(0f, 1f))
                )
            //binding.mapview.map.mapObjects.addPolyline(
            //    Polyline(
            polylineMapObject?.let {
                binding.mapview.map.mapObjects.remove(it)
            }

            polylineMapObject = binding.mapview.map.mapObjects.addPolyline(
                Polyline(
                    listGeoPoint.map {
                        Point(it.lat, it.lon)
                    }
                )
            )
            polylineMapObject?.setStrokeColor(Color.RED)
            //polylineMapObject?.outlineWidth=1f
            polylineMapObject?.strokeWidth = 1f
            polylineMapObject?.dashLength = 8f
            polylineMapObject?.gapLength = 4f
//            polylineMapObject?.addTapListener { mapObject, point ->
//                true
//            }


            //    )
            //)
        }
        //binding.mapview.map.mapObjects.addPlacemark(
        //    Point(55.751574, 37.573856)
        //)

        //val textView: TextView = binding.textGallery
        //galleryViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}

        //binding.mapview.map.addTapListener(geoObjectTapListener)

        binding.dateSpinner.adapter = spinnerAdapter
        binding.dateSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                /**
                 *
                 * Callback method to be invoked when an item in this view has been
                 * selected. This callback is invoked only when the newly selected
                 * position is different from the previously selected position or if
                 * there was no selected item.
                 *
                 * Implementers can call getItemAtPosition(position) if they need to access the
                 * data associated with the selected item.
                 *
                 * @param parent The AdapterView where the selection happened
                 * @param view The view within the AdapterView that was clicked
                 * @param position The position of the view in the adapter
                 * @param id The row id of the item that is selected
                 */
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.e(TAG, "onItemSelected: posision=$position, id=$id\n----\n")
                    view?.let {
                        Log.e(TAG, "onItemSelected: view.text=${(view as TextView).text}")
                        yaMapViewModel.setDate(view.text.toString())
                    }
                }

                /**
                 * Callback method to be invoked when the selection disappears from this
                 * view. The selection can disappear for instance when touch is activated
                 * or when the adapter becomes empty.
                 *
                 * @param parent The AdapterView that now contains no selected item.
                 */
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

        yaMapViewModel.dateList.observe(viewLifecycleOwner) {
            Log.e(TAG, "DATA LIST: ${it.toList()}", )
            spinnerAdapter.clear()
            spinnerAdapter.addAll(it.toList())
            spinnerAdapter.notifyDataSetChanged()
            binding.dateSpinner.setSelection(spinnerAdapter.count-1)
        }

        return root
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to #Activity.onStop] of the containing
     * Activity's lifecycle.
     */
    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to #Activity.onStart] of the containing
     * Activity's lifecycle.
     */
    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}