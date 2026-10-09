/*
MSSV: 123220144
Tên: Trần Lê Công Đức
Lớp: 22PFIEV3
*/
package com.example.midterm_123220144

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import java.text.Collator
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ContactAdapter
    private lateinit var listView: ListView
    private lateinit var etSearch: EditText
    private lateinit var spFilter: Spinner
    private lateinit var tvCount: TextView
    private lateinit var tvEmpty: TextView

    // Danh sách đang hiển thị (sau khi tìm kiếm + lọc + sắp xếp)
    private var displayed: List<Contact> = emptyList()

    // Vị trí 0 = Tất cả, 1 = Yêu thích, còn lại là các nhóm
    private val filterOptions = listOf("Tất cả", "★ Yêu thích") + Groups.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        listView = findViewById(R.id.listViewContacts)
        etSearch = findViewById(R.id.etSearch)
        spFilter = findViewById(R.id.spFilter)
        tvCount = findViewById(R.id.tvCount)
        tvEmpty = findViewById(R.id.tvEmpty)

        // Adapter cho ListView
        adapter = ContactAdapter(this) { contact ->
            ContactRepository.toggleFavorite(this, contact.id)
            refresh()
        }
        listView.adapter = adapter

        // Bộ lọc
        val filterAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFilter.adapter = filterAdapter
        spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) = refresh()
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        // Tìm kiếm: gõ đến đâu lọc đến đó
        etSearch.doAfterTextChanged { refresh() }

        // Bấm vào 1 người -> màn hình chi tiết
        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_ID, displayed[position].id)
            startActivity(intent)
        }

        // Nhấn giữ -> menu Xem / Sửa / Xóa
        listView.setOnItemLongClickListener { _, _, position, _ ->
            showActionDialog(displayed[position])
            true
        }

        // Nút + -> thêm liên hệ mới
        findViewById<TextView>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java))
        }
    }

    // Quay lại từ màn hình Chi tiết / Thêm / Sửa thì tải lại danh sách
    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val keyword = etSearch.text.toString().trim().removeAccents().lowercase()
        val filterPos = spFilter.selectedItemPosition.coerceAtLeast(0)
        val collator = Collator.getInstance(Locale.forLanguageTag("vi"))

        displayed = ContactRepository.getAll(this)
            .filter { c ->
                val matchFilter = when (filterPos) {
                    0 -> true
                    1 -> c.favorite
                    else -> c.group == Groups.ALL[filterPos - 2]
                }
                val matchSearch = keyword.isEmpty()
                        || c.name.removeAccents().lowercase().contains(keyword)
                        || c.phone.replace(" ", "").contains(keyword.replace(" ", ""))
                        || c.email.lowercase().contains(keyword)
                matchFilter && matchSearch
            }
            .sortedWith { a, b ->
                val r = collator.compare(a.givenName, b.givenName)
                if (r != 0) r else collator.compare(a.name, b.name)
            }

        adapter.submitList(displayed)
        tvCount.text = "${displayed.size} liên hệ"
        tvEmpty.visibility = if (displayed.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showActionDialog(contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle(contact.name)
            .setItems(arrayOf("Xem chi tiết", "Sửa", "Xóa")) { _, which ->
                when (which) {
                    0 -> startActivity(
                        Intent(this, DetailActivity::class.java)
                            .putExtra(DetailActivity.EXTRA_ID, contact.id)
                    )
                    1 -> startActivity(
                        Intent(this, EditActivity::class.java)
                            .putExtra(EditActivity.EXTRA_ID, contact.id)
                    )
                    2 -> confirmDelete(contact) { refresh() }
                }
            }
            .show()
    }
}