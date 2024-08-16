package com.app.service.impl;

import com.app.model.Menu;
import com.app.repo.IMenuRepo;
import com.app.repo.IGenericRepo;
import com.app.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends CRUDImpl<Menu,String> implements IMenuService {

    public final IMenuRepo repo;

    @Override
    protected IGenericRepo<Menu, String> getRepo() {
        return repo;
    }
}
