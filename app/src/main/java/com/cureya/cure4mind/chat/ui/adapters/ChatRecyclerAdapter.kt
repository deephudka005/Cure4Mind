package com.cureya.cure4mind.chat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.cureya.cure4mind.R
import com.cureya.cure4mind.chat.data.models.Message

class ChatRecyclerAdapter(
    private val senderId: String
) : RecyclerView.Adapter<ChatRecyclerAdapter.ChatMessageViewHolder>() {
    private val messages: MutableList<Message> = mutableListOf()
    fun updateData(messages: List<Message>) {
        this.messages.clear()
        notifyDataSetChanged()
        this.messages.addAll(messages)
        notifyDataSetChanged()
    }


    inner class ChatMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderMessageView: TextView = itemView.findViewById(R.id.sender_message)
        val senderImageMessage : ImageView = itemView.findViewById(R.id.sender_image)
        val receiverMessageView: TextView = itemView.findViewById(R.id.receiver_message)
        val receiverImageMessage : ImageView = itemView.findViewById(R.id.receiver_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.message_item, parent, false)
        return ChatMessageViewHolder(v)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val message = messages[position]
        if (message.senderId == senderId) {
            holder.receiverMessageView.visibility = View.GONE
            holder.senderMessageView.text = message.text
            message.photoUrl?.let {
                holder.senderImageMessage.load(it)
                holder.senderImageMessage.visibility = View.VISIBLE
            }
        } else {
            holder.senderMessageView.visibility = View.GONE
            holder.receiverMessageView.text = message.text
            message.photoUrl?.let {
                holder.receiverImageMessage.load(it)
                holder.receiverImageMessage.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return this.messages.size
    }
}