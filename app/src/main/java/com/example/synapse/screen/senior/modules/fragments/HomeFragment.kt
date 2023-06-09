package com.example.synapse.screen.senior.modules.fragments

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import com.example.synapse.screen.util.ReplaceFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.auth.FirebaseUser
import com.example.synapse.screen.util.PromptMessage
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.card.MaterialCardView
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.synapse.screen.senior.MyLocation
import com.example.synapse.screen.Login
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.example.synapse.screen.util.readwrite.ReadWriteUserSenior
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails
import com.example.synapse.screen.util.notifications.FcmNotificationsSender
import com.google.firebase.database.DatabaseError
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.recyclerview.widget.RecyclerView
import com.example.synapse.R
import com.example.synapse.screen.senior.modules.fragments.fitness.HeartRateFragment
import com.example.synapse.screen.senior.modules.fragments.fitness.StepCountsFragment
import com.example.synapse.screen.util.adapter.Steps
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment() : Fragment() {

    var replaceFragment = ReplaceFragment()
    var promptMessage: PromptMessage? = null

    var referenceSeniorLocation: DatabaseReference? = null
    var referenceSenior: DatabaseReference? = null
    var referenceAssignedCarer: DatabaseReference? = null
    var referenceCarer: DatabaseReference? = null

    var mUser: FirebaseUser? = null
    var client: FusedLocationProviderClient? = null
    protected var handler: Handler? = null

    var tvSeniorName: TextView? = null
    var ivProfilePic: AppCompatImageView? = null

    var swap: TextView? = null
    var city: String? = null
    var country: String? = null
    var address: String? = null
    var carerID: String? = null
    var latitude: Double? = null
    var longtitude: Double? = null

    /* variables for step counts and heart rate */
    var tvHeartRate: TextView? = null
    private lateinit var tvHeartRateMinMax : TextView
    private lateinit var tvStepsForToday : TextView
    private var steps_for_today: Long = 0


    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_senior_home, container, false)

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        FirebaseMessaging.getInstance().subscribeToTopic("hello")
        promptMessage = PromptMessage()

        val healthConnectClient = HealthConnectClient.getOrCreate(requireContext())

        val currentTime = view.findViewById<TextClock>(R.id.tcTime)
        val btnMedication = view.findViewById<MaterialCardView>(R.id.btnMedication)
        val btnGames = view.findViewById<MaterialCardView>(R.id.btnGames)
        val btnLogout = view.findViewById<MaterialCardView>(R.id.btnLogout)
        val btnPhysicalActivity = view.findViewById<MaterialCardView>(R.id.btnPhysicalActivity)
        val btnAppointment = view.findViewById<MaterialCardView>(R.id.btnAppointment)
        val btnMyLocation = view.findViewById<MaterialCardView>(R.id.btnMyLocation)
        val btnEmergency = view.findViewById<MaterialCardView>(R.id.btnEmergency)
        val btnMoreInfo = view.findViewById<MaterialCardView>(R.id.btnMoreInfo)
        val btnStepCountsHistory = view.findViewById<MaterialCardView>(R.id.btnStepCountsHistory)
        val btnHeartRateHistory = view.findViewById<MaterialCardView>(R.id.btnHeartRateHistory)

        ivProfilePic = view.findViewById(R.id.ivSeniorProfilePic)
        tvSeniorName = view.findViewById(R.id.tvSeniorFullName)
        swap = view.findViewById(R.id.swipe)

        tvHeartRate = view.findViewById(R.id.tvHeartRate)
        tvStepsForToday = view.findViewById(R.id.tvStepCounts)
        tvHeartRateMinMax = view.findViewById(R.id.tvHR_MAX_MIN)

        mUser = FirebaseAuth.getInstance().currentUser
        val userID = mUser!!.uid

        referenceSenior = FirebaseDatabase.getInstance().getReference("Users").child("Seniors")
        referenceSeniorLocation = FirebaseDatabase.getInstance().getReference("SeniorLocation").child(mUser!!.uid)
        referenceAssignedCarer = FirebaseDatabase.getInstance().getReference("AssignedSeniors")
        referenceCarer = FirebaseDatabase.getInstance().getReference("Users").child("Carers")

        btnMedication.setOnClickListener {
            replaceFragment.replaceFragment(
                MedicationFragment(), activity
            )
        }
        btnGames.setOnClickListener {
            replaceFragment.replaceFragment(
                GamesFragment(),
                getActivity()
            )
        }
        btnPhysicalActivity.setOnClickListener {
            replaceFragment.replaceFragment(
                PhysicalActivityFragment(), activity
            )
        }
        btnAppointment.setOnClickListener {
            replaceFragment.replaceFragment(
                AppointmentFragment(), activity
            )
        }
        btnMyLocation.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    MyLocation::class.java
                )
            )
        }
        btnEmergency.setOnClickListener{
            callEmergencyCarer()
       }
        btnStepCountsHistory.setOnClickListener {
            replaceFragment.replaceFragment(
                StepCountsFragment(), activity)
        }
        btnHeartRateHistory.setOnClickListener{
            replaceFragment.replaceFragment(
                HeartRateFragment(), activity
            )
        }

        val localeToday = LocalDate.now(ZoneId.of("Asia/Manila"))
        val dawn = LocalTime.MIDNIGHT

        GlobalScope.launch { aggregateStepsIntoDays(healthConnectClient, LocalDateTime.of(localeToday, dawn), LocalDateTime.now()) }

        GlobalScope.launch { aggregateHeartRate(healthConnectClient, LocalDateTime.of(localeToday, dawn), LocalDateTime.now()) }


        currentTime.format12Hour = "hh:mm a"
        showUserProfile(userID)

        btnLogout.setOnClickListener {
            val user: FirebaseAuth = FirebaseAuth.getInstance()
            user.signOut()
            val intent: Intent = Intent(getActivity(), Login::class.java)
            intent.addFlags(
                (Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            startActivity(intent)
            requireActivity().onBackPressed()
        }

        //message handler for the send thread.
        handler = Handler(Handler.Callback { msg ->
            val stuff = msg.data
            logthisHeartRate(stuff.getString("logthis1"))
            //logthisStatus(stuff.getString("logthis2"))
            //logthisStepCounts(stuff.getString("logthis3"))
            true
        })

        // Register the local broadcast receiver
        val messageFilter = IntentFilter(Intent.ACTION_SEND)
        val messageReceiver = MessageReceiver()
        LocalBroadcastManager.getInstance((activity)!!)
            .registerReceiver(messageReceiver, messageFilter)

        location

        return view
    }

    //==============================================================================================

    /** Shows a list of all sessions (only session metadata) for the current day 1 */
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
        }
        tvStepsForToday.post {tvStepsForToday.text = steps_for_today.toString()}
        updateHealthStatus("stepcounts",steps_for_today.toString())
    }

    /** shows MIN and MAX HeartRate for the week */
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

    //setup a broadcast receiver to receive the messages from the wear device via the MessageService.
    inner class MessageReceiver() : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val heartrate = intent.getStringExtra("heartrate")

            if (heartrate != null) {
                logthisHeartRate(heartrate)
                updateHealthStatus("heartrate", heartrate)
            }
        }
    }

    /*
     * simple method to add the log TextView.
     */
    fun logthisHeartRate(newinfo1: String?) {
        if (newinfo1!!.compareTo("") != 0) {
            tvHeartRate!!.text = newinfo1
        }
    }

    fun updateHealthStatus(title: String, newinfo: String) {
        val hashMap = HashMap<String, Any>()
        hashMap[title] = newinfo
        referenceSenior!!.child(mUser!!.uid).updateChildren(hashMap)
            .addOnCompleteListener(object : OnCompleteListener<Void?> {
                override fun onComplete(task: Task<Void?>) {
                }
            })
    }

    fun callEmergencyCarer() {
        referenceAssignedCarer!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (key: DataSnapshot in snapshot.children) {
                    val carerKey = key.key
                    referenceAssignedCarer!!.child((carerKey)!!)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (key: DataSnapshot in snapshot.children) {
                                    val key1 = key.key
                                    referenceAssignedCarer!!.child((carerKey)).child((key1)!!)
                                        .addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.hasChild("seniorID")) {
                                                    val senior = snapshot.getValue(
                                                        ReadWriteUserSenior::class.java
                                                    )
                                                    val seniorID = senior!!.getSeniorID()
                                                    val mUser =
                                                        FirebaseAuth.getInstance().currentUser
                                                    if ((seniorID == mUser!!.uid)) {
                                                        referenceCarer!!.child((carerKey))
                                                            .addListenerForSingleValueEvent(object :
                                                                ValueEventListener {
                                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                                    if (snapshot.exists()) {
                                                                        val carer =
                                                                            snapshot.getValue(
                                                                                ReadWriteUserDetails::class.java
                                                                            )
                                                                        val carerMobileNumber =
                                                                            carer!!.getMobileNumber()
                                                                        val intent =
                                                                            Intent(Intent.ACTION_CALL)
                                                                        intent.data =
                                                                            Uri.parse("tel:$carerMobileNumber")
                                                                        if (ActivityCompat.checkSelfPermission(
                                                                                (activity)!!,
                                                                                Manifest.permission.CALL_PHONE
                                                                            ) != PackageManager.PERMISSION_GRANTED
                                                                        ) {
                                                                            return
                                                                        }
                                                                        startActivity(intent)
                                                                    }
                                                                }

                                                                override fun onCancelled(error: DatabaseError) {
                                                                    promptMessage!!.defaultErrorMessageContext(
                                                                        activity
                                                                    )
                                                                }
                                                            })
                                                    }
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                promptMessage!!.defaultErrorMessageContext(activity)
                                            }
                                        })
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                promptMessage!!.defaultErrorMessageContext(activity)
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                promptMessage!!.defaultErrorMessageContext(activity)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        generateToken()
        //location
    }

    fun generateToken() {
        // retrieve  and generate token everytime user access the app
        val hashMap = HashMap<String, Any>()
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(object : OnCompleteListener<String?> {
                override fun onComplete(task: Task<String?>) {
                    if (!task.isSuccessful) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        return
                    }
                    val token = task.result
                    hashMap["token"] = token.toString()
                    referenceSenior!!.child(mUser!!.uid).updateChildren(hashMap)
                        .addOnSuccessListener {
                            Log.d("Token", token.toString())
                        }
                }
            })
    }

    // Logic to handle location object// Got last known location. In some rare situations this can be null.// TODO: Consider calling

    //    ActivityCompat#requestPermissions
    // here to request the missing permissions, and then overriding
    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
    //                                          int[] grantResults)
    // to handle the case where the user grants the permission. See the documentation
    // for ActivityCompat#requestPermissions for more details.
    // retrieve senior's current location and store to firebase
    val location: Unit
        get() {
            // retrieve senior's current location and store to firebase
            client = LocationServices.getFusedLocationProviderClient((activity)!!)
            if ((ActivityCompat.checkSelfPermission(
                    (activity)!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(
                    (activity)!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                        != PackageManager.PERMISSION_GRANTED)
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            client!!.lastLocation
                .addOnSuccessListener((activity)!!, object : OnSuccessListener<Location?> {
                    override fun onSuccess(location: Location?) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            val geocoder = Geocoder((activity!!), Locale.ENGLISH)
                            var current_address: List<Address>? = null
                            try {
                                current_address = geocoder.getFromLocation(
                                    location.latitude,
                                    location.longitude,
                                    1
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            address = current_address!![0].getAddressLine(0)
                            latitude = current_address[0].latitude
                            longtitude = current_address[0].longitude
                            city = current_address[0].locality
                            country = current_address[0].countryName
                            storeLocation(address, latitude, longtitude, city, country)
                        }
                    }
                })
        }

    fun storeLocation(
        address: String?, latitude: Double?,
        longtitude: Double?, city: String?, country: String?
    ) {
        val hashMap = HashMap<String, Any?>()
        hashMap["Address"] = address
        hashMap["Latitude"] = latitude
        hashMap["Longtitude"] = longtitude
        hashMap["City"] = city
        hashMap["Country"] = country
        referenceSeniorLocation!!.setValue(hashMap)
            .addOnCompleteListener(object : OnCompleteListener<Void?> {
                override fun onComplete(task: Task<Void?>) {
                    if (task.isSuccessful) {
                        Log.d("Location", "Location is successfully stored")
                    }
                }
            })
    }

    // retrieve senior's profile picture
    fun showUserProfile(firebaseUser: String?) {
        referenceSenior!!.child((firebaseUser)!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userProfile = snapshot.getValue(
                            ReadWriteUserDetails::class.java
                        )
                        if (userProfile != null) {
                            val fullname = userProfile.firstName + " " + userProfile.lastName
                            tvSeniorName!!.text = fullname

                            // display carer profile pic
                            val uri = mUser!!.photoUrl
                            Picasso.get()
                                .load(uri)
                                .transform(CropCircleTransformation())
                                .into(ivProfilePic)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        activity,
                        "Something went wrong! Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    companion object {
        // Global variables
        val TAG = "wear"

        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}

