package com.example.demo.service.serviceImp;

import com.example.demo.domain.Creator;
import com.example.demo.domain.Game;
import com.example.demo.domain.User;
import com.example.demo.dto.CreatorDto;
import com.example.demo.mapper.CreatorMapper;
import com.example.demo.repository.CreatorRepository;
import com.example.demo.service.CreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreatorServiceImpl implements CreatorService {

    private final CreatorRepository creatorRepository;

    private final CreatorMapper creatorMapper;

    @Autowired
    public CreatorServiceImpl(CreatorRepository creatorRepository, CreatorMapper creatorMapper) {
        this.creatorRepository = creatorRepository;
        this.creatorMapper = creatorMapper;
    }

    @Override
    public CreatorDto save(CreatorDto creatorDto) {
        return creatorMapper.toDto(creatorRepository.save(creatorMapper.toEntity(creatorDto)));
    }

    @Override
    public List<CreatorDto> readAll() {
        List<Creator> creatorList = creatorRepository.findAll();
        return creatorList.stream().map(creatorMapper::toDto).toList();
    }

    @Override
    public CreatorDto updateById(Long id, CreatorDto creatorDto) {
        Creator creator = creatorMapper.toEntity(creatorDto);
        creator.setId(id);
        return creatorMapper.toDto(creator);
    }

    @Override
    public void deleteById(Long id) {
        creatorRepository.deleteById(id);
    }
}
