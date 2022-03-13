package nick.template.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transformLatest
import nick.template.data.Effect
import nick.template.data.Event
import nick.template.data.Result
import nick.template.data.State
import nick.template.usecase.Filesystem

class MainViewModel(
    private val filesystem: Filesystem
) : MviViewModel<Event, Result, State, Effect>(State()) {

    override fun onSubscription() {
        processEvent(Event.LoadFiles)
    }

    override fun Flow<Event>.toResults(): Flow<Result> {
        return merge(
            filterIsInstance<Event.LoadFiles>().onLoadFiles(),
            filterIsInstance<Event.ToggleDirectory>().onExpandDirectory(),
            filterIsInstance<Event.AddRandomFiles>().onAddRandomFiles()
        )
    }

    private fun Flow<Event.LoadFiles>.onLoadFiles(): Flow<Result> {
        return flatMapLatest { filesystem.files }
            .map { files -> Result.LoadedFiles(files) }
    }

    private fun Flow<Event.ToggleDirectory>.onExpandDirectory(): Flow<Result> {
        return transformLatest { event -> filesystem.toggle(event.file) }
    }

    private fun Flow<Event.AddRandomFiles>.onAddRandomFiles(): Flow<Result> {
        return transformLatest { filesystem.addRandomFiles() }
    }

    override fun Result.reduce(state: State): State {
        return when (this) {
            is Result.LoadedFiles -> state.copy(isLoading = false, files = files)
            else -> state
        }
    }

    class Factory @Inject constructor(private val filesystem: Filesystem) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(filesystem) as T
        }
    }
}
