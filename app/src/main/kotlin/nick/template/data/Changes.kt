package nick.template.data

import androidx.annotation.DrawableRes
import nick.template.R

sealed class Event {
    object LoadFiles : Event()
    data class ToggleDirectory(val file: File.Directory) : Event()
    object AddRandomFiles : Event()
}

sealed class Result {
    data class LoadedFiles(val files: List<File>) : Result()
}

sealed class Effect

data class State(
    val isLoading: Boolean = true,
    val files: List<File>? = null,
    @DrawableRes val fab: Int = R.drawable.add,
    val fabContentDescription: String = "Add"
)
