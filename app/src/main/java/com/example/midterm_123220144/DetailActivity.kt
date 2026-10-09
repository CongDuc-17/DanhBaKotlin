/*
MSSV: ..............
Tên: ..............
Lớp: ..............
*/
package com.example.midterm_123220144

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "extra_contact_id"
    }

    private var contactId = -1L
    private var current: Contact? = null

    private lateinit var tvAvatar: TextView
    private lateinit var tvName: TextView
    private lateinit var tvGroup: TextView
    private lateinit var tvFavorite: TextView
    private lateinit var infoContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        contactId = intent.getLongExtra(EXTRA_ID, -1L)

        tvAvatar = findViewById(R.id.tvDetailAvatar)
        tvName = findViewById(R.id.tvDetailName)
        tvGroup = findViewById(R.id.tvDetailGroup)
        tvFavorite = findViewById(R.id.tvDetailFavorite)
        infoContainer = findViewById(R.id.infoContainer)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        tvFavorite.setOnClickListener {
            current?.let {
                ContactRepository.toggleFavorite(this, it.id)
                load()
            }
        }

        findViewById<Button>(R.id.btnCall).setOnClickListener {
            current?.let { safeStart(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + it.phone.replace(" ", "")))) }
        }
        findViewById<Button>(R.id.btnSms).setOnClickListener {
            current?.let { safeStart(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + it.phone.replace(" ", "")))) }
        }
        findViewById<Button>(R.id.btnEmail).setOnClickListener {
            current?.let {
                if (it.email.isBlank()) {
                    Toast.makeText(this, "Liên hệ này chưa có email", Toast.LENGTH_SHORT).show()
                } else {
                    safeStart(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + it.email)))
                }
            }
        }

        findViewById<Button>(R.id.btnEdit).setOnClickListener {
            current?.let {
                startActivity(
                    Intent(this, EditActivity::class.java).putExtra(EditActivity.EXTRA_ID, it.id)
                )
            }
        }
        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            current?.let { c -> confirmDelete(c) { finish() } }
        }
    }

    // Sau khi sửa xong quay lại màn hình này thì tải lại dữ liệu mới
    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        val c = ContactRepository.getById(this, contactId)
        if (c == null) {
            finish()
            return
        }
        current = c

        tvAvatar.text = c.initial
        tvAvatar.background.mutate().setTint(Color.parseColor("#7986CB"))
        tvName.text = c.name
        tvGroup.text = "Nhóm: " + c.group
        tvFavorite.text = if (c.favorite) "★" else "☆"
        tvFavorite.setTextColor(
            if (c.favorite) Color.parseColor("#FFC107") else Color.parseColor("#9E9E9E")
        )

        infoContainer.removeAllViews()
        addInfoRow("Số điện thoại", c.phone)
        addInfoRow("Email", c.email)
        addInfoRow("Địa chỉ", c.address)
        addInfoRow("Công ty", c.company)
    }

    private fun addInfoRow(label: String, value: String) {
        val row = LayoutInflater.from(this).inflate(R.layout.item_info, infoContainer, false)
        row.findViewById<TextView>(R.id.tvLabel).text = label
        row.findViewById<TextView>(R.id.tvValue).text = value.ifBlank { "—" }
        infoContainer.addView(row)
    }

    private fun safeStart(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Không tìm thấy ứng dụng phù hợp", Toast.LENGTH_SHORT).show()
        }
    }
}