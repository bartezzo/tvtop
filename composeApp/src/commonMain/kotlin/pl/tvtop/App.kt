package pl.tvtop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            var query by remember { mutableStateOf("") }
            var fetched by remember { mutableStateOf<List<String>?>(null) }
            var loading by remember { mutableStateOf(false) }
            var error by remember { mutableStateOf<String?>(null) }
            val scope = rememberCoroutineScope()
            val epg = remember { EpgClient() }
            val sourceGroups = remember(fetched) {
                fetched?.let { listOf(StationGroup("epg.ovh", it)) } ?: stationGroups
            }
            val groups = remember(query, sourceGroups) {
                val seen = mutableSetOf<String>()
                sourceGroups.mapNotNull { group ->
                    val stations = group.stations.filter {
                        it.contains(query.trim(), ignoreCase = true) && seen.add(it)
                    }
                    if (stations.isEmpty()) null else StationGroup(group.name, stations)
                }
            }

            Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Stacje telewizyjne w Polsce",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                    }
                    Button(
                        enabled = !loading,
                        onClick = {
                            scope.launch {
                                loading = true
                                error = null
                                try {
                                    fetched = epg.fetchChannels()
                                } catch (e: Exception) {
                                    error = "Nie udało się pobrać listy: ${e.message ?: e::class.simpleName}"
                                } finally {
                                    loading = false
                                }
                            }
                        }
                    ) { Text("Refresh") }
                }
                error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp)
                    )
                }
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Szukaj stacji") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
                LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                    groups.forEach { group ->
                        item(key = "header-${group.name}") {
                            Text(
                                group.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth().padding(16.dp, 16.dp, 16.dp, 8.dp)
                            )
                        }
                        items(group.stations, key = { "${group.name}-$it" }) { station ->
                            Text(
                                station,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
