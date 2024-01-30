package com.company.parser.service

import com.company.parser.rabbitmq.dto.SendMessageDto
import com.company.parser.rabbitmq.service.RabbitMqSenderService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

@Service
class RabotaRuParserService(private val rabbitMqSenderService: RabbitMqSenderService) {
    companion object {
        private val log = LoggerFactory.getLogger(RabotaRuParserService.javaClass)
    }

    fun findAllVacancies(
        query: String, amount: Int, salary: BigDecimal, onlyWithSalary: Boolean,
        experience: Int, cityId: Int, isRemoteAvailable: Boolean
    ): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            var previousPage: Int
            var currentPage: Int = 1
            val url: StringBuilder = StringBuilder(
                "https://www.rabota.ru/vacancy/" +
                        "?query=$query" +
                        "&min_salary=$salary" +
                        "&experience_ids=${parseExperience(experience)}"
            )

            val parsedCityId: String = parseCityId(cityId)
            if (parsedCityId.isNotEmpty()) {
                url.replace(0, 30, "https://$parsedCityId.rabota.ru/vacancy/")
            }

            if (isRemoteAvailable) {
                url.append("&schedule_ids=6")
            }

            val doc: Document? = connectDocumentToUrl(url.toString())
            var elements: Elements? = null
            if (doc != null) {
                elements = doc.getElementsByClass("r-serp__item r-serp__item_vacancy")
            }

            if (elements != null) {
                val sendMessageDtoList: ArrayList<SendMessageDto> = arrayListOf()
                val sendMessageDtoListMaxSize = 10

                while (currentPage <= amount / elements.size) {
                    elements.forEach {
                        val source: String = it.getElementsByAttribute("href").first().absUrl("href")

                        val title: String = it.getElementsByClass("vacancy-preview-card__title").first().text()
                        val date: String = parseUpdatedDate(source).toString()
                        val vacancySalary: String = it.getElementsByClass("vacancy-preview-card__salary").first().text()
                        val requirements: String = "Нет поддержки ключевых слов для rabota.ru."
                        val company: String = it.getElementsByClass("vacancy-preview-card__company-name").first().text()
                        val description: String =
                            it.getElementsByClass("vacancy-preview-card__short-description").first().text()
                        val schedule: String =
                            it.getElementsByClass("vacancy-preview-location__address-text").first().text()

                        val message: SendMessageDto = SendMessageDto(
                            title,
                            date,
                            vacancySalary,
                            company,
                            requirements,
                            description,
                            schedule,
                            source
                        )
                        sendMessageDtoList.add(message)
                    }


                    previousPage = currentPage;
                    currentPage++;
                    url.replace(
                        url.indexOf("?page=$previousPage"),
                        url.lastIndexOf("?page=$previousPage"),
                        "?page=$currentPage"
                    )

                    if (sendMessageDtoList.size > sendMessageDtoListMaxSize) {
                        rabbitMqSenderService.send(sendMessageDtoList)
                        sendMessageDtoList.clear()
                    }
                }
            } else {
                log.error("Could not parse elements")
            }
        }
    }

    private fun parseExperience(experience: Int): String {
        var result: String = ""
        when (experience) {
            1 -> result = "0" // нет опыта - нет опыта
            2 -> result = "2" // 1-2 года - 1-3 года
            3 -> result = "3" // 3-4 года - 3-6 лет
        }
        return result
    }

    private fun parseCityId(cityId: Int): String {
        var parsedCityId: String = ""
        when (cityId) {
            0 -> parsedCityId = ""  // Москва
            1 -> parsedCityId = "spb" // СПБ
            2 -> parsedCityId = "eburg" // ЕКБ
            3 -> parsedCityId = "nsk" // Новосибирск
            4 -> parsedCityId = "kazan" // Казань
            5 -> parsedCityId = "nn" // Нижний Новгород
            6 -> parsedCityId = "ulv" // Ульяновск
            7 -> parsedCityId = "tol" // Тольятти
            8 -> parsedCityId = "astrakhan" // Астрахань
            9 -> parsedCityId = "ufa" // Уфа
        }
        return parsedCityId
    }

    private fun parseUpdatedDate(url: String): String? {
        var doc: Document? = null
        try {
            doc = Jsoup.connect(url).get()
        } catch (ex: IOException) {
            log.error(ex.message)
        }

        if (doc != null) {
            return doc.getElementsByClass("vacancy-system-info__updated-date").text()
        }
        return null
    }

    private fun connectDocumentToUrl(url: String): Document? {
        try {
            return Jsoup.connect(url).get()
        } catch (e: IOException) {
            log.error(e.message)
        }
        return null
    }
}