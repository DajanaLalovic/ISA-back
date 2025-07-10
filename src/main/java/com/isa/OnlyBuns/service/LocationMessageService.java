package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.dto.LocationMessageDTO;
import com.isa.OnlyBuns.irepository.ILocationMessageRepository;
import com.isa.OnlyBuns.iservice.ILocationMessageService;
import com.isa.OnlyBuns.model.LocationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationMessageService implements ILocationMessageService {

    @Autowired
    private ILocationMessageRepository locationMessageRepository;
    @Override
    public LocationMessage save(LocationMessageDTO locationMessageDTO) {
        LocationMessage locationMessage = new LocationMessage();
        locationMessage.setName(locationMessageDTO.getName());
        locationMessage.setStreet(locationMessageDTO.getStreet());
        locationMessage.setNumber(locationMessageDTO.getNumber());
        locationMessage.setCity(locationMessageDTO.getCity());
        locationMessage.setPostalCode(locationMessageDTO.getPostalCode());
        locationMessage.setCountry(locationMessageDTO.getCountry());
        return locationMessageRepository.save(locationMessage);
    }
    public List<LocationMessage> findAll() throws AccessDeniedException {
        return locationMessageRepository.findAll();
    }
}
