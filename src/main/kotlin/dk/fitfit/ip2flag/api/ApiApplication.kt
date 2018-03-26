package dk.fitfit.ip2flag.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*


@SpringBootApplication
open class ApiApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}

@RestController
class Ip2FlagController {
    @GetMapping("/ip2flagemoji/{ip}")
    fun ip2emojiflag(@PathVariable ip: String): String {
        TODO("https://stackoverflow.com/questions/42234666/get-emoji-flag-by-country-code/42235254")
    }

    @GetMapping(value = ["/ip2flag/{ip}/{size}"], produces = [MediaType.IMAGE_PNG_VALUE])
    fun ip2flagWithSize(@PathVariable ip: String, @PathVariable size: Int): Resource {
        // TODO: Service - Locale by ip...
        val data = lookupIp(ip)
        val locale: Locale = toLocale(data)
        // TODO: FlagByLocaleService
        return findResource(locale, size)
    }

    private fun lookupIp(ip: String): String {
        return readStringFromURL("http://ip2c.org/$ip")
    }

    @GetMapping(value = ["/ip2flag/{ip}"], produces = [MediaType.IMAGE_PNG_VALUE])
    fun ip2flag(@PathVariable ip: String): Resource {
        return ip2flagWithSize(ip, 16)
    }

    private fun findResource(locale: Locale, size: Int = 16): Resource {
        val locationPattern = "images/Final Flags/PNG/$size/*${locale.displayCountry.toLowerCase()}*.png"
        val resolver = PathMatchingResourcePatternResolver()
        val resources = resolver.getResources(locationPattern)
        return resources[0]
    }

    private fun toLocale(data: String): Locale {
        val split = data.split(";")
        return when (split[0]) {
            "0" -> throw Exception("Soemthing went wrong")
            "1" -> Locale("", split[1])
            "2" -> throw Exception("Not found in database")
            else -> throw IllegalArgumentException("Not sure what happened...")
        }
    }

    @Throws(IOException::class)
    private fun readStringFromURL(requestURL: String): String {
        val stream = URL(requestURL).openStream()
        Scanner(stream, StandardCharsets.UTF_8.toString()).use({ scanner ->
            scanner.useDelimiter("\\A")
            return if (scanner.hasNext()) scanner.next() else ""
        })
    }
}
