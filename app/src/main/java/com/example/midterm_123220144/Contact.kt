/*
MSSV: ..............
Tên: ..............
Lớp: ..............
*/
package com.example.midterm_123220144

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.text.Normalizer

data class Contact(
    val id: Long,
    val name: String,
    val phone: String,
    val email: String,
    val address: String,
    val company: String,
    val group: String,
    val favorite: Boolean = false
) {
    // Tên gọi (từ cuối) – dùng để sắp xếp và làm avatar
    val givenName: String
        get() = name.trim().split(Regex("\\s+")).last()

    val initial: String
        get() = givenName.firstOrNull()?.uppercase() ?: "?"
}

object Groups {
    val ALL = listOf("Gia đình", "Bạn bè", "Công việc", "Khác")
}

// Bỏ dấu tiếng Việt để tìm kiếm không phân biệt dấu: "Hà" -> "Ha"
fun String.removeAccents(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return normalized
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        .replace('đ', 'd')
        .replace('Đ', 'D')
}

// Hộp thoại xác nhận xóa, dùng chung cho màn hình danh sách và màn hình chi tiết
fun Context.confirmDelete(contact: Contact, onDeleted: () -> Unit) {
    AlertDialog.Builder(this)
        .setTitle("Xóa liên hệ")
        .setMessage("Bạn có chắc muốn xóa \"${contact.name}\" không?")
        .setPositiveButton("Xóa") { _, _ ->
            ContactRepository.delete(this, contact.id)
            Toast.makeText(this, "Đã xóa liên hệ", Toast.LENGTH_SHORT).show()
            onDeleted()
        }
        .setNegativeButton("Hủy", null)
        .show()
}