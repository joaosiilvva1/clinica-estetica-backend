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

    // --- Cabeçalho ---
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    // --- Hero (topo do site) ---
    @Column(name = "hero_eyebrow", length = 200)
    private String heroEyebrow;

    @Column(name = "hero_title", length = 300)
    private String heroTitle;

    @Column(name = "hero_subtitle", length = 1000)
    private String heroSubtitle;

    // JSON: [{"icon": "🛡️", "text": "Procedimentos seguros"}, ...]
    @Column(name = "hero_trust_items_json", columnDefinition = "TEXT")
    private String heroTrustItemsJson;

    // --- Faixa de benefícios (logo abaixo do hero) ---
    // JSON: [{"icon": "⭐", "text": "Atendimento Exclusivo e Personalizado"}, ...]
    @Column(name = "benefits_items_json", columnDefinition = "TEXT")
    private String benefitsItemsJson;

    // --- Seção "Indicações" (cards de quem pode se beneficiar) ---
    @Column(name = "indications_section_title", length = 300)
    private String indicationsSectionTitle;

    // JSON: [{"icon": "✨", "title": "...", "text": "..."}, ...]
    @Column(name = "indications_items_json", columnDefinition = "TEXT")
    private String indicationsItemsJson;

    // --- Seção "Sobre" ---
    @Column(name = "about_badge_text", length = 100)
    private String aboutBadgeText;

    @Column(name = "about_photo_url", length = 500)
    private String aboutPhotoUrl;

    // --- Seção "Tratamentos" (cabeçalho; os tratamentos em si têm CRUD próprio) ---
    @Column(name = "treatments_eyebrow", length = 200)
    private String treatmentsEyebrow;

    @Column(name = "treatments_section_title", length = 300)
    private String treatmentsSectionTitle;

    @Column(name = "treatments_section_subtitle", length = 500)
    private String treatmentsSectionSubtitle;

    // --- Seção "Onde Estamos" ---
    @Column(name = "location_section_title", length = 300)
    private String locationSectionTitle;

    @Column(name = "location_section_subtitle", length = 500)
    private String locationSectionSubtitle;

    // --- Seção "Agendamento" ---
    @Column(name = "booking_section_title", length = 300)
    private String bookingSectionTitle;

    @Column(name = "booking_section_subtitle", length = 500)
    private String bookingSectionSubtitle;

    // --- Seção "Depoimentos" ---
    @Column(name = "testimonials_section_title", length = 300)
    private String testimonialsSectionTitle;

    @Column(name = "testimonials_section_subtitle", length = 500)
    private String testimonialsSectionSubtitle;

    // --- Seção "Perguntas Frequentes" ---
    @Column(name = "faq_section_title", length = 300)
    private String faqSectionTitle;

    @Column(name = "faq_section_subtitle", length = 500)
    private String faqSectionSubtitle;

    // JSON: [{"question": "...", "answer": "..."}, ...]
    @Column(name = "faq_items_json", columnDefinition = "TEXT")
    private String faqItemsJson;

    // --- Rodapé ---
    @Column(name = "footer_tagline", length = 500)
    private String footerTagline;

    @Column(name = "footer_contact_email", length = 200)
    private String footerContactEmail;

    @Column(name = "footer_copyright_text", length = 300)
    private String footerCopyrightText;
}