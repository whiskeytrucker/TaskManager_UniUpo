package it.polettomatteo.taskmanager_uniupo.dataclass

import com.google.firebase.Timestamp
import java.io.Serializable

data class Message(
    val id: String = "",
    val sender: Boolean,
    val text: String = "",
    val timestamp: Timestamp
): Serializable
