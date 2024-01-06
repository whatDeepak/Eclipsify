package com.vyarth.ellipsify.fragments

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.Observer
import com.vyarth.ellipsify.databinding.FragmentChatBinding
import com.vyarth.ellipsify.model.ChatAdapter
import com.vyarth.ellipsify.model.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding: FragmentChatBinding get() = _binding!!
    private val viewModel by viewModels<ChatViewModel>()
    private val chatAdapter = ChatAdapter()
    private val messageList = mutableListOf<Pair<String, Int>>()

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
    }
    private fun setAdapter(){
        binding.chatRv.adapter = chatAdapter
    }

    private fun sendMessage(){
        binding.chatSend.setOnClickListener {
            val userMessage = binding.chatPromptTextEt.text.toString()

            // Add the initial sentence before the user's message
            val initialSentence = "Behave as if you are a mental health consultant/therapist " +
                    "and give advice that is very genuine and helps the user. " +
                    "Be human-like and give a short and nice response."

            val completeMessage = "$initialSentence\n$userMessage"
            viewModel.geminiChat(completeMessage)
            messageList.add(Pair(completeMessage,ChatAdapter.VIEW_TYPE_USER))
            chatAdapter.setMessages(messageList)
            scrollPosition()
            binding.chatPromptTextEt.setText("")
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun observe(){
        viewModel.messageResponse.observe(viewLifecycleOwner){content ->
            content.text?.let {
                messageList.add(Pair(it,ChatAdapter.VIEW_TYPE_GEMINI))
                chatAdapter.setMessages(messageList)
                scrollPosition()
            }
        }
    }

    private fun scrollPosition(){
        binding.chatRv.smoothScrollToPosition(chatAdapter.itemCount - 1)

    }

}

