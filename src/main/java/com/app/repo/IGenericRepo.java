package com.app.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;


//Interfaz de definición de generico de repo, entonces le decimos a spring que no intente generar un Bean con esto al no
// saber las clases en concretas a usar
@NoRepositoryBean
public interface IGenericRepo<T,ID> extends ReactiveMongoRepository<T,ID> {
}
