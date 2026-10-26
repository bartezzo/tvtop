package pl.tvtop

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

// Assumed XMLTV feed; adjust if epg.ovh serves the guide under a different path.
const val EPG_URL = "https://epg.ovh/pl.xml"

private val channelRegex = Regex("<channel\\b[^>]*>(.*?)</channel>", RegexOption.DOT_MATCHES_ALL)
private val displayNameRegex = Regex("<display-name\\b[^>]*>(.*?)</display-name>", RegexOption.DOT_MATCHES_ALL)

class EpgClient(private val client: HttpClient = HttpClient()) {
    suspend fun fetchChannels(url: String = EPG_URL): List<String> =
        parseChannels(client.get(url).bodyAsText())
}

internal fun parseChannels(xml: String): List<String> =
    channelRegex.findAll(xml)
        .mapNotNull { displayNameRegex.find(it.groupValues[1])?.groupValues?.get(1) }
        .map { decodeEntities(it.trim()) }
        .filter { it.isNotEmpty() }
        .distinct()
        .toList()

private fun decodeEntities(s: String) = s
    .replace("&lt;", "<")
    .replace("&gt;", ">")
    .replace("&quot;", "\"")
    .replace("&apos;", "'")
    .replace("&amp;", "&")
