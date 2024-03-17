package com.company.aggregator.websockets.controllers;

import com.company.aggregator.dtos.VacancyDto;
import com.company.aggregator.exceptions.FavouritesIsEmptyException;
import com.company.aggregator.models.Favourite;
import com.company.aggregator.models.User;
import com.company.aggregator.rabbitmq.dtos.SendMessageDto;
import com.company.aggregator.rabbitmq.services.RabbitMqService;
import com.company.aggregator.services.EmailSenderService;
import com.company.aggregator.services.FavouriteService;
import com.company.aggregator.services.PdfGeneratorService;
import com.company.aggregator.services.UserService;
import com.company.aggregator.websockets.dtos.WebSocketReceiveMessageDto;
import com.company.aggregator.websockets.dtos.WebSocketSendMessageDto;
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


    //registry.setApplicationDestinationPrefixes("/app")
    //messages from clients
    //routed... | full address - /app/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public VacancyDto receiveMessageFromWs(@RequestBody VacancyDto vacancyDto) {
        rabbitMqService.send(SendMessageDto.builder()
                .username(vacancyDto.getUsername())
                .title(vacancyDto.getTitle())
                .salary(vacancyDto.getSalary())
                .onlyWithSalary(vacancyDto.getOnlyWithSalary())
                .experience(vacancyDto.getExperience())
                .cityId(vacancyDto.getCityId())
                .isRemoteAvailable(vacancyDto.getIsRemoteAvailable())
                .build());
        return vacancyDto;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public WebSocketReceiveMessageDto addUser(@Payload WebSocketReceiveMessageDto webSocketReceiveMessageDto,
                                              SimpMessageHeaderAccessor headerAccessor) {
        //add username in web socket session
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
                .thenAccept((favourites) -> pdfGeneratorService
                        .generatePdf(favourites, pdfPath))
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
//            messageSendingOperations.convertAndSend("/topic/public", WebsocketDto.builder().counter(100).type("RECEIVE").build());
            return null;
        }).join();

        return webSocketReceiveMessageDto;
    }
}
