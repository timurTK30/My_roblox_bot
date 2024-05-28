package com.example.demo.service.serviceImp;

import com.example.demo.domain.SupportMassage;
import com.example.demo.dto.SuportMassageDto;
import com.example.demo.mapper.SuportMassageMapper;
import com.example.demo.repository.SupportMassageRepository;
import com.example.demo.service.SupportMassageService;
import org.hibernate.internal.build.AllowNonPortable;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class SupportMassageServiceImpl implements SupportMassageService {

    private final SupportMassageRepository repository;
    private final SuportMassageMapper mapper;

    @Autowired
    public SupportMassageServiceImpl(SupportMassageRepository repository, SuportMassageMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public SuportMassageDto save(SuportMassageDto suportMassageDto) {
        return mapper.toDto(repository.save(mapper.toEntity(suportMassageDto)));
    }

    @Override
    public List<SuportMassageDto> readAll() {
        List<SupportMassage> supportMassages = repository.findAll();
        return supportMassages.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public SuportMassageDto updateByChatId(SuportMassageDto suportMassageDto, Long chatId) {
        Optional<SuportMassageDto> massageByChatId = getMassageByChatId(chatId);
        if(massageByChatId.isPresent()){
            SuportMassageDto massageDtoByChatId = massageByChatId.get();
            massageDtoByChatId.setMassage(suportMassageDto.getMassage());
            massageDtoByChatId.setDate(suportMassageDto.getDate());
            return massageDtoByChatId;
        }
        return null;
    }

    @Override
    public void deleteByChatId(Long chatId) {
        repository.deleteById(getMassageByChatId(chatId).get().getId());
    }

    @Override
    public Optional<SuportMassageDto> getMassageByChatId(Long chatId) {
        SupportMassage massageByChatId = repository.getMassageByChatId(chatId);
        Optional<SuportMassageDto> suportMassageDto = Optional.ofNullable(mapper.toDto(massageByChatId));
        return suportMassageDto;
    }
}
