package com.company.parser.service

import com.company.parser.rabbitmq.dto.SendMessageDto
import com.company.parser.rabbitmq.service.RabbitMqSenderService
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import java.io.IOException
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SuperJobParserService(private val rabbitMqSenderService: RabbitMqSenderService) {
    companion object {
        private val log = LoggerFactory.getLogger(SuperJobParserService.javaClass)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun findAllVacancies() {
        val query: String = "java"
        val amount: Int = 100

        var previousPage: Int
        var currentPage: Int = 1
        var url: StringBuilder = StringBuilder("https://www.rabota.ru/vacancy/?query=java")
        var doc: Document? = connectDocumentToUrl(url.toString())

        var elements: Elements? = null
        if (doc != null) {
            elements = doc.getElementsByClass("r-serp__item r-serp__item_vacancy")
        }

        if (elements != null) {
            while (currentPage <= amount / elements.size) {
                elements.forEach {
                    val source: String = it.getElementsByAttribute("href").first().absUrl("href")
                    val title: String = it.getElementsByClass("vacancy-preview-card__title").first().text()
                    // date
                    val salaray: String = it.getElementsByClass("vacancy-preview-card__salary").first().text()
                    val company: String = it.getElementsByClass("vacancy-preview-card__company-name").first().text()
                    // req
                    val description: String = it.getElementsByClass("vacancy-preview-card__short-description").first().text()
                    val schedule: String = it.getElementsByClass("vacancy-preview-location__address-text").first().text()
                    println()
                }


                previousPage = currentPage;
                currentPage++;
            }
        }
    }

    private fun parseVacancyWebPage(url: String): SendMessageDto {
        var doc: Document? = null
        try {
             doc = Jsoup.connect(url).get()
        } catch (ex: IOException) {
            log.error(ex.message)
        }

        if (doc != null) {
            return SendMessageDto()
        }
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