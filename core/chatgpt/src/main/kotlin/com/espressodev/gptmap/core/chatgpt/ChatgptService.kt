package com.espressodev.gptmap.core.chatgpt

import com.espressodev.gptmap.core.model.chatgpt.Location
import com.espressodev.gptmap.core.model.chatgpt.ChatgptRequest

interface ChatgptService {
    suspend fun getPrompt(message: ChatgptRequest): Location
}