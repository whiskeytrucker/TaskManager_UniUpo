package it.polettomatteo.taskmanager_uniupo.dataclass

import com.google.firebase.Timestamp
import java.io.Serializable

data class Chat(
    var id: String = "",
    var from: String = "",
    var to: String = "",
    val lastMessage: String = "",
    val lastMessageDate: Timestamp
): Serializable
