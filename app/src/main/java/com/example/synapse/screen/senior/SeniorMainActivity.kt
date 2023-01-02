package com.example.synapse.screen.senior

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.synapse.screen.util.ReplaceFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.synapse.screen.senior.modules.view.TicTacToeHome
import com.example.synapse.screen.senior.games.TriviaQuiz
import com.example.synapse.screen.senior.games.MathGame
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.synapse.R
import com.example.synapse.databinding.ActivityCarerBottomNavigationBinding
import com.example.synapse.databinding.ActivitySeniorBottomNavigationBinding
import com.google.firebase.messaging.FirebaseMessaging
import com.example.synapse.screen.senior.modules.fragments.HomeFragment
import com.example.synapse.screen.senior.modules.fragments.MedicationFragment
import com.example.synapse.screen.senior.modules.fragments.PhysicalActivityFragment
import com.example.synapse.screen.senior.modules.fragments.SettingsFragment
import com.example.synapse.screen.util.PromptMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.random.Random

class SeniorMainActivity : AppCompatActivity() {

    private var binding: ActivitySeniorBottomNavigationBinding? = null

    var replaceFragment = ReplaceFragment()
    var promptMessage = PromptMessage()
    val sessionStartTime = Instant.now().minus((1 + Random.nextInt(168)).toLong(), ChronoUnit.HOURS)
    val sessionEndTime = sessionStartTime.plus(15, ChronoUnit.MINUTES)

    // build a set of permissions for required data types
    val PERMISSIONS =
        setOf(
            HealthPermission.createReadPermission(HeartRateRecord::class),
            HealthPermission.createWritePermission(HeartRateRecord::class),
            HealthPermission.createReadPermission(StepsRecord::class),
            HealthPermission.createWritePermission(StepsRecord::class)
        )

    // Create the permissions launcher.
    val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()

    val requestPermissions =
        registerForActivityResult(requestPermissionActivityContract) { granted ->
            if (granted.containsAll(PERMISSIONS)) {
                promptMessage.displayMessage(
                    "Success",
                    "Permissions successfully granted",
                    R.color.dark_green,this)

            } else {
                promptMessage.displayMessage(
                    "Reminder",
                    "Lack of required permissions",
                    R.color.red1,this)
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeniorBottomNavigationBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        val bottomNavigationView: BottomNavigationView
        val floatingActionButton: FloatingActionButton

        replaceFragment.replaceFragment(HomeFragment(), this@SeniorMainActivity)

        bottomNavigationView = findViewById(R.id.bottomNavigationViewSenior)
        bottomNavigationView.setBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.transparent
            )
        )

        floatingActionButton = findViewById(R.id.fabLocateSenior)

        // build a set of permissions for required data types
        if (HealthConnectClient.isAvailable(applicationContext)) {
            // health connect is available
            val healthConnectClient = HealthConnectClient.getOrCreate(applicationContext)

            GlobalScope.launch { checkPermissionsAndRun(healthConnectClient) }

        }else{
            promptMessage.displayMessage(
                "Reminder",
                "Please install Health Connect in Play Store to access your health status",
                R.color.red1, this)
            installHealthConnect()
        }

            FirebaseMessaging.getInstance().subscribeToTopic("hello")
            floatingActionButton.setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        MyLocation::class.java
                    )
                )
            }

            binding!!.bottomNavigationViewSenior.setOnItemSelectedListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.miHome -> replaceFragment.replaceFragment(
                        HomeFragment(),
                        this@SeniorMainActivity
                    )
                    R.id.miChat -> {}
                    R.id.miProfile -> {}
                    R.id.miSettings -> replaceFragment.replaceFragment(
                        SettingsFragment(),
                        this@SeniorMainActivity
                    )
                }
                true
            }
       }

    suspend fun checkPermissionsAndRun(healthConnectClient: HealthConnectClient) {
        val granted = healthConnectClient.permissionController.getGrantedPermissions(PERMISSIONS)
        if (granted.containsAll(PERMISSIONS)) {
            // Permissions already granted, proceed with inserting or reading data.

            val med_key = intent.getStringExtra("med_key")
            val medicationFragment = MedicationFragment()
            val args1 = Bundle()
            args1.putString("key", med_key)
            medicationFragment.arguments = args1

            val phy_key = intent.getStringExtra("phy_key")
            val physicalActivityFragment = PhysicalActivityFragment()
            val args2 = Bundle()
            args2.putString("key", phy_key)
            physicalActivityFragment.arguments = args2

            if (med_key != null) {
                val fragmentManager = (this as FragmentActivity).supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frame_layout, medicationFragment)
                fragmentTransaction.commit()
            }

            if (phy_key != null) {
                val fragmentManager = (this as FragmentActivity).supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frame_layout, physicalActivityFragment)
                fragmentTransaction.commit()
            }

            val game_tag = intent.getStringExtra("game_tag")
            if (game_tag != null) {
                if (game_tag == "Tic-tac-toe") startActivity(
                    Intent(
                        this@SeniorMainActivity,
                        TicTacToeHome::class.java
                    )
                ) else if (game_tag == "TriviaQuiz") startActivity(
                    Intent(this@SeniorMainActivity, TriviaQuiz::class.java)
                ) else if (game_tag == "MathGame") startActivity(
                    Intent(
                        this@SeniorMainActivity,
                        MathGame::class.java
                    )
                )
            }

        } else {
            requestPermissions.launch(PERMISSIONS)
        }
    }

    // redirect user to install health connect
    fun installHealthConnect(){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setPackage("com.android.vending")
        intent.data = Uri.parse("market://details")
            .buildUpon()
            .appendQueryParameter("id","com.google.android.apps.healthdata")
            .appendQueryParameter("url", "healthconnect://onboarding")
            .build()
        this.startActivity(intent)
    }
 }