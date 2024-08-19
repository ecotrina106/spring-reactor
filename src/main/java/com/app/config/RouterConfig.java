package com.app.config;

import com.app.handler.ClientHandler;
import com.app.handler.DishHandler;
import com.app.handler.InvoiceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    //Functional Endpoint
    @Bean
    public RouterFunction<ServerResponse> routesDish(DishHandler handler){
        return route(GET("/v2/dishes"), handler::findAll)
                .and(route(GET("/v2/dishes/{id}"), handler::findById))
                .and(route(POST("/v2/dishes"),handler::save))
                .and(route(PUT("/v2/dishes/{id}"),handler::update))
                .and(route(DELETE("/v2/dishes"),handler::delete));
    }

    @Bean
    public RouterFunction<ServerResponse> routesClient(ClientHandler handler){
        return route(GET("/v2/clients"), handler::findAll)
                .and(route(GET("/v2/clients/{id}"), handler::findById))
                .and(route(POST("/v2/clients"),handler::save))
                .and(route(PUT("/v2/clients/{id}"),handler::update))
                .and(route(DELETE("/v2/clients"),handler::delete));
    }

    @Bean
    public RouterFunction<ServerResponse> routesInvoice(InvoiceHandler handler){
        return route(GET("/v2/invoices"), handler::findAll)
                .and(route(GET("/v2/invoices/{id}"), handler::findById))
                .and(route(POST("/v2/invoices"),handler::save))
                .and(route(PUT("/v2/invoices/{id}"),handler::update))
                .and(route(DELETE("/v2/invoices"),handler::delete));
    }


}
