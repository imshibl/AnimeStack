package com.bilcodes.animestack

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bilcodes.animestack.adapter.ChatAdapter
import com.bilcodes.animestack.model.ChatMessage
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class StackBotChatActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatMessageEditText: TextInputEditText
    private lateinit var sendButton: ImageView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var model: GenerativeModel
    private lateinit var chat: Chat

    private val messages = mutableListOf<ChatMessage>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_stack_bot_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.back)
        toolbar.setNavigationOnClickListener { finish() }




        initializeModel()

        chatRecyclerView = findViewById(R.id.recyclerView)
        chatMessageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendMessageButton)
        chatAdapter = ChatAdapter(this, messages);



        chatRecyclerView.layoutManager =  LinearLayoutManager(this);
        (chatRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true;
        chatRecyclerView.adapter = chatAdapter
        chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount)




        sendButton.setOnClickListener {
            chatAdapter.addMessage(ChatMessage( chatMessageEditText.text.toString(), "user"))
            sendMessage()
        }



    }

    private fun initializeModel() {
        val apiKey = BuildConfig.geminiApiKey
        model = GenerativeModel(
            "gemini-1.5-flash",
            apiKey,
            generationConfig = generationConfig {
                temperature = 1f
                topK = 64
                topP = 0.95f
                maxOutputTokens = 8192
                responseMimeType = "text/plain"
            },
            systemInstruction = content { text("you are an AI bot named Stack, an expert in Japanese anime shows and movies. Your major capabilities are suggesting anime based on the user's needs and requirements, and conversing about user-specified anime shows and movies. You are not able to answer anything that is not related to anime. also, you are created and designed by Bilcodes, who is also the developer of Animestack an app that has 40k+ anime details with an offline watch list.") }
        )

        chat = model.startChat()
    }

    private fun sendMessage() {
        val userMessage = chatMessageEditText.text.toString().trim()
        if (userMessage.isNotEmpty()) {
//            chatAdapter.addMessage(ChatMessage("user", userMessage))
            chatMessageEditText.text?.clear()

            lifecycleScope.launch {
                try {
                    val response = chat.sendMessage(userMessage)
                    val aiResponse = response.text ?: "Sorry, I couldn't generate a response."
//                    chatAdapter.addMessage(ChatMessage("model", aiResponse))
                    chatAdapter.addMessage(ChatMessage(aiResponse, "AI"))


                } catch (e: Exception) {
//                    chatAdapter.addMessage(ChatMessage("model", "Error: ${e.message}"))
                    chatAdapter.addMessage(ChatMessage(
                        "Sorry, I couldn't generate a response.",
                        "AI"
                    ))

                }
            }
        }
    }


}



