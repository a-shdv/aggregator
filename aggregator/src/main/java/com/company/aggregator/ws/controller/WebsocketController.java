package com.company.aggregator.ws.controller;

import com.company.aggregator.dto.RequestDto;
import com.company.aggregator.dto.VacancyDto;
import com.company.aggregator.exception.FavouritesIsEmptyException;
import com.company.aggregator.entity.Favourite;
import com.company.aggregator.entity.User;
import com.company.aggregator.rabbitmq.dto.vacancies.SendMessageDto;
import com.company.aggregator.rabbitmq.service.RabbitMqService;
import com.company.aggregator.service.*;
import com.company.aggregator.ws.dto.WebSocketReceiveMessageDto;
import com.company.aggregator.ws.dto.WebSocketSendMessageDto;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
public class WebsocketController {
    private final RabbitMqService rabbitMqService;

    private final FavouriteService favouriteService;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailSenderService emailSenderService;
    private final UserService userService;
    private final SimpMessageSendingOperations messageSendingOperations;
    private final RequestService requestService;
    private final VacancyService vacancyService;


    //registry.setApplicationDestinationPrefixes("/app")
    //messages from clients
    //routed... | full address - /app/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public VacancyDto receiveMessageFromWs(@RequestBody VacancyDto vacancyDto) {
        User user = userService.findUserByUsername(vacancyDto.getUsername());
        vacancyService.deleteVacanciesByUser(user);

        rabbitMqService.sendToVacanciesParser(SendMessageDto.builder()
                .username(vacancyDto.getUsername())
                .title(vacancyDto.getTitle())
                .salary(vacancyDto.getSalary())
                .onlyWithSalary(vacancyDto.getOnlyWithSalary())
                .experience(vacancyDto.getExperience())
                .cityId(vacancyDto.getCityId())
                .isRemoteAvailable(vacancyDto.getIsRemoteAvailable())
                .numOfRequests(0)
                .build());

        requestService.save(new RequestDto(
                vacancyDto.getUsername(),
                vacancyDto.getTitle(),
                vacancyDto.getSalary(),
                vacancyDto.getOnlyWithSalary(),
                vacancyDto.getExperience(),
                vacancyDto.getCityId(),
                vacancyDto.getIsRemoteAvailable(),
                0
                ));

        return vacancyDto;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public WebSocketReceiveMessageDto addUser(@Payload WebSocketReceiveMessageDto webSocketReceiveMessageDto,
                                              SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", webSocketReceiveMessageDto.getSender());
        return webSocketReceiveMessageDto;
    }

    @MessageMapping("/chat.spin")
    @SendTo("/topic/public")
    public WebSocketReceiveMessageDto spin(@Payload WebSocketReceiveMessageDto webSocketReceiveMessageDto,
                                           SimpMessageHeaderAccessor headerAccessor) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        String pdfPath = System.getProperty("user.home") + "/Downloads/report-" + UUID.randomUUID() + ".pdf";

        CompletableFuture.supplyAsync(() -> {
                    List<Favourite> favourites = null;
                    User user;
                    try {
                        user = userService.findUserByUsername(webSocketReceiveMessageDto.getSender());
                        favourites = favouriteService.findByUser(user);
                    } catch (FavouritesIsEmptyException e) {
                        future.completeExceptionally(e);
                    }
                    return favourites;
                })
                .thenAccept((favourites) -> pdfGeneratorService.generatePdf(favourites, pdfPath))
                .thenRun(() -> {
                    try {
                        emailSenderService.sendEmailWithAttachment("shadaev2001@icloud.com", "Избранные вакансии", "", pdfPath);
                        future.complete(null);
                    } catch (MessagingException | FileNotFoundException e) {
                        future.completeExceptionally(e);
                    }
                });


        future.handle((res, ex) -> {
            if (ex != null) {
                messageSendingOperations.convertAndSend("/topic/public", WebSocketSendMessageDto.builder().content("-1").type("RECEIVE").build());
            } else {
                messageSendingOperations.convertAndSend("/topic/public", WebSocketSendMessageDto.builder().content("0").type("RECEIVE").build());
            }
            return null;
        }).join();

        return webSocketReceiveMessageDto;
    }
}
