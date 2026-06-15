package com.xue2026.floatingx.security

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xue2026.floatingx.R
import java.io.File

class CrashLogAdapter(
    private val files: List<File>,
    private val onClick: (File) -> Unit
) : RecyclerView.Adapter<CrashLogAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvFileName)
        val size: TextView = view.findViewById(R.id.tvFileSize)
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_crash_log, p, false))

    override fun onBindViewHolder(h: VH, i: Int) {
        h.name.text = files[i].name
        h.size.text = format(files[i].length())
        h.itemView.setOnClickListener { onClick(files[i]) }
    }

    override fun getItemCount() = files.size

    private fun format(b: Long): String = when {
        b < 1024 -> "$b B"
        b < 1024 * 1024 -> "${b / 1024} KB"
        else -> "${"%.1f".format(b.toDouble() / (1024 * 1024))} MB"
    }
}