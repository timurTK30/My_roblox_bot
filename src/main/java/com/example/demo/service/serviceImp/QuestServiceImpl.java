package com.example.demo.service.serviceImp;

import com.example.demo.domain.Quest;
import com.example.demo.repository.QuestRepository;
import com.example.demo.service.QuestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class QuestServiceImpl implements QuestService {

    private final QuestRepository repository;

    @Autowired
    public QuestServiceImpl(QuestRepository repository) {
        this.repository = repository;
    }

    @Override
    public Quest save(Quest quest) {
        return repository.save(quest);
    }

    @Override
    public List<Quest> readAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Quest> updateById(Long id, Quest quest) {
        Optional<Quest> questById = getQuestById(id);
        quest.setId(id);
        return questById;
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Quest> getQuestById(Long id) {
        Optional<Quest> byId = repository.findById(id);
        if(byId.isPresent()){
            return byId;
        }

        log.info("Такого квеста нет. Id = {}", id);
        return Optional.empty();
    }


}
