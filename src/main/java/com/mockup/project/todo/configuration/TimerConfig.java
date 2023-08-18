package com.mockup.project.todo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Timer;

@Configuration
@EnableScheduling
public class TimerConfig {

    @Bean
    public Timer timer() {
        return new Timer();
    }
}
