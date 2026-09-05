package com.mariayasmim.estetica.dto;

import com.mariayasmim.estetica.entity.SiteSettings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SiteSettingsDTO {
    private String aboutText;
    private String address;
    private String whatsapp;
    private String openingHoursText;
    private String instagramUrl;

    private String logoUrl;

    private String heroEyebrow;
    private String heroTitle;
    private String heroSubtitle;
    private String heroTrustItemsJson;

    private String benefitsItemsJson;

    private String indicationsSectionTitle;
    private String indicationsItemsJson;

    private String aboutBadgeText;
    private String aboutPhotoUrl;

    private String treatmentsEyebrow;
    private String treatmentsSectionTitle;
    private String treatmentsSectionSubtitle;

    private String locationSectionTitle;
    private String locationSectionSubtitle;

    private String bookingSectionTitle;
    private String bookingSectionSubtitle;

    private String testimonialsSectionTitle;
    private String testimonialsSectionSubtitle;

    private String faqSectionTitle;
    private String faqSectionSubtitle;
    private String faqItemsJson;

    private String footerTagline;
    private String footerContactEmail;
    private String footerCopyrightText;

    public static SiteSettingsDTO from(SiteSettings s) {
        SiteSettingsDTO dto = new SiteSettingsDTO();
        dto.setAboutText(s.getAboutText());
        dto.setAddress(s.getAddress());
        dto.setWhatsapp(s.getWhatsapp());
        dto.setOpeningHoursText(s.getOpeningHoursText());
        dto.setInstagramUrl(s.getInstagramUrl());

        dto.setLogoUrl(s.getLogoUrl());

        dto.setHeroEyebrow(s.getHeroEyebrow());
        dto.setHeroTitle(s.getHeroTitle());
        dto.setHeroSubtitle(s.getHeroSubtitle());
        dto.setHeroTrustItemsJson(s.getHeroTrustItemsJson());

        dto.setBenefitsItemsJson(s.getBenefitsItemsJson());

        dto.setIndicationsSectionTitle(s.getIndicationsSectionTitle());
        dto.setIndicationsItemsJson(s.getIndicationsItemsJson());

        dto.setAboutBadgeText(s.getAboutBadgeText());
        dto.setAboutPhotoUrl(s.getAboutPhotoUrl());

        dto.setTreatmentsEyebrow(s.getTreatmentsEyebrow());
        dto.setTreatmentsSectionTitle(s.getTreatmentsSectionTitle());
        dto.setTreatmentsSectionSubtitle(s.getTreatmentsSectionSubtitle());

        dto.setLocationSectionTitle(s.getLocationSectionTitle());
        dto.setLocationSectionSubtitle(s.getLocationSectionSubtitle());

        dto.setBookingSectionTitle(s.getBookingSectionTitle());
        dto.setBookingSectionSubtitle(s.getBookingSectionSubtitle());

        dto.setTestimonialsSectionTitle(s.getTestimonialsSectionTitle());
        dto.setTestimonialsSectionSubtitle(s.getTestimonialsSectionSubtitle());

        dto.setFaqSectionTitle(s.getFaqSectionTitle());
        dto.setFaqSectionSubtitle(s.getFaqSectionSubtitle());
        dto.setFaqItemsJson(s.getFaqItemsJson());

        dto.setFooterTagline(s.getFooterTagline());
        dto.setFooterContactEmail(s.getFooterContactEmail());
        dto.setFooterCopyrightText(s.getFooterCopyrightText());

        return dto;
    }
}