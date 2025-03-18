package com.desarrollo.servicioclientes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import com.desarrollo.common.entity.ClienteMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JmsConfig {

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT); // El mensaje JMS será texto (JSON)
        converter.setTypeIdPropertyName("_type");  // Propiedad para identificar el tipo de objeto

        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("ClienteMessage", ClienteMessage.class); // Nombre corto para el tipo
        converter.setTypeIdMappings(typeIdMappings);

        return converter;
    }
}
