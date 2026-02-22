package com.senai.nprrs_tagdog_backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("TUTOR")
public class Tutor extends Usuario{
    @Column(unique = true, length = 14)
    private String cpf;

    @Column(length = 15)
    private String telefone;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco endereco;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Animal> animais;
}
