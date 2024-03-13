package com.company.aggregator.websocket.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WebSocketProgressBarMessage {
    private String type;
    private String content;
    private String sender;
}
