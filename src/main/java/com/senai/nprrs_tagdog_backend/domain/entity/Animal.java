package com.senai.nprrs_tagdog_backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Animal{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String cpfTutor;

    @Column(nullable = false) //GenerationType.SEQUENCE
    private String matricula;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String raca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SexoAnimal sexo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PorteAnimal porte;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false)
    private String descricao;

    private String numeroTag;

    @Column(nullable = false)
    protected boolean ativo;
}
