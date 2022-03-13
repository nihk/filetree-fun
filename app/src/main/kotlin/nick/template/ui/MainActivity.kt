package nick.template.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import nick.template.data.Event

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: MainViewModel.Factory
    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainContent(
                    state = viewModel.states.collectAsState().value,
                    onAddRandomFiles = { viewModel.processEvent(Event.AddRandomFiles) },
                    onDirectoryClicked = { file -> viewModel.processEvent(Event.ToggleDirectory(file)) }
                )
            }
        }
    }
}
