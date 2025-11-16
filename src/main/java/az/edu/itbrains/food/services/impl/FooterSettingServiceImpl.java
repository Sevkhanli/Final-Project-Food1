package az.edu.itbrains.food.services.impl;

import az.edu.itbrains.food.DTOs.response.FooterSettingDTO;
import az.edu.itbrains.food.models.FooterSetting;
import az.edu.itbrains.food.repositories.FooterSettingRepository;
import az.edu.itbrains.food.services.IFooterSettingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper; // ModelMapper-i yenə istifadə edirik
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FooterSettingServiceImpl implements IFooterSettingService {

    private final FooterSettingRepository repository;
    private final ModelMapper modelMapper;
    private final Long DEFAULT_ID = 1L;

    @Override
    public FooterSettingDTO getSettings() {
        // ID=1 olan qeydi axtarır. Tapmasa null qaytarır.
        Optional<FooterSetting> settings = repository.findById(DEFAULT_ID);
        return settings.map(setting -> modelMapper.map(setting, FooterSettingDTO.class))
                .orElse(null);
    }

    @Override
    public FooterSettingDTO saveOrUpdateSettings(FooterSettingDTO dto) {
        FooterSetting entity;

        // Əgər ID varsa (default olaraq 1), obyekti tapıb yeniləyirik.
        // Yoxdursa, yeni bir obyektdir (Create).
        Optional<FooterSetting> existing = repository.findById(DEFAULT_ID);
        if (existing.isPresent()) {
            entity = existing.get();
        } else {
            entity = new FooterSetting();
            entity.setId(DEFAULT_ID); // Həmişə ID=1 olsun
        }

        // DTO-dan Entity-ə məlumatları köçürürük
        entity.setCopyrightText(dto.getCopyrightText());
        entity.setFacebookUrl(dto.getFacebookUrl());
        entity.setTwitterUrl(dto.getTwitterUrl());
        entity.setInstagramUrl(dto.getInstagramUrl());
        entity.setLinkedinUrl(dto.getLinkedinUrl());

        // Saxla
        FooterSetting saved = repository.save(entity);
        return modelMapper.map(saved, FooterSettingDTO.class);
    }
}