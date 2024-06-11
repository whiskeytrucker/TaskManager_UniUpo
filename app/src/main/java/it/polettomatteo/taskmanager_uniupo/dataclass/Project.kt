package it.polettomatteo.taskmanager_uniupo.dataclass

import java.io.Serializable

data class Project(
    var id: String = "",
    var titolo: String = "",
    var descr: String = "",
    var assigned: String = "",
    var autore: String = "",
    var progress: Int = 0
): Serializable