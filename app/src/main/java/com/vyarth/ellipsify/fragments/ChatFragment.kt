package com.vyarth.ellipsify.fragments

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.vyarth.ellipsify.databinding.FragmentChatBinding
import com.vyarth.ellipsify.adapters.ChatAdapter
import com.vyarth.ellipsify.model.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding: FragmentChatBinding get() = _binding!!
    private val viewModel by viewModels<ChatViewModel>()
    private val chatAdapter = ChatAdapter()
    private val messageList = mutableListOf<Pair<String, Int>>()

    private var isInitialGreetingShown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        sendMessage()
        observe()

        val tvTitle: TextView = binding.tvChatTitle
        val customTypeface = Typeface.createFromAsset(requireContext().assets, "epilogue_semibold.ttf")
        tvTitle.typeface = customTypeface
        // Show initial greeting message from Gemini if not shown yet
        if (!isInitialGreetingShown) {
            showInitialGreeting()
            isInitialGreetingShown = true
        }
    }
    private fun setAdapter(){
        binding.chatRv.adapter = chatAdapter
    }

    private fun sendMessage(){
        binding.chatSend.setOnClickListener {
            val userMessage = binding.chatPromptTextEt.text.toString()

            if(userMessage.isEmpty()){
                showEmptyMessage()
            }
            else{
                // Add the initial sentence before the user's message
                val initialSentence = "Behave as if you are a mental health consultant/therapist " +
                        "and give advice that is very genuine and helps the user. " +
                        "Be human-like and give a very short and nice response.Remember always provide a short response not more than 150 words ever. User : "

                val completeMessage = "$initialSentence $userMessage"
                viewModel.geminiChat(completeMessage)
                messageList.add(Pair(completeMessage, ChatAdapter.VIEW_TYPE_USER))
                chatAdapter.setMessages(messageList)
                scrollPosition()
                binding.chatPromptTextEt.setText("")
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
            }

        }
    }

    private fun showEmptyMessage() {
        val messageEmpty = "Feel free to share/ask whatever you want to! I am here to help you"
        messageList.add(Pair(messageEmpty, ChatAdapter.VIEW_TYPE_GEMINI))
        chatAdapter.setMessages(messageList)
        scrollPosition()
    }

    private fun observe(){
        viewModel.messageResponse.observe(viewLifecycleOwner){content ->
            content.text?.let {
                messageList.add(Pair(it, ChatAdapter.VIEW_TYPE_GEMINI))
                chatAdapter.setMessages(messageList)
                scrollPosition()
            }
        }
    }

    private fun scrollPosition(){
        binding.chatRv.smoothScrollToPosition(chatAdapter.itemCount - 1)

    }

    private fun showInitialGreeting() {
        val initialGreeting = "Hello! I'm here to help you. How can I assist you today?"
        messageList.add(Pair(initialGreeting, ChatAdapter.VIEW_TYPE_GEMINI))
        chatAdapter.setMessages(messageList)
        scrollPosition()
    }

}

