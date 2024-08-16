package com.app.controller;

import com.app.dto.ClientDTO;
import com.app.model.Client;
import com.app.pagination.PageSupport;
import com.app.service.IClientService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cloudinary.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Map;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final IClientService service;
    @Qualifier("clientMapper")
    private final ModelMapper modelMapper;
    private final Cloudinary cloudinary;

    @GetMapping
    public Mono<ResponseEntity<Flux<ClientDTO>>> findAll(){
        Flux<ClientDTO> fx = service.findAll().map(this::convertToDto);

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)
        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ClientDTO>> findById(@PathVariable("id") String id){
        return service.findById(id)
                .map(this::convertToDto)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<ClientDTO>> save(@RequestBody @Valid ClientDTO dto, final ServerHttpRequest req){
        return service.save(this.convertToDocument(dto))
                .map(this::convertToDto)
                .map(e -> ResponseEntity.created(
                        URI.create(req.getURI().toString().concat("/").concat(e.getId()))
                                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ClientDTO>> update(@PathVariable("id") String id, @RequestBody @Valid ClientDTO dto){
        return Mono.just(this.convertToDocument(dto))
                .map(e -> {
                    e.setId(id);
                    return e;
                })
                .flatMap(e -> service.update(id, e))
                .map(this::convertToDto)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id){

        return service.delete(id)
                .flatMap(result -> {
                    if(result){
                        return Mono.just(ResponseEntity.noContent().build());
                    }else{
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                });
    }

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<ClientDTO>>> getPage(@RequestParam(name = "page", defaultValue = "0") int page,
                                                              @RequestParam(name = "size", defaultValue = "2") int size){
        //utilización de PageRequest que implementa Pageable
        return service.getPage(PageRequest.of(page, size))
                .map(pageSupport -> new PageSupport<>(
                        pageSupport.getContent().stream().map(this::convertToDto).toList(),
                        pageSupport.getPageNumber(),
                        pageSupport.getPageSize(),
                        pageSupport.getTotalElements()
                ))
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel<ClientDTO>> getHateoas(@PathVariable("id") String id){
        Mono<Link> monoLink = linkTo(methodOn(ClientController.class).findById(id)).withRel("client-info").toMono();

        return service.findById(id)
                .map(this::convertToDto)
                .zipWith(monoLink,EntityModel::of); //(d,link) -> EntityModel.of(d,link)
    }

    @PostMapping("/v1/upload/{id}")
    //FilePart es la clase para files en reactivo
    public Mono<ResponseEntity<ClientDTO>> uploadV1(@PathVariable("id") String id, @RequestPart("file") FilePart filePart){
        return service.findById(id)
                .flatMap(client -> {
                    try {
                        File f = Files.createTempFile("temp", filePart.filename()).toFile();
                        //Esta tranferencia toma tiempo hacerlo, el flujo del código puede seguir y no terminar aun de tranferir el archivo
                        //Una solución rápida para nada recomenada es usar block(), ya que bloquea el hilo en espera
                        filePart.transferTo(f);
                        Map response = cloudinary.uploader().upload(f, ObjectUtils.asMap("resource_type","auto"));
                        JSONObject json = new JSONObject(response);
                        String url = json.getString("url");

                        client.setUrlPhoto(url);

                        return service.update(id,client)
                                .map(this::convertToDto)
                                .map(e -> ResponseEntity.ok().body(e));

                    } catch (IOException e) {
                       return Mono.error(new RuntimeException(e.getMessage()));
                    }

                });
    }

    @PostMapping("/v2/upload/{id}")
    //FilePart es la clase para files en reactivo
    public Mono<ResponseEntity<ClientDTO>> uploadV2(@PathVariable("id") String id, @RequestPart("file") FilePart filePart) throws IOException {
        File f = Files.createTempFile("temp", filePart.filename()).toFile();

        return filePart.transferTo(f)
                .then(service.findById(id).flatMap(client -> {
                    try{
                        Map response = cloudinary.uploader().upload(f, ObjectUtils.asMap("resource_type","auto"));
                        JSONObject json = new JSONObject(response);
                        String url = json.getString("url");

                        client.setUrlPhoto(url);

                        return service.update(id,client)
                                .map(this::convertToDto)
                                .map(e -> ResponseEntity.ok().body(e));

                    }catch (Exception e){
                        return Mono.error(new RuntimeException(e.getMessage()));
                    }
                }));
    }

    @PostMapping("/v3/upload/{id}")
    //FilePart es la clase para files en reactivo
    public Mono<ResponseEntity<ClientDTO>> uploadV3(@PathVariable("id") String id, @RequestPart("file") FilePart filePart){
        return Mono.fromCallable( ()-> Files.createTempFile("temp", filePart.filename()).toFile())
                .flatMap(tempFile -> filePart.transferTo(tempFile)
                        .then(service.findById(id)
                                .flatMap(client -> Mono.fromCallable( ()->{
                                    Map<String,Object> response = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap("resource_type","auto"));
                                    JSONObject json = new JSONObject(response);
                                    String url = json.getString("url");

                                    client.setUrlPhoto(url);

                                    return service.update(id,client)
                                            .map(this::convertToDto)
                                            .map(e -> ResponseEntity.ok().body(e));
                                })
                                        .flatMap(mono -> mono))
                        )
                );
    }

        private ClientDTO convertToDto(Client model){
        return modelMapper.map(model, ClientDTO.class);
    }

    private Client convertToDocument(ClientDTO dto){
        return modelMapper.map(dto, Client.class);
    }

}
