package com.example.demo.mapper;

import com.example.demo.domain.Creator;
import com.example.demo.dto.CreatorDto;
import org.springframework.stereotype.Component;

@Component
public class CreatorMapper {

    public Creator toEntity(CreatorDto dto){
        Creator creator = new Creator();
        creator.setUsername(dto.getUsername());
        creator.setNameOfGroup(dto.getNameOfGroup());
        return creator;
    }

    public CreatorDto toDto(Creator creator){
        CreatorDto creatorDto = new CreatorDto();
        creatorDto.setUsername(creator.getUsername());
        creatorDto.setNameOfGroup(creator.getNameOfGroup());
        return creatorDto;
    }
}
