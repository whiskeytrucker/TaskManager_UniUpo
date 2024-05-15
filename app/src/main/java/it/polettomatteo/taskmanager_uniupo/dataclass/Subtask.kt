package it.polettomatteo.taskmanager_uniupo.dataclass

import com.google.firebase.Timestamp
import java.io.Serializable

data class Subtask(
    val id: String = "",
    val stato: String = "",
    val priorita: String = "",
    val scadenza: Timestamp,
    val progress: Int
): Serializable
