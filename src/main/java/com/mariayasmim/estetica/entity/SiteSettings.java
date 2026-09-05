package com.mariayasmim.estetica.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * MVP: linha única (singleton) com os textos e dados de contato editáveis pela
 * Maria no painel — "Sobre", endereço, WhatsApp, horário de funcionamento e Instagram.
 * Se um dia precisar de múltiplas unidades/profissionais, isso vira um recurso à parte.
 */
@Entity
@Table(name = "site_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "about_text", length = 4000)
    private String aboutText;

    @Column(length = 300)
    private String address;

    @Column(length = 30)
    private String whatsapp;

    @Column(name = "opening_hours_text", length = 500)
    private String openingHoursText;

    @Column(name = "instagram_url", length = 300)
    private String instagramUrl;
}