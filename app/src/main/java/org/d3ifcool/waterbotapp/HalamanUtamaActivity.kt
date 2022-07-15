package org.d3ifcool.waterbotapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        binding.ivBathelp.setOnClickListener {

            Toast.makeText(this, "*Tegangan Baterai : \\n >12.5 Aman \\n < 12.5 Hapir habis \\n < 12 Tidak bisa digunakan", Toast.LENGTH_SHORT).show()
        }

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

//        database = FirebaseDatabase.getInstance().reference
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val nilaiHumidity = snapshot.child("Waterbot/Humidity").value!!.toString().toFloat()
//                val nilaiVolt = snapshot.child("Waterbot/Volt").value!!.toString().toFloat()
//                binding.tvHumidity.text = "${nilaiHumidity}"
//                binding.tvVolt.text = "${nilaiVolt}"
//                Log.d(TAG, "Value HUM:" + nilaiHumidity)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        })


        database = FirebaseDatabase.getInstance().getReference("Waterbot")
        database.get().addOnSuccessListener {

            if (it.exists()) {

                val volt = it.child("Volt").value
                val humidity = it.child("Humidity").value
                val auto = it.child("Auto").value
                val relay = it.child("Relay").value
                Toast.makeText(this, "Successfuly Read", Toast.LENGTH_SHORT).show()
                binding.tvHumidity.text = (humidity.toString())
                binding.tvVolt.text = (volt.toString())

            } else {
                Toast.makeText(this, "Data Doesn't Exist", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {

            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()

        }

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
            FirebaseDatabase.getInstance().reference.child("Waterbot").child("Relay").setValue("On")
            binding.pwr1.setImageResource(R.drawable.btn_on)
            binding.status.text = "Power On"
            status++
        } else {
            FirebaseDatabase.getInstance().reference.child("Waterbot").child("Relay")
                .setValue("Off")
            binding.pwr1.setImageResource(R.drawable.btn_off)
            binding.status.text = "Power Off"
            status++
        }
    }

    private fun readData(data: String) {


    }


}