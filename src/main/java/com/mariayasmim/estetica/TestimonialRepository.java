package com.mariayasmim.estetica;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {
    List<Testimonial> findByApprovedTrue();
    List<Testimonial> findByApprovedFalseOrderByIdDesc();
}