package it.polettomatteo.taskmanager_uniupo.dataclass

import java.io.Serializable

data class Project(
    var titolo: String = "",
    var descr: String = "",
    var progress: Int = 0
): Serializable