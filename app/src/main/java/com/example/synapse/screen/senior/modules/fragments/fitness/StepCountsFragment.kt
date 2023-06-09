package com.example.synapse.screen.senior.modules.fragments.fitness

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.synapse.R
import com.example.synapse.screen.senior.modules.fragments.HomeFragment
import com.example.synapse.screen.util.ReplaceFragment
import com.example.synapse.screen.util.adapter.fitness.StepCountsForTheDayAdapter
import com.example.synapse.screen.util.adapter.Steps
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StepCountsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StepCountsFragment : Fragment() {

    var replaceFragment = ReplaceFragment()

    lateinit var tvDate: TextView
    lateinit var btnBack: ImageButton

    lateinit var toggleGroup: MaterialButtonToggleGroup
    lateinit var btnDay: Button
    lateinit var btnWeek: Button
    lateinit var btnMonth: Button

    /* variables for step counts and heart rate */
    private lateinit var tvStepsCount : TextView
    private lateinit var tvEntriesStepsCounts : TextView
    private lateinit var date1 : Date
    private lateinit var date2 : Date
    private var steps_time_range: Long = 0
    private var steps: Long = 0
    private lateinit var end_time_step : Instant
    private lateinit var start_time_step : Instant
    private lateinit var newRecycleView : RecyclerView
    private lateinit var newStepsList : ArrayList<Steps>

    /** Shows a list of all steps based on time range (STEPS FOR THE DAY) */
    suspend fun readStepByTimeRangeDay(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ){
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
        for(stepRecord in response.records){

            steps = stepRecord.count
            start_time_step = stepRecord.startTime
            end_time_step = stepRecord.endTime

            date1 = Date.from(start_time_step)
            date2 = Date.from(end_time_step)

            val formatter = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            val formattedDate1 = formatter.format(date1)
            val formattedDate2 = formatter.format(date2)

            newStepsList.add(Steps("$formattedDate1 - $formattedDate2", steps.toString() + " steps"))
        }
        newRecycleView.post {newRecycleView.adapter = StepCountsForTheDayAdapter(newStepsList) }
        tvEntriesStepsCounts.post {tvEntriesStepsCounts.text = newStepsList.count().toString() + " entries"}
    }

    /** Shows a list of all steps based on time range (STEPS FOR THE WEEK) */
    suspend fun readStepByTimeRangeWeek(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ){
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
        for(stepRecord in response.records){

            steps = stepRecord.count
            start_time_step = stepRecord.startTime
            end_time_step = stepRecord.endTime

            date1 = Date.from(start_time_step)
            date2 = Date.from(end_time_step)

            val formatter = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            val formattedDate1 = formatter.format(date1)
            val formattedDate2 = formatter.format(date2)

            newStepsList.add(Steps("$formattedDate1 - $formattedDate2", steps.toString() + " steps"))
        }
        newRecycleView.post {newRecycleView.adapter = StepCountsForTheDayAdapter(newStepsList) }
        tvEntriesStepsCounts.post {tvEntriesStepsCounts.text = newStepsList.count().toString() + " entries"}
    }

    /** Shows a list of all steps based on time range (STEPS FOR THE Month) */
    suspend fun readStepByTimeRangeMonth(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ){
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
        for(stepRecord in response.records){

            steps = stepRecord.count
            start_time_step = stepRecord.startTime
            end_time_step = stepRecord.endTime

            date1 = Date.from(start_time_step)
            date2 = Date.from(end_time_step)

            val formatter = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            val formattedDate1 = formatter.format(date1)
            val formattedDate2 = formatter.format(date2)

            newStepsList.add(Steps("$formattedDate1 - $formattedDate2", steps.toString() + " steps"))
        }
        newRecycleView.post {newRecycleView.adapter = StepCountsForTheDayAdapter(newStepsList) }
        tvEntriesStepsCounts.post {tvEntriesStepsCounts.text = newStepsList.count().toString() + " entries"}
    }

    /** Shows show the total of steps (only session metadata) for the current day 1 */
    suspend fun aggregateStepsIntoDays(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )
        for (monthlyResult in response) {
            steps_time_range += monthlyResult.result[StepsRecord.COUNT_TOTAL]!!
        }
        tvStepsCount.post {tvStepsCount.text = steps_time_range.toString()}
    }

    /** Shows show the total of steps (only session metadata) for the current week 1 */
    suspend fun aggregateStepsIntoWeeks(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofWeeks(1)
                )
            )
        for (monthlyResult in response) {
            steps_time_range += monthlyResult.result[StepsRecord.COUNT_TOTAL]!!
        }
        tvStepsCount.post {tvStepsCount.text = steps_time_range.toString()}
    }

    /** Shows show the total of steps (only session metadata) for the current month 1 */
    suspend fun aggregateStepsIntoMonth(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofMonths(1)
                )
            )
        for (monthlyResult in response) {
            steps_time_range += monthlyResult.result[StepsRecord.COUNT_TOTAL]!!
        }
        tvStepsCount.post {tvStepsCount.text = steps_time_range.toString()}
    }
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_step_counts, container, false)

        val healthConnectClient = HealthConnectClient.getOrCreate(requireContext())

        tvStepsCount = view.findViewById(R.id.tvStepCounts)
        tvEntriesStepsCounts = view.findViewById(R.id.tvCountEntries)
        tvDate = view.findViewById(R.id.tvDate)
        btnBack = view.findViewById(R.id.ibBack)
        toggleGroup = view.findViewById(R.id.toggleButtonGroup)
        btnDay = view.findViewById(R.id.btnDay)
        btnWeek = view.findViewById(R.id.btnWeek)

        newRecycleView = view.findViewById(R.id.recycleViewStepsForTheDay)
        newRecycleView.layoutManager = LinearLayoutManager(requireContext())
        newRecycleView.setHasFixedSize(true)
        newStepsList = arrayListOf<Steps>()

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
        GlobalScope.launch { aggregateStepsIntoDays(healthConnectClient, LocalDateTime.of(localeToday, dawn),LocalDateTime.now()) }
        GlobalScope.launch { readStepByTimeRangeDay(healthConnectClient, LocalDateTime.of(localeToday, dawn), LocalDateTime.now()) }

        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if(isChecked){
                when(checkedId){
                    R.id.btnDay -> {
                        newStepsList.clear()
                        steps_time_range = 0
                        newRecycleView.adapter?.notifyDataSetChanged()
                        tvDate.text = formattedDate1.toString()
                        GlobalScope.launch { aggregateStepsIntoDays(healthConnectClient, LocalDateTime.of(localeToday,dawn), LocalDateTime.now()) }
                        GlobalScope.launch { readStepByTimeRangeDay(healthConnectClient, LocalDateTime.of(localeToday, dawn), LocalDateTime.now()) }
                    }
                    R.id.btnWeek -> {
                        newStepsList.clear()
                        steps_time_range = 0
                        newRecycleView.adapter?.notifyDataSetChanged()
                        tvDate.text = formattedDate3 + " - " + formattedDate2
                        GlobalScope.launch { aggregateStepsIntoWeeks(healthConnectClient, LocalDateTime.now().minusWeeks(1), LocalDateTime.now()) }
                        GlobalScope.launch { readStepByTimeRangeWeek(healthConnectClient, LocalDateTime.now().minusWeeks(1), LocalDateTime.now()) }
                    }
                    R.id.btnMonth -> {
                        newStepsList.clear()
                        steps_time_range = 0
                        newRecycleView.adapter?.notifyDataSetChanged()
                        tvDate.text = month
                        GlobalScope.launch { aggregateStepsIntoMonth(healthConnectClient, LocalDateTime.now().minusMonths(1), LocalDateTime.now()) }
                        GlobalScope.launch { readStepByTimeRangeMonth(healthConnectClient, LocalDateTime.now().minusMonths(1), LocalDateTime.now()) }
                    }
                }
            }
        }

        btnBack.setOnClickListener{v: View? ->
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
         * @return A new instance of fragment StepCountsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StepCountsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}