package org.d3ifcool.waterbotapp

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*
import org.d3ifcool.waterbotapp.databinding.ActivityDeviceSelectBinding


class DeviceSelect : AppCompatActivity() {
    var id=""
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityDeviceSelectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_select)
        val idAlat = binding.etId.toString()
        database = FirebaseDatabase.getInstance().reference
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                id = snapshot.child("Waterbot").child(idAlat).toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(ContentValues.TAG, "Failed to read value.", error.toException());
            }
        })

        binding.btnLanjut.setOnClickListener{
            cekID()
        }

    }


    private fun cekID() {
        Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, HalamanUtamaActivity::class.java))
    }
}