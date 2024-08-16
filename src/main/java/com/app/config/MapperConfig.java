package com.app.config;

import com.app.dto.ClientDTO;
import com.app.dto.InvoiceDTO;
import com.app.model.Client;
import com.app.model.Invoice;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class MapperConfig {

    @Bean(name = "defaultMapper")
    public ModelMapper defaultMapper(){
        return new ModelMapper();
    }

    @Bean(name = "clientMapper")
    public ModelMapper clientMapper(){
        ModelMapper mapper = new ModelMapper();
        //Configurar mapeo estricto de los atributos
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        //En teoría solo es necesario mapear los que se cree que no encontrara coincidencia

        //Escritura
        mapper.createTypeMap(ClientDTO.class, Client.class)
                .addMapping(ClientDTO::getName,(dest, val)-> dest.setFirstName((String) val))
                .addMapping(ClientDTO::getSurname,(dest, val)-> dest.setLastName((String) val))
                .addMapping(ClientDTO::getBirthDateClient,(dest, val)-> dest.setBirthDate((LocalDate) val))
                .addMapping(ClientDTO::getPicture,(dest, val)-> dest.setUrlPhoto((String) val));

        //Lectura
        mapper.createTypeMap(Client.class, ClientDTO.class)
                .addMapping(Client::getFirstName,(dest, val)-> dest.setName((String) val))
                .addMapping(Client::getLastName,(dest, val)-> dest.setSurname((String) val))
                .addMapping(Client::getBirthDate,(dest, val)-> dest.setBirthDateClient((LocalDate) val))
                .addMapping(Client::getUrlPhoto,(dest, val)-> dest.setPicture((String) val));

        return mapper;
    }

    @Bean(name = "invoiceMapper")
    public ModelMapper invoiceMapper(){
        ModelMapper mapper = new ModelMapper();
        //Configurar mapeo estricto de los atributos
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        //En teoría solo es necesario mapear los que se cree que no encontrara coincidencia

        //Escritura
        mapper.createTypeMap(InvoiceDTO.class, Invoice.class)
                .addMapping(e -> e.getClient().getName(),(dest, val)-> dest.getClient().setFirstName((String) val))
                .addMapping(e -> e.getClient().getSurname(),(dest, val)-> dest.getClient().setLastName((String) val));

        //Lectura
        mapper.createTypeMap(Invoice.class, InvoiceDTO.class)
                .addMapping(e -> e.getClient().getFirstName(),(dest, val)-> dest.getClient().setName((String) val))
                .addMapping(e -> e.getClient().getLastName(),(dest, val)-> dest.getClient().setSurname((String) val));

        return mapper;
    }
}
