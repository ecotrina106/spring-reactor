package com.app.service.impl;

import com.app.pagination.PageSupport;
import com.app.repo.IGenericRepo;
import com.app.service.ICRUD;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

//Clase abstracta de clase generica para el CRUD del proyecto
public abstract class CRUDImpl<T,ID> implements ICRUD<T,ID> {

    //Se requiere este atributo para obtener el repo asociado
    protected abstract IGenericRepo<T,ID> getRepo();

    @Override
    public Mono<T> save(T t) {
        return getRepo().save(t);
    }

    @Override
    public Mono<T> update(ID id, T t) {
        return getRepo().findById(id)
                .flatMap(e->getRepo().save(t));
    }

    @Override
    public Flux<T> findAll() {
        return  getRepo().findAll();
    }

    @Override
    public Mono<T> findById(ID id) {
        return getRepo().findById(id);
    }

    @Override
    public Mono<Boolean> delete(ID id) {
        return getRepo().findById(id)
                .hasElement()
                .flatMap(result->{
                    if(result){
                        return getRepo().deleteById(id).thenReturn(true);
                    }else {
                        return Mono.just(false);
                    }
                });
    }

    @Override
    public Mono<PageSupport<T>> getPage(Pageable pageable) {
        //Para esta implementacion de page se trae todos los datos y aqui en codigo se hace la división, pudiendo tener que treaer
        // mucha información, otra alternativa es hacerlo mediante un query personalizado
        return getRepo().findAll()
                .collectList()
                .map(list -> new PageSupport<>(
                        list.stream()
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                .limit(pageable.getPageSize()).toList()
                        , pageable.getPageNumber(), pageable.getPageSize(), list.size()
                ));
    }
}
