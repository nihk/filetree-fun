package nick.template.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import nick.template.data.File
import nick.template.data.State

@Composable
fun MainContent(
    state: State,
    onAddRandomFiles: () -> Unit,
    onDirectoryClicked: (File.Directory) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddRandomFiles() }) {
                Icon(
                    painter = painterResource(id = state.fab),
                    contentDescription = state.fabContentDescription
                )
            }
        }
    ) {
        val files = state.files
        if (files == null) {
            Loading()
        } else {
            if (files.isEmpty()) {
                NoFiles()
            } else {
                FileTree(files = files, onDirectoryClicked = onDirectoryClicked)
            }
        }
    }
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = modifier.align(Alignment.Center))
    }
}

@Composable
fun NoFiles(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            modifier = modifier
                .align(Alignment.Center)
                .padding(16.dp),
            textAlign = TextAlign.Center,
            text = "There aren't any files yet. Try adding some by clicking the Floating Action Button."
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileTree(
    files: List<File>,
    onDirectoryClicked: (File.Directory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        itemsIndexed(
            items = files,
            key = { _, file -> file.absolutePath }
        ) { index, file ->
            Row(
                modifier = modifier
                    .padding(
                        start = 8.dp,
                        top = if (index == 0) 8.dp else 0.dp,
                        end = 8.dp,
                        bottom = 8.dp
                    )
                    .padding(start = file.depth * IconSize + file.depth * 8.dp)
                    .animateItemPlacement()
            ) {
                if (file is File.Directory) {
                    val rotate by animateFloatAsState(
                        targetValue = if (file.isExpanded) 90f else 0f
                    )
                    FileIcon(
                        icon = file.icon1,
                        modifier = modifier
                            .align(Alignment.CenterVertically)
                            .rotate(rotate)
                    )
                }
                Surface(
                    modifier = modifier
                        .run {
                            when (file) {
                                is File.Directory -> clickable { onDirectoryClicked(file) }
                                is File.Leaf -> padding(start = IconSize)
                            }
                        },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Row(
                        modifier = modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        val icon = when (file) {
                            is File.Directory -> file.icon2
                            is File.Leaf -> file.icon
                        }
                        FileIcon(icon = icon)
                        Text(
                            text = file.name,
                            modifier = modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FileIcon(@DrawableRes icon: Int, modifier: Modifier = Modifier) {
    Icon(
        modifier = modifier.size(IconSize),
        painter = painterResource(id = icon),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimary
    )
}

private val IconSize = 24.dp
