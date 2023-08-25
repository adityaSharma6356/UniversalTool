package com.example.fifthsemproject.domain.models


data class SingleConversation(
    var startingTime: Long = 0L,
    val conversation: MutableList<SingleInteraction> = mutableListOf()
)

data class SingleInteraction(
    val role: String,
    var content: String,
    var time: Long = 0L,
    val errorMessage: String? = null,
)
