package tk.kvakva.collectgpspoints

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import kotlinx.coroutines.flow.Flow

@Entity
data class GeoPoint(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP") val smartDateTime: String,
    val lat: Double,
    val lon: Double,
    val uploaded: Boolean = false,
    val user: String? = null,
    val gpsDateTime: String? = null,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val speedAccuracy: Float? = null
)

@Dao
interface GeoPointDao {
    @Query("SELECT * FROM geopoint")
    fun getAll(): List<GeoPoint>

    @Query("SELECT * FROM geopoint ORDER BY id DESC LIMIT :n")
    fun getLastNs(n: Int): List<GeoPoint>

    @Query("SELECT * FROM geopoint")
    fun getAllFl(): Flow<List<GeoPoint>>

    @Query("SELECT * FROM geopoint")
    fun getAllLd(): LiveData<List<GeoPoint>>

/*    @Query("SELECT * FROM connectslogentry")
    fun getAllStFl(): StateFlow<List<ConnectsLogEntry>>
    @Query("SELECT * FROM connectslogentry")
    fun getAllShFl(): SharedFlow<List<ConnectsLogEntry>>*/

    @Query("SELECT * FROM geopoint WHERE user LIKE :u")
    fun loadAllByUserIP(u: String): List<GeoPoint>

    @Query("SELECT * FROM geopoint WHERE smartDateTime BETWEEN :startDateTime AND :endDateTime")
    fun loadAllBetweenTwoDateTimes(
        startDateTime: String,
        endDateTime: String
    ): List<GeoPoint>

    @Insert
    suspend fun insertAll(vararg geoPointEntries: GeoPoint)

    @Delete
    fun delete(geoPointEntry: GeoPoint)

    @Query("select distinct date(smartDateTime) from geopoint")
    fun getAllDates(): Flow<Array<String>>

    @Query("select * from geopoint where date(smartDateTime) = :day")
    fun loadAllInThisDay(
        day: String
    ): Flow<List<GeoPoint>>

}

@Database(
    entities = [GeoPoint::class],
    version = 11,
    autoMigrations = [
        AutoMigration(
            from = 8,
            to = 9,
            spec = GeoPointRoomDatabase.MyGeoDbAutoMigration8To9::class
        ),
        AutoMigration (
            from = 9,
            to = 10
        ),
        AutoMigration (
            from = 10,
            to = 11
        ),
    ],
    exportSchema = true
)
abstract class GeoPointRoomDatabase : RoomDatabase() {
    abstract fun geoPointDao(): GeoPointDao

    @RenameColumn(tableName = "GeoPoint", fromColumnName = "datetime", toColumnName = "smartDateTime")
    class MyGeoDbAutoMigration8To9: AutoMigrationSpec

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: GeoPointRoomDatabase? = null

        fun getDatabase(context: Context): GeoPointRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GeoPointRoomDatabase::class.java,
                    "geo_points_database",
                )
                    .enableMultiInstanceInvalidation()
                    //.fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class GeoPointRepository(private val geoPointDao: GeoPointDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allGeoPointEntries: Flow<List<GeoPoint>> = geoPointDao.getAllFl()
    val getAllDates: Flow<Array<String>> = geoPointDao.getAllDates()

    suspend fun insertAll(vararg geoPointEntries: GeoPoint) {
        geoPointDao.insertAll(*geoPointEntries)
    }

    fun loadAllInThisDay(d: String): Flow<List<GeoPoint>> {
        return geoPointDao.loadAllInThisDay(d)
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
//    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(geoPointEntry: GeoPoint) {
        geoPointDao.insertAll(geoPointEntry)
    }
}
