package com.app.handler;

import com.app.dto.InvoiceDTO;
import com.app.model.Invoice;
import com.app.service.IInvoiceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class InvoiceHandler {

    private final IInvoiceService service;

    @Qualifier("invoiceMapper")
    private final ModelMapper modelMapper;

    public Mono<ServerResponse> findAll(ServerRequest request){
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll().map(this::convertToDto), Invoice.class);
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
        Mono<InvoiceDTO> monoInvoiceDTO = request.bodyToMono(InvoiceDTO.class);

        return monoInvoiceDTO.flatMap(e -> service.save(convertToDocument(e)))
                .map(this::convertToDto)
                .flatMap(e -> ServerResponse
                        .created(URI.create(request.uri().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(monoInvoiceDTO))
                );
    }

    public Mono<ServerResponse> update(ServerRequest request){
        String id =request.pathVariable("id");

        return request.bodyToMono(InvoiceDTO.class)
                .map(e -> {
                    e.setId(id);
                    return e;
                })
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


    private InvoiceDTO convertToDto(Invoice model){
        return modelMapper.map(model, InvoiceDTO.class);
    }

    private Invoice convertToDocument(InvoiceDTO dto){
        return modelMapper.map(dto, Invoice.class);
    }

}
