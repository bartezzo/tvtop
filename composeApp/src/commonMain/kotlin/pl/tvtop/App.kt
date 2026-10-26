package pl.tvtop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private sealed interface EpgState {
    data object Loading : EpgState
    data class Error(val message: String) : EpgState
    data class Ready(val data: EpgData) : EpgState
}

@Composable
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            var state by remember { mutableStateOf<EpgState>(EpgState.Loading) }
            var selected by remember { mutableStateOf<Channel?>(null) }
            var query by remember { mutableStateOf("") }
            val listState = rememberLazyListState()
            val scope = rememberCoroutineScope()
            var job by remember { mutableStateOf<Job?>(null) }

            fun refresh() {
                job?.cancel()
                state = EpgState.Loading
                job = scope.launch {
                    state = try {
                        EpgState.Ready(EpgRepository.load())
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Throwable) {
                        EpgState.Error(e.message ?: e.toString())
                    }
                    selected = null
                    listState.scrollToItem(0)
                }
            }

            LaunchedEffect(Unit) { refresh() }

            val current = selected
            val ready = state as? EpgState.Ready
            if (current != null && ready != null) {
                ProgrammeScreen(current, ready.data.programmesFor(current.id)) { selected = null }
                return@Surface
            }

            Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 16.dp)
                ) {
                    Text(
                        "Stacje telewizyjne w Polsce",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { query = ""; refresh() }, enabled = state !is EpgState.Loading) {
                        Text("Refresh")
                    }
                }
                when (val s = state) {
                    EpgState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Text("Pobieranie programu TV…", modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                    is EpgState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(
                            "Nie udało się pobrać programu:\n${s.message}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    is EpgState.Ready -> {
                        val channels = remember(s.data, query) {
                            s.data.channels.filter { it.name.contains(query.trim(), ignoreCase = true) }
                        }
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            label = { Text("Szukaj stacji") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        )
                        LazyColumn(state = listState, contentPadding = PaddingValues(bottom = 16.dp)) {
                            items(channels, key = { it.id }) { channel ->
                                Text(
                                    channel.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable { selected = channel }
                                        .padding(horizontal = 16.dp, vertical = 14.dp)
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgrammeScreen(channel: Channel, programmes: List<Programme>, onBack: () -> Unit) {
    val now = remember { nowMillis() }
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            TextButton(onClick = onBack) { Text("← Wstecz") }
            Text(
                channel.name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider()
        if (programmes.isEmpty()) {
            Text("Brak programu dla tej stacji.", modifier = Modifier.padding(16.dp))
            return@Column
        }
        val byDay = remember(programmes) { programmes.groupBy { it.day } }
        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            byDay.forEach { (day, list) ->
                item(key = "day-$day") {
                    Text(
                        day,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth().padding(16.dp, 16.dp, 16.dp, 8.dp)
                    )
                }
                items(list, key = { "${it.startMillis}-${it.title}" }) { p ->
                    val live = p.startMillis <= now && now < p.stopMillis
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            p.time,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (live) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.width(48.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                p.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (live) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            p.category?.let {
                                Text(it, style = MaterialTheme.typography.labelMedium)
                            }
                            p.description?.let {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
