package com.company.aggregator.rabbitmq.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ReceiveMessageDto implements Serializable {
//    private ErrorCode errorCode;
//    private Color color;
//    private ColorMode colorMode;
}

// 1 тип сообщения - ошибка
// error code
// color
// color mode

// 2 тип сообщения - свет, звук, конвейер
// параметр который управляет включением-выключением света, звука или конвейера

// снятие ошибки: присылается color mode=0 (и свет и звук) если физическки нажали на кнопку и свет и звук выкл (снимается ошибка)
// запуск линии: conveyor_on
// auto: ручной режим не стопает линию