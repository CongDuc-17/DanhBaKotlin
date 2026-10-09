/*
MSSV: ..............
Tên: ..............
Lớp: ..............
*/
package com.example.midterm_123220144

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Nơi lưu trữ danh bạ. Dữ liệu được lưu vào SharedPreferences dưới dạng JSON
 * nên tắt app mở lại vẫn còn.
 */
object ContactRepository {

    private const val PREFS_NAME = "contacts_prefs"
    private const val KEY_DATA = "contacts_json"

    private val contacts = mutableListOf<Contact>()
    private var loaded = false

    private fun ensureLoaded(context: Context) {
        if (loaded) return
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_DATA, null)
        if (json == null) {
            contacts.addAll(sampleData())
            save(context)
        } else {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                contacts.add(fromJson(arr.getJSONObject(i)))
            }
        }
        loaded = true
    }

    fun getAll(context: Context): List<Contact> {
        ensureLoaded(context)
        return contacts.toList()
    }

    fun getById(context: Context, id: Long): Contact? {
        ensureLoaded(context)
        return contacts.find { it.id == id }
    }

    fun add(context: Context, contact: Contact) {
        ensureLoaded(context)
        contacts.add(contact.copy(id = System.currentTimeMillis()))
        save(context)
    }

    fun update(context: Context, contact: Contact) {
        ensureLoaded(context)
        val index = contacts.indexOfFirst { it.id == contact.id }
        if (index >= 0) {
            contacts[index] = contact
            save(context)
        }
    }

    fun delete(context: Context, id: Long) {
        ensureLoaded(context)
        contacts.removeAll { it.id == id }
        save(context)
    }

    fun toggleFavorite(context: Context, id: Long) {
        ensureLoaded(context)
        val index = contacts.indexOfFirst { it.id == id }
        if (index >= 0) {
            contacts[index] = contacts[index].copy(favorite = !contacts[index].favorite)
            save(context)
        }
    }

    private fun save(context: Context) {
        val arr = JSONArray()
        contacts.forEach { arr.put(toJson(it)) }
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_DATA, arr.toString())
            .apply()
    }

    private fun toJson(c: Contact) = JSONObject().apply {
        put("id", c.id)
        put("name", c.name)
        put("phone", c.phone)
        put("email", c.email)
        put("address", c.address)
        put("company", c.company)
        put("group", c.group)
        put("favorite", c.favorite)
    }

    private fun fromJson(o: JSONObject) = Contact(
        id = o.getLong("id"),
        name = o.getString("name"),
        phone = o.getString("phone"),
        email = o.optString("email"),
        address = o.optString("address"),
        company = o.optString("company"),
        group = o.optString("group", "Khác"),
        favorite = o.optBoolean("favorite", false)
    )

    private fun sampleData(): List<Contact> = listOf(
        Contact(1, "Nguyễn Văn An", "0901 234 567", "an.nguyen@gmail.com", "12 Lê Lợi, Quận 1, TP.HCM", "Công ty FPT", "Công việc", true),
        Contact(2, "Trần Thị Bình", "0912 345 678", "binh.tran@gmail.com", "45 Trần Phú, Hải Châu, Đà Nẵng", "Ngân hàng Vietcombank", "Bạn bè"),
        Contact(3, "Lê Minh Châu", "0923 456 789", "chau.le@gmail.com", "88 Nguyễn Huệ, Huế", "Trường ĐH Khoa học Huế", "Gia đình", true),
        Contact(4, "Phạm Quốc Dũng", "0934 567 890", "dung.pham@gmail.com", "23 Hai Bà Trưng, Hà Nội", "Viettel", "Công việc"),
        Contact(5, "Hoàng Thu Hà", "0945 678 901", "ha.hoang@gmail.com", "7 Bạch Đằng, Hải Châu, Đà Nẵng", "Bệnh viện Đà Nẵng", "Bạn bè"),
        Contact(6, "Vũ Đức Huy", "0956 789 012", "huy.vu@gmail.com", "150 Điện Biên Phủ, Quận 3, TP.HCM", "VNG Corporation", "Công việc"),
        Contact(7, "Đặng Khánh Linh", "0967 890 123", "linh.dang@gmail.com", "9 Võ Văn Tần, Quận 3, TP.HCM", "Shopee Việt Nam", "Bạn bè"),
        Contact(8, "Bùi Thanh Mai", "0978 901 234", "mai.bui@gmail.com", "31 Pasteur, Quận 1, TP.HCM", "Vinamilk", "Gia đình"),
        Contact(9, "Đỗ Hoàng Nam", "0989 012 345", "nam.do@gmail.com", "66 Nguyễn Văn Linh, Đà Nẵng", "Tập đoàn Sun Group", "Công việc"),
        Contact(10, "Ngô Bảo Ngọc", "0990 123 456", "ngoc.ngo@gmail.com", "5 Lý Thường Kiệt, Hà Nội", "Tiki", "Bạn bè", true),
        Contact(11, "Dương Gia Phúc", "0902 345 678", "phuc.duong@gmail.com", "102 Lê Duẩn, Hà Nội", "Công ty Misa", "Công việc"),
        Contact(12, "Lý Mỹ Quyên", "0913 456 789", "quyen.ly@gmail.com", "27 Phan Châu Trinh, Hội An", "Khách sạn Anantara", "Gia đình"),
        Contact(13, "Trịnh Văn Sơn", "0924 567 890", "son.trinh@gmail.com", "14 Hùng Vương, Cần Thơ", "Đại học Cần Thơ", "Khác"),
        Contact(14, "Phan Thị Tâm", "0935 678 901", "tam.phan@gmail.com", "50 Nguyễn Trãi, Quận 5, TP.HCM", "Nhà thuốc Long Châu", "Gia đình"),
        Contact(15, "Mai Anh Tuấn", "0946 789 012", "tuan.mai@gmail.com", "3 Trường Chinh, Hà Nội", "Vingroup", "Công việc"),
        Contact(16, "Cao Thị Uyên", "0957 890 123", "uyen.cao@gmail.com", "19 Quang Trung, Đà Nẵng", "Trường THPT Phan Châu Trinh", "Bạn bè"),
        Contact(17, "Tạ Quang Vinh", "0968 901 234", "vinh.ta@gmail.com", "76 Lê Hồng Phong, Nha Trang", "Vinpearl", "Khác"),
        Contact(18, "Hồ Ngọc Yến", "0979 012 345", "yen.ho@gmail.com", "21 Trần Hưng Đạo, Quy Nhơn", "Bưu điện Bình Định", "Gia đình")
    )
}