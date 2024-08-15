package com.app.service.impl;

import com.app.model.Client;
import com.app.repo.IClientRepo;
import com.app.repo.IGenericRepo;
import com.app.service.IClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl extends CRUDImpl<Client,String> implements IClientService {

    public final IClientRepo repo;

    @Override
    protected IGenericRepo<Client, String> getRepo() {
        return repo;
    }
}
