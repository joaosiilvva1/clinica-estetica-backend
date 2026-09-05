package com.mariayasmim.estetica.repository;

import com.mariayasmim.estetica.entity.SiteSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SiteSettingsRepository extends JpaRepository<SiteSettings, UUID> {
}