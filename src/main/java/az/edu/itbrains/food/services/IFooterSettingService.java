package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.response.FooterSettingDTO;

public interface IFooterSettingService {
    FooterSettingDTO getSettings();

    FooterSettingDTO saveOrUpdateSettings(FooterSettingDTO dto);
}