package com.example.synapse.screen.senior.modules.fragments.fitness

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityTinnRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.synapse.R
import com.example.synapse.screen.senior.modules.fragments.HomeFragment
import com.example.synapse.screen.util.ReplaceFragment
import com.example.synapse.screen.util.adapter.HeartRates
import com.example.synapse.screen.util.adapter.Steps
import com.example.synapse.screen.util.adapter.fitness.HeartRateForTheDayAdapter
import com.example.synapse.screen.util.adapter.fitness.StepCountsForTheDayAdapter
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HeartRateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HeartRateFragment : Fragment() {

    var replaceFragment = ReplaceFragment()
    lateinit var tvDate: TextView
    lateinit var btnBack: ImageButton
    lateinit var toggleGroup: MaterialButtonToggleGroup
    lateinit var btnDay: Button
    lateinit var btnWeek: Button
    lateinit var btnMonth: Button

    /* variables for step counts and heart rate */
    private lateinit var tvHeartRateMinMax: TextView
    private lateinit var tvEntriesHeartsCounts: TextView
    private lateinit var date1: Date
    private lateinit var date2: Date
    private var beatsPerMinutes: Long = 0
    private lateinit var end_time_heart: Instant
    private lateinit var start_time_heart: Instant
    private lateinit var newRecycleView: RecyclerView
    private lateinit var newHeartsList: ArrayList<HeartRates>

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    /** Shows a list of all steps based on time range (STEPS FOR THE DAY) */
    suspend fun readHeartRatesByTimeRangeDay(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ){
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
        for(heartRecord in response.records){
            val series = heartRecord.samples

            series.forEach{
                serie ->
                beatsPerMinutes = serie.beatsPerMinute
            }

           start_time_heart = heartRecord.startTime
           end_time_heart = heartRecord.endTime
           date1 = Date.from(start_time_heart)
           date2 = Date.from(end_time_heart)

           val formatter = SimpleDateFormat("h:mm a", Locale.ENGLISH)
           val formattedDate1 = formatter.format(date1)
           val formattedDate2 = formatter.format(date2)

           newHeartsList.add(HeartRates("$formattedDate1 - $formattedDate2", beatsPerMinutes.toString()))

        }
        newRecycleView.post {newRecycleView.adapter = HeartRateForTheDayAdapter(newHeartsList) }
        tvEntriesHeartsCounts.post {tvEntriesHeartsCounts.text = newHeartsList.count().toString() + " entries"}
    }

    /** Shows a list of all steps based on time range (STEPS FOR THE WEEK) */
    suspend fun readHeartRatesByTimeRangeWeek(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ){
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
        for(heartRecord in response.records){
            val series = heartRecord.samples

            series.forEach{
                    serie ->
                beatsPerMinutes = serie.beatsPerMinute
            }

            start_time_heart = heartRecord.startTime
            end_time_heart = heartRecord.endTime
            date1 = Date.from(start_time_heart)
            date2 = Date.from(end_time_heart)

            val formatter = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            val formattedDate1 = formatter.format(date1)
            val formattedDate2 = formatter.format(date2)

            newHeartsList.add(HeartRates("$formattedDate1 - $formattedDate2", beatsPerMinutes.toString()))

        }
        newRecycleView.post {newRecycleView.adapter = HeartRateForTheDayAdapter(newHeartsList) }
        tvEntriesHeartsCounts.post {tvEntriesHeartsCounts.text = newHeartsList.count().toString() + " entries"}
    }

    /** Shows a list of all steps based on time range (STEPS FOR THE WEEK) */
    suspend fun readHeartRatesByTimeRangeMonth(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ){
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
        for(heartRecord in response.records){
            val series = heartRecord.samples

            series.forEach{
                    serie ->
                beatsPerMinutes = serie.beatsPerMinute
            }

            start_time_heart = heartRecord.startTime
            end_time_heart = heartRecord.endTime
            date1 = Date.from(start_time_heart)
            date2 = Date.from(end_time_heart)

            val formatter = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            val formattedDate1 = formatter.format(date1)
            val formattedDate2 = formatter.format(date2)

            newHeartsList.add(HeartRates("$formattedDate1 - $formattedDate2", beatsPerMinutes.toString()))

        }
        newRecycleView.post {newRecycleView.adapter = HeartRateForTheDayAdapter(newHeartsList) }
        tvEntriesHeartsCounts.post {tvEntriesHeartsCounts.text = newHeartsList.count().toString() + " entries"}
    }

    /** shows MIN and MAX HeartRate for the time range */
    suspend fun aggregateHeartRate(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ){
        val response =
            healthConnectClient.aggregate(
                AggregateRequest(
                    metrics =  setOf(HeartRateRecord.BPM_MAX, HeartRateRecord.BPM_MIN),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
        val minimumHeartRate = response[HeartRateRecord.BPM_MIN]
        val maximumHeartRate = response[HeartRateRecord.BPM_MAX]

        tvHeartRateMinMax.post {tvHeartRateMinMax.text = minimumHeartRate.toString() + " - " + maximumHeartRate.toString()}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_heart_rate, container, false)

        val healthConnectClient = HealthConnectClient.getOrCreate(requireContext())
        toggleGroup = view.findViewById(R.id.toggleButtonGroup)
        tvEntriesHeartsCounts = view.findViewById(R.id.tvHeartRateEnEntries)
        tvHeartRateMinMax = view.findViewById(R.id.tvHeartRateMinMax)
        tvDate = view.findViewById(R.id.tvDate)
        btnBack = view.findViewById(R.id.ibBack)
        btnDay = view.findViewById(R.id.btnDay)
        btnWeek = view.findViewById(R.id.btnWeek)
        btnMonth = view.findViewById(R.id.btnMonth)

        newRecycleView = view.findViewById(R.id.recycleViewHeartRateForTheDay)
        newRecycleView.layoutManager = LinearLayoutManager(requireContext())
        newRecycleView.setHasFixedSize(true)
        newHeartsList = arrayListOf<HeartRates>()

        val localeToday = LocalDate.now(ZoneId.of("Asia/Manila"))
        val dawn = LocalTime.MIDNIGHT

        // current date
        val formatter1 = SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH)
        val formattedDate1 = formatter1.format(Date.from(Instant.now()))
        tvDate.text = formattedDate1.toString()

        // startDate and EndDate for the week
        val localDateEndDate = LocalDateTime.now().minusWeeks(1)
        val formatter2 = SimpleDateFormat("MMMM d", Locale.ENGLISH)
        val formatter3 = DateTimeFormatter.ofPattern("MMMM d")
        val formatter4 = SimpleDateFormat("MMMM YYYY", Locale.ENGLISH)
        val formattedDate2 = formatter2.format(Date.from(Instant.now()))
        val formattedDate3 = formatter3.format(localDateEndDate)
        val month = formatter4.format(Date.from(Instant.now()))

        newRecycleView.adapter?.notifyDataSetChanged()
        GlobalScope.launch { readHeartRatesByTimeRangeDay(healthConnectClient, LocalDateTime.of(localeToday, dawn), LocalDateTime.now()) }
        GlobalScope.launch { aggregateHeartRate(healthConnectClient, LocalDateTime.of(localeToday, dawn), LocalDateTime.now()) }

        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if(isChecked){
                when(checkedId){
                    R.id.btnDay -> {
                        newHeartsList.clear()
                        newRecycleView.adapter?.notifyDataSetChanged()
                        tvDate.text = formattedDate1.toString()
                        GlobalScope.launch { aggregateHeartRate(healthConnectClient, LocalDateTime.of(localeToday,dawn), LocalDateTime.now()) }
                        GlobalScope.launch { readHeartRatesByTimeRangeDay(healthConnectClient, LocalDateTime.of(localeToday, dawn), LocalDateTime.now()) }
                    }
                    R.id.btnWeek -> {
                        newHeartsList.clear()
                        newRecycleView.adapter?.notifyDataSetChanged()
                        tvDate.text = formattedDate3 + " - " + formattedDate2
                        GlobalScope.launch { aggregateHeartRate(healthConnectClient, LocalDateTime.now().minusWeeks(1), LocalDateTime.now()) }
                        GlobalScope.launch { readHeartRatesByTimeRangeWeek(healthConnectClient, LocalDateTime.now().minusWeeks(1), LocalDateTime.now()) }
                    }
                    R.id.btnMonth -> {
                        newHeartsList.clear()
                        newRecycleView.adapter?.notifyDataSetChanged()
                        tvDate.text = month
                        GlobalScope.launch { aggregateHeartRate(healthConnectClient, LocalDateTime.now().minusMonths(1), LocalDateTime.now()) }
                        GlobalScope.launch { readHeartRatesByTimeRangeMonth(healthConnectClient, LocalDateTime.now().minusMonths(1), LocalDateTime.now()) }
                    }
                }
            }
        }

        btnBack.setOnClickListener{ v: View? ->
            replaceFragment.replaceFragment(
                HomeFragment(), activity
            )
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HeartRateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HeartRateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


