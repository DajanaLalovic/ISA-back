package com.isa.OnlyBuns.iservice;

import com.isa.OnlyBuns.dto.LocationMessageDTO;
import com.isa.OnlyBuns.model.LocationMessage;

import java.util.List;

public interface ILocationMessageService {
    LocationMessage save(LocationMessageDTO locationMessageDTO);
    List<LocationMessage> findAll ();
}
