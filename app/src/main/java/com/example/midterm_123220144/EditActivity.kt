/*
MSSV: ..............
Tên: ..............
Lớp: ..............
*/
package com.example.midterm_123220144

import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Dùng chung cho cả THÊM MỚI (không có EXTRA_ID) và CHỈNH SỬA (có EXTRA_ID).
 */
class EditActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "extra_contact_id"
    }

    private var editing: Contact? = null

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAddress: EditText
    private lateinit var etCompany: EditText
    private lateinit var spGroup: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        etAddress = findViewById(R.id.etAddress)
        etCompany = findViewById(R.id.etCompany)
        spGroup = findViewById(R.id.spGroup)

        val groupAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Groups.ALL)
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGroup.adapter = groupAdapter

        val id = intent.getLongExtra(EXTRA_ID, -1L)
        editing = if (id != -1L) ContactRepository.getById(this, id) else null

        val tvTitle = findViewById<TextView>(R.id.tvEditTitle)
        val old = editing
        if (old == null) {
            tvTitle.text = "Thêm liên hệ"
        } else {
            tvTitle.text = "Sửa liên hệ"
            etName.setText(old.name)
            etPhone.setText(old.phone)
            etEmail.setText(old.email)
            etAddress.setText(old.address)
            etCompany.setText(old.company)
            spGroup.setSelection(Groups.ALL.indexOf(old.group).coerceAtLeast(0))
        }

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnSave).setOnClickListener { save() }
    }

    private fun save() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val company = etCompany.text.toString().trim()

        // Kiểm tra dữ liệu nhập
        if (name.isEmpty()) {
            etName.error = "Vui lòng nhập họ tên"
            etName.requestFocus()
            return
        }
        if (phone.isEmpty()) {
            etPhone.error = "Vui lòng nhập số điện thoại"
            etPhone.requestFocus()
            return
        }
        if (!Patterns.PHONE.matcher(phone).matches() || phone.count { it.isDigit() } < 8) {
            etPhone.error = "Số điện thoại không hợp lệ"
            etPhone.requestFocus()
            return
        }
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Email không hợp lệ"
            etEmail.requestFocus()
            return
        }

        val group = Groups.ALL[spGroup.selectedItemPosition]
        val old = editing
        if (old == null) {
            ContactRepository.add(this, Contact(0, name, phone, email, address, company, group))
            Toast.makeText(this, "Đã thêm liên hệ", Toast.LENGTH_SHORT).show()
        } else {
            ContactRepository.update(
                this,
                old.copy(
                    name = name, phone = phone, email = email,
                    address = address, company = company, group = group
                )
            )
            Toast.makeText(this, "Đã cập nhật liên hệ", Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}