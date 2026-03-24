package com.senai.nprrs_tagdog_backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable = false)
    private String latitude;

    @Column(nullable = false)
    private String longitude;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "animal_id", nullable = true)
    private Animal animal;

    @Column(nullable = false)
    private LocalDateTime dataCriado;

    @Column(nullable = false)
    private boolean ativo;
}
