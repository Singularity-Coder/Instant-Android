package com.singularitycoder.readsms

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.Telephony
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.singularitycoder.readsms.databinding.ActivityMainBinding
import com.singularitycoder.readsms.databinding.ItemSmsBinding
import java.text.DateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_READ_SMS: Int = 101
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSmsListView()
        binding.root.setOnClickListener { setSmsListView() }
    }

    private fun setSmsListView() {
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS), REQUEST_CODE_READ_SMS)
            return
        }
        binding.llSms.removeAllViews()
        for (sms in getAllSms(this)) {
            showViewsWithViewBinding(sms)
        }
    }

    private fun showViewsWithViewBinding(sms: Sms) {
        val itemBinding: ItemSmsBinding = ItemSmsBinding.inflate(LayoutInflater.from(this), binding.llSms, false)
        itemBinding.tvNumber.text = sms.number
        itemBinding.tvBody.text = sms.body
        itemBinding.tvDate.text = sms.date
        itemBinding.tvType.text = sms.type?.toUpperCase()
        binding.llSms.addView(itemBinding.root)
    }

    private fun showViewsWithoutViewBinding(sms: Sms) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_sms, binding.llSms, false)
        view?.findViewById<TextView>(R.id.tv_number)?.text = sms.number
        view?.findViewById<TextView>(R.id.tv_body)?.text = sms.body
        view?.findViewById<TextView>(R.id.tv_date)?.text = sms.date
        view?.findViewById<TextView>(R.id.tv_type)?.text = sms.type
        binding.llSms.addView(view)
    }

    // https://stackoverflow.com/questions/848728/how-can-i-read-sms-messages-from-the-device-programmatically-in-android
    private fun getAllSms(context: Context): MutableList<Sms> {
        val contentResolver: ContentResolver = context.contentResolver
        val cursor: Cursor? = contentResolver.query(Telephony.Sms.CONTENT_URI, null, null, null, null)
        var totalSMS: Int = 0
        val smsList: MutableList<Sms> = mutableListOf<Sms>()
        if (cursor == null) {
            Toast.makeText(this, "No message to show!", Toast.LENGTH_SHORT).show()
            return smsList
        }
        totalSMS = cursor.count
        if (cursor.moveToFirst()) {
            for (j in 0 until totalSMS) {
                val smsDate: String = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))
                val date = DateFormat.getDateTimeInstance().format(smsDate.toLong())
                val number: String = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body: String = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
                val type: String = when (cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)).toInt()) {
                    Telephony.Sms.MESSAGE_TYPE_INBOX -> "inbox"
                    Telephony.Sms.MESSAGE_TYPE_SENT -> "sent"
                    Telephony.Sms.MESSAGE_TYPE_OUTBOX -> "outbox"
                    else -> "Unknown"
                }
                smsList.add(Sms(date.toString(), number, body, type))
                println("sms: ${Sms(date.toString(), number, body, type)}")
                cursor.moveToNext()
            }
        }
        cursor.close()
        return smsList
    }
}

data class Sms(
    var date: String? = null,
    var number: String? = null,
    var body: String? = null,
    var type: String? = null
)