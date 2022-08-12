package tk.kvakva.collectgpspoints

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.NavHostFragment
import tk.kvakva.collectgpspoints.databinding.ActivityMainBinding

private const val TAG = "MainActivity"


class MainActivity : AppCompatActivity() {
    private val _locationPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )


    var _menu: Menu? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val backgroundLocationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        Log.e(TAG, "backgroundLocationPermissionRequest: $it")
    }
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Log.e(TAG, "-------------------: ")
        for ((t, u) in permissions) {
            Log.e(TAG, "$t -> $u: ")
        }
        Log.e(TAG, "-------------------: ")
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Log.e(TAG, " // Precise location access granted.")
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Log.e(TAG, " // Only approximate location access granted.")
            }
            else -> {
                Log.e(TAG, " // No location access granted.")
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (allPermissionsGranted(permissions.keys.toTypedArray())) {
                backgroundLocationPermissionRequest.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(TAG, "onCreate: ${BuildConfig.YaMapkitKey}")
        if (!allPermissionsGranted(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    _locationPermissions.plus(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                } else {
                    _locationPermissions
                }
            )
        )
        //requestPermissions(_location_permissions,666)
            locationPermissionRequest.launch(_locationPermissions)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
//        binding.appBarMain.fab.setMaxImageSize(110)
//        binding.appBarMain.fab.elevation = 5f
//        GeoPointCollectServ.geoPointCollectServ.observe(this) {
//            if (it == null) {
//                binding.appBarMain.fab.setImageResource(R.drawable.ic_baseline_not_started_24)
//                binding.appBarMain.fab.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
//                binding.appBarMain.fab.scaleType = ImageView.ScaleType.FIT_XY
////                binding.appBarMain.fab.setMaxImageSize(110)
////                binding.appBarMain.fab.elevation = 5f
//                //binding.appBarMain.fab.imageTintList=ColorStateList.valueOf(Color.GREEN)
//                Log.e(TAG, "onCreate: geoPointCollectionServ if null: $it")
//            } else {
//                binding.appBarMain.fab.setImageResource(R.drawable.ic_baseline_stop_24)
//                binding.appBarMain.fab.backgroundTintList = ColorStateList.valueOf(Color.RED)
//                //binding.appBarMain.fab.imageTintList=ColorStateList.valueOf(Color.RED)
//
//
//                Log.e(TAG, "onCreate: geoPointCollectioServ if NOT NULL: $it")
//            }
//        }
//        binding.appBarMain.fab.setOnClickListener { view ->
//            if (GeoPointCollectServ.geoPointCollectServ.value == null) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    startForegroundService(Intent(this, GeoPointCollectServ::class.java))
//                } else {
//                    startService(Intent(this, GeoPointCollectServ::class.java))
//                }
//            } else {
//                stopService(Intent(this, GeoPointCollectServ::class.java))
//            }
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        //val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment).navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_yamap, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        GeoPointCollectServ.geoPointCollectServ.observe(this) {
            _menu?.findItem(R.id.action_service)?.icon = ResourcesCompat.getDrawable(
                resources,
                if (it == null)
                    R.drawable.ic_baseline_not_started_24
                else
                    R.drawable.ic_baseline_stop_24,
                null
            )
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        _menu = menu
        _menu?.findItem(R.id.action_service)?.icon = ResourcesCompat.getDrawable(
            resources,
            if (GeoPointCollectServ.geoPointCollectServ.value == null)
                R.drawable.ic_baseline_not_started_24
            else
                R.drawable.ic_baseline_stop_24,
            null
        )
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun allPermissionsGranted(p: Array<String>) = p.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun actionStartService(item: MenuItem) {
        if (item.itemId == R.id.action_service) {
            if (GeoPointCollectServ.geoPointCollectServ.value == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(
                        Intent(
                            applicationContext,
                            GeoPointCollectServ::class.java
                        )
                    )
                } else {
                    startService(Intent(applicationContext, GeoPointCollectServ::class.java))
                }
            } else {
                stopService(Intent(applicationContext, GeoPointCollectServ::class.java))
            }
        }
    }
}

