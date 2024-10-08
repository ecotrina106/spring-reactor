package com.app.handler;

import com.app.dto.DishDTO;
import com.app.dto.ValidationDTO;
import com.app.model.Dish;
import com.app.service.IDishService;
import com.app.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class DishHandler {

    private final IDishService service;

    @Qualifier("defaultMapper")
    private final ModelMapper modelMapper;

    //private final Validator validator;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> findAll(ServerRequest request){
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll().map(this::convertToDto), Dish.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request){
        String id =request.pathVariable("id");

        return service.findById(id)
                .map(this::convertToDto)
                .flatMap(e -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        //el body se debe usar fromValue
                        .body(fromValue(e))
                  //Para el retorno se debe usar switchIfEmpty
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> save(ServerRequest request){
        Mono<DishDTO> monoDishDTO = request.bodyToMono(DishDTO.class);

        /*return monoDishDTO
                .flatMap(e -> {
                    Errors erros = new BeanPropertyBindingResult(e, DishDTO.class.getName());
                    validator.validate(e,erros);

                    if(erros.hasErrors()){
                        return Flux.fromIterable(erros.getFieldErrors())
                                .map(error -> new ValidationDTO(error.getField(),error.getDefaultMessage()))
                                .collectList()
                                .flatMap(list -> ServerResponse.badRequest()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(fromValue(list)));
                    }else{
                        return service.save(convertToDocument(e))
                                .map(this::convertToDto)
                                .flatMap(dto -> ServerResponse
                                        .created(URI.create(request.uri().toString().concat("/").concat(e.getId())))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(fromValue(dto)));
                    }
                });*/

        return monoDishDTO
                //Validacion extraida en un método
                .flatMap(requestValidator::validate)
                .flatMap(e -> service.save(convertToDocument(e)))
                .map(this::convertToDto)
                .flatMap(e -> ServerResponse
                        .created(URI.create(request.uri().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(monoDishDTO))
                );
    }

    public Mono<ServerResponse> update(ServerRequest request){
        String id =request.pathVariable("id");

        return request.bodyToMono(DishDTO.class)
                .map(e -> {
                    e.setId(id);
                    return e;
                })
                .flatMap(requestValidator::validate)
                .flatMap(e -> service.update(id, convertToDocument(e)))
                .map(this::convertToDto)
                .flatMap(e -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(e))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request){
        String id = request.pathVariable("id");

        return service.delete(id)
                .flatMap(result ->{
                    if(result){
                        return ServerResponse.noContent().build();
                    }
                    else {
                        return ServerResponse.notFound().build();
                    }
                });
    }


    private DishDTO convertToDto(Dish model){
        return modelMapper.map(model, DishDTO.class);
    }

    private Dish convertToDocument(DishDTO dto){
        return modelMapper.map(dto, Dish.class);
    }

}
