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
        this.messages.addAll(messages)
        notifyDataSetChanged()
    }

    inner class ChatMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderMessageView: TextView = itemView.findViewById(R.id.sender_message)
        val senderImageMessage: ImageView = itemView.findViewById(R.id.sender_image)
        val receiverMessageView: TextView = itemView.findViewById(R.id.receiver_message)
        val receiverImageMessage: ImageView = itemView.findViewById(R.id.receiver_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.message_item, parent, false)
        return ChatMessageViewHolder(v)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val message = messages[position]
        holder.apply {
            if (message.senderId == senderId) {
                receiverMessageView.visibility = View.GONE
                receiverImageMessage.visibility = View.GONE
                if (message.text.isNotBlank()) senderMessageView.text =
                    message.text else senderMessageView.visibility = View.GONE
                if (message.photoUrl != null) senderImageMessage.load(message.photoUrl) else senderImageMessage.visibility =
                    View.GONE
            } else {
                senderMessageView.visibility = View.GONE
                senderImageMessage.visibility = View.GONE
                if (message.text.isNotBlank()) receiverMessageView.text =
                    message.text else receiverMessageView.visibility = View.GONE
                if (message.photoUrl != null) receiverImageMessage.load(message.photoUrl) else receiverMessageView.visibility =
                    View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return this.messages.size
    }
}