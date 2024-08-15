package com.app.service.impl;

import com.app.model.Dish;
import com.app.repo.IDishRepo;
import com.app.service.IDishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements IDishService {

    public final IDishRepo repo;


    @Override
    public Mono<Dish> save(Dish dish) {
        return repo.save(dish);
    }

    @Override
    public Mono<Dish> update(String id, Dish dish) {
        return repo.save(dish);
    }

    @Override
    public Flux<Dish> findAll() {
        return repo.findAll();
    }

    @Override
    public Mono<Dish> findById(String id) {
        return repo.findById(id);
    }

    @Override
    public Mono<Boolean> delete(String id) {
        //  then se ejecuta después del proceso anterior
         return repo.deleteById(id).then(Mono.just(true));
        //  thenReturn se ejecuta después del proceso anterior, pero no exige un flujo, lo sobreentiende
        // return repo.deleteById(id).thenReturn(true);
    }
}
