package com.example.demo.service.serviceImp;

import com.example.demo.dto.CreatorDto;
import com.example.demo.repository.CreatorRepository;
import com.example.demo.service.CreatorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreatorServiceImpl implements CreatorService {

    private CreatorRepository creatorRepository;

    public CreatorServiceImpl(CreatorRepository creatorRepository) {
        this.creatorRepository = creatorRepository;
    }

    @Override
    public CreatorDto save(CreatorDto creatorDto) {
        return null;
    }

    @Override
    public List<CreatorDto> readAll() {
        return null;
    }

    @Override
    public CreatorDto updateById(Long id, String name) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
