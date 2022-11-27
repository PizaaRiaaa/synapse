package com.example.synapse.screen.senior

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.changes.DeletionChange
import androidx.health.connect.client.changes.UpsertionChange
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ChangesTokenRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.platform.client.impl.permission.token.PermissionTokenManager
import com.example.synapse.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import java.util.jar.Manifest
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TestFragment : Fragment() {

    lateinit var tvStepsForToday : TextView
    lateinit var tvHeartRateMinMax : TextView
    var steps_for_today: Long = 0

    suspend fun aggregateStepsIntoMonths(
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
            // The result may be null if no data is available in the time range.
            val totalSteps = monthlyResult.result[StepsRecord.COUNT_TOTAL]
            Log.e("totalStepsForTheMoth", totalSteps.toString())
        }
    }

    /** Shows a list of all sessions (only session metadata) in the last 7 days. */
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
           steps_for_today += monthlyResult.result[StepsRecord.COUNT_TOTAL]!!
            Log.e("totalStepsForTheDay", steps_for_today.toString())
        }
        tvStepsForToday.post {tvStepsForToday.text = steps_for_today.toString()}
    }

    /** Shows a list of all steps based on time range */
    suspend fun readStepByTimeRange(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ){
       val response =
           healthConnectClient.readRecords(
               ReadRecordsRequest(
                   StepsRecord::class,
                   timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
               )
           )
        for(stepRecord in response.records){
            val steps = stepRecord.count
            Log.e("steps", steps.toString())
        }
    }

    /** shows MIN and MAX HeartRate for the week */
    suspend fun aggregateHeartRate(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
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
        //Log.e("hr-minimum", minimumHeartRate.toString())
        //Log.e("hr-maximum", maximumHeartRate.toString())

       tvHeartRateMinMax.post {tvHeartRateMinMax.text = minimumHeartRate.toString() + " - " + maximumHeartRate.toString()}
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

    suspend fun retrieveToken(
        healthConnectClient: HealthConnectClient
    ): String {
        val changesToken =
            healthConnectClient.getChangesToken(
                ChangesTokenRequest(
                    recordTypes =  setOf(StepsRecord::class),
                )
            )
        return changesToken
    }

    suspend fun getToken(
        healthConnectClient: HealthConnectClient,
        token: String,
    ){
        val response = healthConnectClient.getChanges(token)
        for(change in response.changes){
            Log.e("nagbago", change.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test, container, false)

        val healthConnectClient = HealthConnectClient.getOrCreate(requireContext())
        //var nextChangesToken = changesToken

        tvStepsForToday = view.findViewById(R.id.tvStepCounts)
        tvHeartRateMinMax = view.findViewById(R.id.tvHR_MAX_MIN)


        GlobalScope.launch {aggregateHeartRate(healthConnectClient,Instant.now().minus(7, ChronoUnit.DAYS), Instant.now())}

        GlobalScope.launch {aggregateStepsIntoMonths(healthConnectClient, LocalDateTime.now().minusMonths(1), LocalDateTime.now()) }

        GlobalScope.launch {aggregateStepsIntoDays(healthConnectClient, LocalDateTime.now().minusMinutes(900), LocalDateTime.now())}

        GlobalScope.launch {readStepByTimeRange(healthConnectClient, Instant.now().minus(1, ChronoUnit.DAYS), Instant.now()) }

      //  GlobalScope.launch { retrieveToken(healthConnectClient) }

        GlobalScope.launch { getToken(healthConnectClient, retrieveToken(healthConnectClient)) }



        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}