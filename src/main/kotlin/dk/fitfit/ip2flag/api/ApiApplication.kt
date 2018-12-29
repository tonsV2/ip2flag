package dk.fitfit.ip2flag.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@EnableFeignClients
@SpringBootApplication
open class ApiApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}

@RestController
class Ip2FlagController(private val countryClient: Ip2CountryClient) {
    private val logger: Logger = LoggerFactory.getLogger(Ip2FlagController::class.java)

    @GetMapping("/ip2flagemoji/{ip}")
    fun ip2emojiflag(@PathVariable ip: String): String {
        logger.info("/ip2flagemoji/$ip")
        TODO("https://stackoverflow.com/questions/42234666/get-emoji-flag-by-country-code/42235254")
    }

    @GetMapping(value = ["/ip2flag/{ip}/{size}"], produces = [MediaType.IMAGE_PNG_VALUE])
    fun ip2flagWithSize(@PathVariable ip: String, @PathVariable size: Int): Resource {
        logger.info("/ip2flag/$ip/$size")
        val locale: Locale = ip2locale(ip)
        return findResource(locale.displayCountry, size)
    }

    @GetMapping(value = ["/ip2flag/{ip}"], produces = [MediaType.IMAGE_PNG_VALUE])
    fun ip2flag(@PathVariable ip: String): Resource {
        logger.info("/ip2flag/$ip")
        return ip2flagWithSize(ip, 16)
    }

    @GetMapping("/ip2country/{ip}")
    fun ip2country(@PathVariable ip: String): String {
        logger.info("/ip2country/$ip")
        val locale: Locale = ip2locale(ip)
        return locale.displayCountry
    }

    private fun ip2locale(ip: String): Locale {
        val country = countryClient.findCountry(ip)
        return Locale("", country)
    }

    private fun findResource(country: String, size: Int = 16): Resource {
        val locationPattern = "images/Final Flags/PNG/$size/*${country.toLowerCase()}*.png"
        val resources = PathMatchingResourcePatternResolver().getResources(locationPattern)
        return resources[0]
    }
}

@FeignClient(name = "\${feign.name}", url = "\${feign.url}")
interface Ip2CountryClient {
    @GetMapping("/ip2country/{ip}")
    fun findCountry(@PathVariable ip: String): String
}
