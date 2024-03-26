package com.example.demo.service;

import com.example.demo.dto.CreatorDto;

import java.util.List;

public interface CreatorService {

    CreatorDto save(CreatorDto creatorDto);
    List<CreatorDto> readAll();
    CreatorDto updateById(Long id, CreatorDto creatorDto);
    void deleteById(Long id);
}
