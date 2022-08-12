package tk.kvakva.collectgpspoints

import android.util.Log
import org.junit.Test

import org.junit.Assert.*
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun what_the_fuck() {
        val i = Instant.now()
        println("i.isSupported(ChronoUnit.HOURS) = ${i.isSupported(ChronoUnit.HOURS)}")
        System.err.println("i.isSupported(ChronoUnit.HOURS) = ${i.isSupported(ChronoUnit.HOURS)} ")
        System.err.println("i.isSupported(ChronoUnit.DAYS) = ${i.isSupported(ChronoUnit.DAYS)} ")
        System.err.println("i.isSupported(ChronoUnit.YEARS) = ${i.isSupported(ChronoUnit.YEARS)} ")
        println()
        System.err.println("i.isSupported(ChronoField.MILLI_OF_SECOND) = ${i.isSupported(ChronoField.MILLI_OF_SECOND)} ")
        System.err.println("i.isSupported(ChronoField.INSTANT_SECONDS) = ${i.isSupported(ChronoField.INSTANT_SECONDS)} ")
        System.err.println("i.isSupported(ChronoField.HOUR) = ${i.isSupported(ChronoField.HOUR_OF_DAY)} ")
        System.err.println("i.isSupported(ChronoField.DAY_OF_MONTH) = ${i.isSupported(ChronoField.DAY_OF_MONTH)} ")
        System.err.println("i.isSupported(ChronoField.YEARS) = ${i.isSupported(ChronoField.YEAR)} ")
        System.err.println("i = ${java.time.format.DateTimeFormatter.ofPattern("YYYY-MM-dd hh:mm:ss").withZone(/*ZoneOffset.UTC*/ ZoneId.systemDefault()).format(i)} ")


    }
}