package com.app.service.impl;

import com.app.model.Dish;
import com.app.repo.IDishRepo;
import com.app.repo.IGenericRepo;
import com.app.service.IDishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DishServiceImpl extends CRUDImpl<Dish,String> implements IDishService {

    public final IDishRepo repo;

    @Override
    protected IGenericRepo<Dish, String> getRepo() {
        return repo;
    }
}
