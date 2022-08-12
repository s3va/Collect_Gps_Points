package tk.kvakva.collectgpspoints.ui.home

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import tk.kvakva.collectgpspoints.GeoPointCollectServ
import tk.kvakva.collectgpspoints.R
import tk.kvakva.collectgpspoints.databinding.FragmentHomeBinding

private const val TAG = "HomeFragment"
class HomeFragment : Fragment() {

    //private val _homeViewModel by navGraphViewModels<HomeViewModel>(R.id.mobile_navigation)

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        //binding.fab.setMaxImageSize(110)
        //Log.e(TAG, "onCreateView: fab height = ${binding.fab.height} fab width = ${binding.fab.width} fab size = ${binding.fab.height}" )
        //binding.fab.setMaxImageSize(
        //    binding.fab.height-4
        //)

        binding.fab.elevation = 5f
        GeoPointCollectServ.geoPointCollectServ.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.fab.setImageResource(R.drawable.ic_baseline_not_started_24)
                binding.fab.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                binding.fab.scaleType = ImageView.ScaleType.FIT_XY
//                binding.appBarMain.fab.setMaxImageSize(110)
//                binding.appBarMain.fab.elevation = 5f
                //binding.appBarMain.fab.imageTintList=ColorStateList.valueOf(Color.GREEN)
                Log.e(TAG, "onCreate: geoPointCollectionServ if null: $it")
            } else {
                binding.fab.setImageResource(R.drawable.ic_baseline_stop_24)
                binding.fab.backgroundTintList = ColorStateList.valueOf(Color.RED)
                //binding.appBarMain.fab.imageTintList=ColorStateList.valueOf(Color.RED)


                Log.e(TAG, "onCreate: geoPointCollectioServ if NOT NULL: $it")
            }
        }
        binding.fab.setOnClickListener { view ->
            if (GeoPointCollectServ.geoPointCollectServ.value == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireActivity().startForegroundService(Intent(requireContext(), GeoPointCollectServ::class.java))
                } else {
                    requireActivity().startService(Intent(requireContext(), GeoPointCollectServ::class.java))
                }
            } else {
                requireActivity().stopService(Intent(requireContext(), GeoPointCollectServ::class.java))
            }
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
