package com.vyarth.ellipsify.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.databinding.AdapterGeminiBinding
import com.vyarth.ellipsify.databinding.AdapterUserBinding

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messages = mutableListOf<Pair<String, Int>>()

    @SuppressLint("NotifyDataSetChanged")
    fun setMessages(messages: List<Pair<String, Int>>) {
        this.messages.apply {
            clear()
            addAll(messages)
        }
        notifyDataSetChanged()
    }

    inner class UserViewHolder(private val binding: AdapterUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Pair<String, Int>) {
            val userMessage = message.first
            // Add the initial sentence before the user's message
            val initialSentence = "Behave as if you are a mental health consultant/therapist " +
                    "and give advice that is very genuine and helps the user. " +
                    "Be human-like and give a very short and nice response.Remember always provide a short response not more than 150 words ever. User : "

            // Check if the message starts with the initial sentence
            if (userMessage.startsWith(initialSentence)) {
                // If it does, set the initial sentence to a different style
                val actualUserMessage = userMessage.substring(initialSentence.length)
                binding.adapterUserTv.text = actualUserMessage
                binding.adapterUserCard.setBackgroundResource(R.drawable.card_radius_user)
                binding.adapterUserCard.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.user_background))
            }
        }
    }


    inner class GeminiViewHolder(private val binding: AdapterGeminiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Pair<String, Int>) {
            binding.adapterGeminiTv.text = message.first

            binding.adapterGeminiCard.setBackgroundResource(R.drawable.card_radius_gemini)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val binding =
                    AdapterUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UserViewHolder(binding)
            }

            VIEW_TYPE_GEMINI -> {
                val binding =
                    AdapterGeminiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                GeminiViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder.itemViewType) {
            VIEW_TYPE_USER -> {
                val userViewHolder = holder as UserViewHolder
                userViewHolder.bind(message)
            }

            VIEW_TYPE_GEMINI -> {
                val geminiViewHolder = holder as GeminiViewHolder
                geminiViewHolder.bind(message)
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return messages[position].second
    }

    companion object {
        const val VIEW_TYPE_USER = 1
        const val VIEW_TYPE_GEMINI = 2
    }
}