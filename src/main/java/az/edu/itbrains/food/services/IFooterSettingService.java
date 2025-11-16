package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.response.FooterSettingDTO;

public interface IFooterSettingService {
    // Tək bir qeydi gətirir (Biz id-ni 1 olaraq qəbul edəcəyik)
    FooterSettingDTO getSettings();

    // Məlumatı yeniləyir və ya yaradır
    FooterSettingDTO saveOrUpdateSettings(FooterSettingDTO dto);
}