package org.d3ifcool.waterbotapp

import android.app.ActionBar
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.*
import org.d3ifcool.waterbotapp.databinding.ActivityControlBinding
import java.util.*


class ControlActivity : AppCompatActivity() {

    companion object {
        private var REMIND_HOUR = 0
        private var REMIND_MINUTES = 0

        private var REMIND_HOUR_OFF = 0
        private var REMIND_MINUTES_OFF = 0
    }

    lateinit var timePicker: TimePickerHelper

    var status = 0

    private lateinit var binding: ActivityControlBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = ""
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        database = FirebaseDatabase.getInstance().reference
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val relay = snapshot.child("Waterbot/Relay").value!!.toString()
                if (relay == "off") {
                    binding.btnPower.setImageResource(R.drawable.btn_off)
                    binding.status.text = getString(R.string.power_off)

                } else {
                    binding.btnPower.setImageResource(R.drawable.btn_on)
                    binding.status.text = getString(R.string.power_on)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.btnPower.setOnClickListener {
            setPower()
        }

        timePicker = TimePickerHelper(this, true, false)

        binding.btnSchedule.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                setOn()
                setOff()
                Toast.makeText(this, "Schedule ON", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Schedule OFF", Toast.LENGTH_SHORT).show()
            }
        }

        binding.editTextTimeOn.setOnClickListener { showTimePickerDialog1() }

        binding.editTextTimeOff.setOnClickListener { showTimePickerDialog2() }

        binding.btnHumidity.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                setHumidity()
                Toast.makeText(this, getString(R.string.kontrol_lembab_on), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, getString(R.string.kontrol_lembab_off), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setHumidity() {
        val hum = binding.etHumidity.text.toString()
        FirebaseDatabase.getInstance().reference.child("Waterbot/Relay").setValue(hum)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showTimePickerDialog1() {
        val cal = Calendar.getInstance()
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)
        timePicker.showDialog(h, m, object : TimePickerHelper.Callback {
            override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                val hourStr = if (hourOfDay < 10) "0${hourOfDay}" else "${hourOfDay}"
                val minuteStr = if (minute < 10) "0${minute}" else "${minute}"
                binding.editTextTimeOn.text = "${hourOfDay}:${minuteStr}"

                REMIND_HOUR = hourOfDay
                REMIND_MINUTES = minute
            }
        })
    }

    private fun showTimePickerDialog2() {
        val cal = Calendar.getInstance()
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)
        timePicker.showDialog(h, m, object : TimePickerHelper.Callback {
            override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                val hourStr = if (hourOfDay < 10) "0${hourOfDay}" else "${hourOfDay}"
                val minuteStr = if (minute < 10) "0${minute}" else "${minute}"
                binding.editTextTimeOff.text = "${hourOfDay}:${minuteStr}"

                REMIND_HOUR_OFF = hourOfDay
                REMIND_MINUTES = minute
            }
        })
    }

    private fun setOn() {
        FirebaseDatabase.getInstance().reference.child("Waterbot/Relay").setValue("on")
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, REMIND_HOUR)
            set(Calendar.MINUTE, REMIND_MINUTES)
            set(Calendar.SECOND, 0)
        }

        if (calendar.time <= Calendar.getInstance().time) {
            FirebaseDatabase.getInstance().reference.child("Waterbot/Relay").setValue("on")
        }
    }

    private fun setOff() {
        FirebaseDatabase.getInstance().reference.child("Waterbot/Relay").setValue("off")
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, REMIND_HOUR_OFF)
            set(Calendar.MINUTE, REMIND_MINUTES_OFF)
            set(Calendar.SECOND, 0)
        }

        if (calendar.time <= Calendar.getInstance().time) {
            FirebaseDatabase.getInstance().reference.child("Waterbot/Relay").setValue("off")
        }
    }

    private fun setPower() {
        if (status % 2 == 0) {
            FirebaseDatabase.getInstance().reference.child("Waterbot/Relay").setValue("on")
            status++
        } else {
            FirebaseDatabase.getInstance().reference.child("Waterbot/Relay").setValue("off")
            status++
        }
    }

}