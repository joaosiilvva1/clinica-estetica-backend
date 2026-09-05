package com.mariayasmim.estetica.dto;

import com.mariayasmim.estetica.entity.SiteSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SiteSettingsDTO {
    private String aboutText;
    private String address;
    private String whatsapp;
    private String openingHoursText;
    private String instagramUrl;

    public static SiteSettingsDTO from(SiteSettings s) {
        return new SiteSettingsDTO(
                s.getAboutText(), s.getAddress(), s.getWhatsapp(),
                s.getOpeningHoursText(), s.getInstagramUrl()
        );
    }
}