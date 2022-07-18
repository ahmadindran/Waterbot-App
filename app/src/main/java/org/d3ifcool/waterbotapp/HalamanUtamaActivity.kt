package org.d3ifcool.waterbotapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import org.d3ifcool.waterbotapp.databinding.ActivityHalamanUtamaBinding
import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.database.DatabaseReference


 class HalamanUtamaActivity : AppCompatActivity() {

    val user = Firebase.auth.currentUser

    private lateinit var binding: ActivityHalamanUtamaBinding
    private lateinit var database: DatabaseReference

    var status = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHalamanUtamaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (user != null) {
            val iv_user = binding.ivUser
            Glide.with(this).load(user.photoUrl).into(iv_user)
            binding.welcome.text = getString(R.string.welcome_x, user.displayName)
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.ivUser.setOnClickListener { goProfileActivity() }
        binding.control.setOnClickListener { goCtrlActivity() }
        binding.pwr1.setOnClickListener { setPower() }
        binding.ivBathelp.setOnClickListener {
            Toast.makeText(
                this,
                "*Tegangan Baterai : \\n >12.5 Aman \\n < 12.5 Hapir habis \\n < 12 Tidak bisa digunakan",
                Toast.LENGTH_SHORT
            ).show()
        }

        database = FirebaseDatabase.getInstance().reference
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nilaiHumidity = snapshot.child("Waterbot/Humidity").value!!.toString()
                val nilaiVolt = snapshot.child("Waterbot/Volt").value!!.toString().toFloat()
                val waktu = snapshot.child("Waterbot/Waktu").value!!.toString()

                binding.tvHumidity.text = "${nilaiHumidity}%"
                binding.tvVolt.text = "${nilaiVolt} V"
                binding.lastUpdate.text = waktu

                when {
                    nilaiVolt < 12.0 -> {
                        binding.infoBatt.text = getString(R.string.tidak_bisa_digunakan)
                        binding.infoBatt.setTextColor(Color.parseColor("#FF9494"))
                        binding.infoBatt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
                    }
                    nilaiVolt < 12.5 -> {
                        binding.infoBatt.text = getString(R.string.hampir_habis)
                        binding.infoBatt.setTextColor(Color.parseColor("#eed202"))
                        binding.infoBatt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16F)
                    }
                    else -> {
                        binding.infoBatt.text = getString(R.string.aman)
                        binding.infoBatt.setTextColor(Color.parseColor("#4BB543"))
                        binding.infoBatt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16F)
                    }
                }

                val relay = snapshot.child("Waterbot/Relay").value!!.toString()
                if (relay == "off") {
                    binding.pwr1.setImageResource(R.drawable.btn_off)
                } else {
                    binding.pwr1.setImageResource(R.drawable.btn_on)
                }

                Log.i(TAG, "Value HUM:" + nilaiHumidity)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "Failed to read value.", error.toException());
            }
        })

    }

    private fun goCtrlActivity() {
        val intent = Intent(this, ControlActivity::class.java)
        startActivity(intent)

    }

    private fun goProfileActivity() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun setPower() {
        if (status % 2 == 0) {
            FirebaseDatabase.getInstance().reference.child("Waterbot").child("Relay").setValue("on")
            status++
        } else {
            FirebaseDatabase.getInstance().reference.child("Waterbot").child("Relay")
                .setValue("off")
            status++
        }
    }

}