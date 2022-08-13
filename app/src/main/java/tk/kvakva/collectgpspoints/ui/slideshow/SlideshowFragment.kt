package tk.kvakva.collectgpspoints.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.observeOn
import tk.kvakva.collectgpspoints.GeoPoint
import tk.kvakva.collectgpspoints.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        val adapter = PointsListRecViewAdapt(slideshowViewModel.listGeoPointsFromRep.value?.toTypedArray()?: arrayOf())
        binding.pointsRecView.adapter = adapter
//            PointsListRecViewAdapt(
//            arrayOf(
//                GeoPoint(1, "1234-12-23 12:12:12", 55.123121, 33.234231),
//                GeoPoint(2, "1235-01-24 13:13:12", 55.123122, 33.234232),
//                GeoPoint(3, "1236-02-25 14:14:12", 55.123123, 33.234233),
//                GeoPoint(4, "1237-03-26 15:15:12", 55.123124, 33.234234),
//                GeoPoint(5, "1238-04-27 16:15:12", 55.1232, 33.234),
//                GeoPoint(6, "1239-04-27 16:15:12", 5.132, 33.34),
//                GeoPoint(7, "1240-04-27 16:15:12", 5.13, -33.3),
//                GeoPoint(7, "1241-05-28 16:15:12", 5.1, -3.3),
//            )
        //)
        binding.pointsRecView.addItemDecoration(
            DividerItemDecoration(
                binding.pointsRecView.context,
                LinearLayoutManager.VERTICAL
            )
        )
        slideshowViewModel.listGeoPointsFromRep.observe(viewLifecycleOwner){
            adapter.dataSet = it.toTypedArray()
            adapter.notifyDataSetChanged()
            binding.pointsRecView.scrollToPosition(it.size-1)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}