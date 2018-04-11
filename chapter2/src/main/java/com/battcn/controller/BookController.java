package com.battcn.controller;

import com.battcn.config.RabbitConfig;
import com.battcn.entity.Book;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Levin
 * @since 2018/4/2 0002
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/books")
@Api(value = "消息队列", description = "消息队列", tags = {"1.0"})
public class BookController {

    private final RabbitTemplate rabbitTemplate;
    private final AmqpTemplate amqpTemplate;

    @Autowired
    public BookController(AmqpTemplate amqpTemplate, RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.amqpTemplate = amqpTemplate;
    }

    @ApiOperation("添加消息")
    @PostMapping
    public void defaultMessage(@RequestBody Book book) {
        /*
        this.rabbitTemplate.convertAndSend(RabbitConfig.DEFAULT_BOOK_QUEUE, book);
        this.rabbitTemplate.convertAndSend(RabbitConfig.MANUAL_BOOK_QUEUE, book);
        // 添加延时队列
        this.rabbitTemplate.convertAndSend(RabbitConfig.BOOK_DELAY_EXCHANGE, RabbitConfig.BOOK_DELAY_EXCHANGE, book, message -> {
            message.getMessageProperties().setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, Book.class.getName());
            message.getMessageProperties().setExpiration(2 * 1000 + "");
            message.getMessageProperties().setDelay(2 * 1000);
            return message;
        });*/
        rabbitTemplate.setExchange(RabbitConfig.REGISTER_DELAY_EXCHANGE_NAME);
        rabbitTemplate.setRoutingKey("");
        rabbitTemplate.convertAndSend(book, message -> {
            message.getMessageProperties().setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, Book.class.getName());
            message.getMessageProperties().setExpiration(2 * 1000 + "");
            return message;
        });
    }


}