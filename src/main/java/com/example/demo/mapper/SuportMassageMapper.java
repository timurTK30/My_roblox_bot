package com.example.demo.mapper;

import com.example.demo.domain.SupportMassage;
import com.example.demo.dto.SuportMassageDto;
import org.springframework.stereotype.Component;

@Component
public class SuportMassageMapper {

    public SupportMassage toEntity(SuportMassageDto dto){
        SupportMassage supportMassage = new SupportMassage();
        supportMassage.setId(dto.getId());
        supportMassage.setChatId(dto.getChatId());
        supportMassage.setMassage(dto.getMassage());
        supportMassage.setDate(dto.getDate());
        return supportMassage;
    }

    public SuportMassageDto toDto(SupportMassage supportMassage){
        SuportMassageDto suportMassageDto = new SuportMassageDto();
        suportMassageDto.setId(supportMassage.getId());
        suportMassageDto.setChatId(supportMassage.getChatId());
        suportMassageDto.setMassage(supportMassage.getMassage());
        suportMassageDto.setDate(supportMassage.getDate());
        return suportMassageDto;
    }
}
