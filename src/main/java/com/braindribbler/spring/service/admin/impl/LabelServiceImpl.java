package com.braindribbler.spring.service.admin.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.braindribbler.spring.models.admin.Label;
import com.braindribbler.spring.repositories.admin.LabelRepository;
import com.braindribbler.spring.service.admin.LabelService;

import jakarta.transaction.Transactional;

@Service
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;

    public LabelServiceImpl(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Override
    @Cacheable("labelsCache")
    public Map<String, String> getAllLabelsAsMap() {
        return labelRepository.findAll().stream()
                .collect(Collectors.toMap(label -> label.getKey(), label -> label.getLabel()));
    }

    @Override
    public List<Label> getAllLabelsAsList() {
        return labelRepository.findAll();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "labelsCache", allEntries = true)
    public void saveLabel(Label label) {
        if (label == null) {
            throw new IllegalArgumentException("Label cannot be null");
        }
        labelRepository.save(label);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "labelsCache", allEntries = true)
    public void deleteLabel(Long id) {
        if (id != null) {
            labelRepository.deleteById(id);
        }
    }
}
