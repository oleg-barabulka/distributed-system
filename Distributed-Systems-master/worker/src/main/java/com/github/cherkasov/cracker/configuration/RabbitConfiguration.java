package com.github.cherkasov.cracker.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Value("${tasks.queue.name}")
    private String tasksQueueName;

    @Value("${results.queue.name}")
    private String taskResultsQueueName;

    @Value("${percent.queue.name}")
    private String percentsQueueName;

    @Getter
    @Value("${rabbitmq.exchange.name}")
    private String exchangeQueueName;

    @Getter
    @Value("${rabbitmq.manager.request.routing.key}")
    private String managerRequestRoutingKey;

    @Getter
    @Value("${rabbitmq.worker.response.routing.key}")
    private String workerResponseRoutingKey;

    @Getter
    @Value("${rabbitmq.worker.percent.routing.key}")
    private String percentResponseRoutingKey;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeQueueName);
    }

    @Bean
    public Queue managerRequestQueue() {
        return new Queue(tasksQueueName);
    }

    @Bean
    public Binding managerRequestBinding(DirectExchange exchange) {
        return BindingBuilder.bind(managerRequestQueue())
                .to(exchange)
                .with(managerRequestRoutingKey);
    }

    @Bean
    public Queue workerResponseQueue() {
        return new Queue(taskResultsQueueName);
    }

    @Bean
    public Binding workerResponseBinding(DirectExchange exchange) {
        return BindingBuilder.bind(workerResponseQueue())
                .to(exchange)
                .with(workerResponseRoutingKey);
    }

    @Bean
    public Queue percentResponseQueue() {
        return new Queue(percentsQueueName);
    }

    @Bean
    public Binding percentResponseBinding(DirectExchange exchange) {
        return BindingBuilder.bind(percentResponseQueue())
                .to(exchange)
                .with(percentResponseRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, ObjectMapper mapper) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter(mapper));
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, ObjectMapper mapper) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(jsonMessageConverter(mapper));
        return factory;
    }
}
