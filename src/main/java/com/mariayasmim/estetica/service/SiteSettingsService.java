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

        settings.setLogoUrl(dto.getLogoUrl());

        settings.setHeroEyebrow(dto.getHeroEyebrow());
        settings.setHeroTitle(dto.getHeroTitle());
        settings.setHeroSubtitle(dto.getHeroSubtitle());
        settings.setHeroTrustItemsJson(dto.getHeroTrustItemsJson());

        settings.setBenefitsItemsJson(dto.getBenefitsItemsJson());

        settings.setIndicationsSectionTitle(dto.getIndicationsSectionTitle());
        settings.setIndicationsItemsJson(dto.getIndicationsItemsJson());

        settings.setAboutBadgeText(dto.getAboutBadgeText());
        settings.setAboutPhotoUrl(dto.getAboutPhotoUrl());

        settings.setTreatmentsEyebrow(dto.getTreatmentsEyebrow());
        settings.setTreatmentsSectionTitle(dto.getTreatmentsSectionTitle());
        settings.setTreatmentsSectionSubtitle(dto.getTreatmentsSectionSubtitle());

        settings.setLocationSectionTitle(dto.getLocationSectionTitle());
        settings.setLocationSectionSubtitle(dto.getLocationSectionSubtitle());

        settings.setBookingSectionTitle(dto.getBookingSectionTitle());
        settings.setBookingSectionSubtitle(dto.getBookingSectionSubtitle());

        settings.setTestimonialsSectionTitle(dto.getTestimonialsSectionTitle());
        settings.setTestimonialsSectionSubtitle(dto.getTestimonialsSectionSubtitle());

        settings.setFaqSectionTitle(dto.getFaqSectionTitle());
        settings.setFaqSectionSubtitle(dto.getFaqSectionSubtitle());
        settings.setFaqItemsJson(dto.getFaqItemsJson());

        settings.setFooterTagline(dto.getFooterTagline());
        settings.setFooterContactEmail(dto.getFooterContactEmail());
        settings.setFooterCopyrightText(dto.getFooterCopyrightText());

        return SiteSettingsDTO.from(siteSettingsRepository.save(settings));
    }
}