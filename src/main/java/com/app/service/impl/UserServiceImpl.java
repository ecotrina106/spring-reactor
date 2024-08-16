package com.app.service.impl;

import com.app.model.User;
import com.app.repo.IUserRepo;
import com.app.repo.IGenericRepo;
import com.app.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends CRUDImpl<User,String> implements IUserService {

    public final IUserRepo repo;

    @Override
    protected IGenericRepo<User, String> getRepo() {
        return repo;
    }
}
