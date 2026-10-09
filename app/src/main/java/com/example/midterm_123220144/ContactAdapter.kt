package com.example.midterm_123220144

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ContactAdapter(
    context: Context,
    private val onFavoriteClick: (Contact) -> Unit
) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)
    private var items: List<Contact> = emptyList()

    private val avatarColors = intArrayOf(
        Color.parseColor("#E57373"), Color.parseColor("#BA68C8"),
        Color.parseColor("#7986CB"), Color.parseColor("#4FC3F7"),
        Color.parseColor("#4DB6AC"), Color.parseColor("#81C784"),
        Color.parseColor("#FFB74D"), Color.parseColor("#A1887F")
    )

    private class ViewHolder(view: View) {
        val tvAvatar: TextView = view.findViewById(R.id.tvAvatar)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPhone: TextView = view.findViewById(R.id.tvPhone)
        val tvFavorite: TextView = view.findViewById(R.id.tvFavorite)
    }

    fun submitList(newItems: List<Contact>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Contact = items[position]
    override fun getItemId(position: Int): Long = items[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_contact, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val c = getItem(position)
        holder.tvName.text = c.name
        holder.tvPhone.text = c.phone + "  •  " + c.group
        holder.tvAvatar.text = c.initial
        holder.tvAvatar.background.mutate()
            .setTint(avatarColors[(c.name.hashCode() and 0x7fffffff) % avatarColors.size])

        holder.tvFavorite.text = if (c.favorite) "★" else "☆"
        holder.tvFavorite.setTextColor(
            if (c.favorite) Color.parseColor("#FFC107") else Color.parseColor("#9E9E9E")
        )
        holder.tvFavorite.setOnClickListener { onFavoriteClick(c) }

        return view
    }
}