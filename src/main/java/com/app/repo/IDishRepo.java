package com.app.repo;

import com.app.model.Dish;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IDishRepo extends ReactiveMongoRepository<Dish,String> {
}
