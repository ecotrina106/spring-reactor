package com.app.controller;

import com.app.model.Dish;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/backpressure")
public class BackPressureController {

    @GetMapping(value = "/json", produces = "application/json")
    public Flux<Dish> json(){
        return Flux.interval(Duration.ofMillis(100))
                .map(t -> new Dish("1","Soda",5.90,true));
    }

    @GetMapping(value = "/event", produces = "text/event-stream") //application/stream+json
    public Flux<Dish> eventStream(){
        return Flux.interval(Duration.ofMillis(100))
                .map(t -> new Dish("1","Soda",5.90,true));
    }


    @GetMapping("/limitRate")
    public Flux<Integer> testLimitRate(){
        return Flux.range(1,50000)
                .log();
                //.limitRate(10000,5000);
                //.limitRate(10,5);
                //.limitRate(10);
                //.delayElements(Duration.ofMilis(1));
    }
}
