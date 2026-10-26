package pl.tvtop

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.BufferedSource
import okio.GzipSource
import okio.buffer

const val EPG_URL = "https://epg.ovh/pltv.gz"

expect fun nowMillis(): Long

data class Channel(val id: String, val name: String)

data class Programme(
    val title: String,
    val description: String?,
    val category: String?,
    val startMillis: Long,
    val stopMillis: Long,
    /** Local (EPG) date, yyyy-MM-dd. */
    val day: String,
    /** Local (EPG) start time, HH:mm. */
    val time: String,
)

class EpgData(
    val channels: List<Channel>,
    private val programmes: Map<String, List<Programme>>,
) {
    fun programmesFor(channelId: String): List<Programme> = programmes[channelId].orEmpty()
}

object EpgRepository {
    private val client by lazy { HttpClient() }

    /** Downloads the gzipped XMLTV file, decompresses it on the fly and parses it. */
    suspend fun load(): EpgData {
        val bytes = client.get(EPG_URL).readRawBytes()
        return withContext(Dispatchers.Default) {
            val source = GzipSource(Buffer().write(bytes)).buffer()
            parseXmlTv(source, nowMillis())
        }
    }
}

private const val MAX_DESCRIPTION = 400

internal fun parseXmlTv(source: BufferedSource, now: Long): EpgData {
    val names = LinkedHashMap<String, String>()
    val byChannel = HashMap<String, MutableList<Programme>>()

    var channelId: String? = null
    var inProgramme = false
    var pChannel = ""
    var pStart = ""
    var pStop = ""
    var pTitle: String? = null
    var pDesc: String? = null
    var pCategory: String? = null

    while (true) {
        val line = source.readUtf8Line() ?: break
        val l = line.trim()
        when {
            l.startsWith("<channel ") -> channelId = attr(l, "id")
            l.startsWith("</channel>") -> channelId = null
            l.startsWith("<display-name") -> {
                val id = channelId
                if (id != null && id !in names) names[id] = text(l)
            }
            l.startsWith("<programme ") -> {
                inProgramme = true
                pChannel = attr(l, "channel").orEmpty()
                pStart = attr(l, "start").orEmpty()
                pStop = attr(l, "stop").orEmpty()
                pTitle = null; pDesc = null; pCategory = null
            }
            l.startsWith("<title") && inProgramme -> if (pTitle == null) pTitle = text(l)
            l.startsWith("<category") && inProgramme -> if (pCategory == null) pCategory = text(l)
            l.startsWith("<desc") && inProgramme -> {
                // Descriptions may span several lines.
                var raw = l
                while (!raw.contains("</desc>")) {
                    raw += "\n" + (source.readUtf8Line() ?: break)
                }
                if (pDesc == null) pDesc = text(raw).trim().take(MAX_DESCRIPTION).ifEmpty { null }
            }
            l.startsWith("</programme>") -> {
                inProgramme = false
                val start = parseTime(pStart)
                val stop = parseTime(pStop)
                if (start != null && stop != null && stop >= now) {
                    byChannel.getOrPut(pChannel) { ArrayList() }.add(
                        Programme(
                            title = pTitle?.trim().orEmpty().ifEmpty { "Bez tytułu" },
                            description = pDesc,
                            category = pCategory,
                            startMillis = start,
                            stopMillis = stop,
                            day = "${pStart.substring(0, 4)}-${pStart.substring(4, 6)}-${pStart.substring(6, 8)}",
                            time = "${pStart.substring(8, 10)}:${pStart.substring(10, 12)}",
                        )
                    )
                }
            }
        }
    }

    val channels = names.filterKeys { it in byChannel }.map { (id, name) -> Channel(id, name) }
    byChannel.values.forEach { list -> list.sortBy { it.startMillis } }
    return EpgData(channels, byChannel)
}

private fun attr(line: String, name: String): String? {
    val marker = " $name=\""
    val i = line.indexOf(marker)
    if (i < 0) return null
    val from = i + marker.length
    val end = line.indexOf('"', from)
    if (end < 0) return null
    return decode(line.substring(from, end))
}

private fun text(raw: String): String {
    val start = raw.indexOf('>') + 1
    val end = raw.indexOf("</", start).let { if (it < 0) raw.length else it }
    return decode(raw.substring(start, end))
}

private fun decode(s: String): String {
    if ('&' !in s) return s
    val sb = StringBuilder()
    var i = 0
    while (i < s.length) {
        val c = s[i]
        if (c == '&') {
            val semi = s.indexOf(';', i)
            if (semi > i && semi - i <= 8) {
                val entity = s.substring(i + 1, semi)
                val replacement = when {
                    entity == "amp" -> "&"
                    entity == "lt" -> "<"
                    entity == "gt" -> ">"
                    entity == "quot" -> "\""
                    entity == "apos" -> "'"
                    entity.startsWith("#x") -> entity.substring(2).toIntOrNull(16)?.toChar()?.toString()
                    entity.startsWith("#") -> entity.substring(1).toIntOrNull()?.toChar()?.toString()
                    else -> null
                }
                if (replacement != null) {
                    sb.append(replacement)
                    i = semi + 1
                    continue
                }
            }
        }
        sb.append(c)
        i++
    }
    return sb.toString()
}

/** Parses "yyyyMMddHHmmss +hhmm" into epoch millis. */
private fun parseTime(s: String): Long? {
    if (s.length < 14) return null
    return try {
        val y = s.substring(0, 4).toInt()
        val mo = s.substring(4, 6).toInt()
        val d = s.substring(6, 8).toInt()
        val h = s.substring(8, 10).toInt()
        val mi = s.substring(10, 12).toInt()
        val sec = s.substring(12, 14).toInt()
        var offset = 0
        val tz = s.substring(14).trim()
        if (tz.length >= 5) {
            val sign = if (tz[0] == '-') -1 else 1
            offset = sign * (tz.substring(1, 3).toInt() * 3600 + tz.substring(3, 5).toInt() * 60)
        }
        (daysFromCivil(y, mo, d) * 86400L + h * 3600 + mi * 60 + sec - offset) * 1000L
    } catch (e: NumberFormatException) {
        null
    }
}

// Howard Hinnant's days-from-civil algorithm.
private fun daysFromCivil(year: Int, month: Int, day: Int): Long {
    val y = if (month <= 2) year - 1 else year
    val era = (if (y >= 0) y else y - 399) / 400
    val yoe = y - era * 400
    val doy = (153 * (month + (if (month > 2) -3 else 9)) + 2) / 5 + day - 1
    val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
    return era * 146097L + doe - 719468L
}
