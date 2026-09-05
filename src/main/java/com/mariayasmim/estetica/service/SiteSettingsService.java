package com.mariayasmim.estetica.service;

import com.mariayasmim.estetica.dto.SiteSettingsDTO;
import com.mariayasmim.estetica.entity.SiteSettings;
import com.mariayasmim.estetica.repository.SiteSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SiteSettingsService {

    private final SiteSettingsRepository siteSettingsRepository;

    // Sempre existe exatamente uma linha. Se ainda não existir (primeira vez), cria vazia.
    private SiteSettings getOrCreate() {
        return siteSettingsRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> siteSettingsRepository.save(SiteSettings.builder().build()));
    }

    public SiteSettingsDTO get() {
        return SiteSettingsDTO.from(getOrCreate());
    }

    public SiteSettingsDTO update(SiteSettingsDTO dto) {
        SiteSettings settings = getOrCreate();
        settings.setAboutText(dto.getAboutText());
        settings.setAddress(dto.getAddress());
        settings.setWhatsapp(dto.getWhatsapp());
        settings.setOpeningHoursText(dto.getOpeningHoursText());
        settings.setInstagramUrl(dto.getInstagramUrl());
        return SiteSettingsDTO.from(siteSettingsRepository.save(settings));
    }
}