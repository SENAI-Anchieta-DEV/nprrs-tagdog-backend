package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.TagDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.Tag;
import com.senai.nprrs_tagdog_backend.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {
    private final AnimalRepository animalRepository;
    private final TagRepository tagRepository;

    public void salvar(TagDTO.TagRegistroDTO dto) {
        Animal animal = animalRepository.findByNumeroTag(dto.numero());

        Tag tag = new Tag();
        if(animal != null){
            tag = dto.toEntity();
            tag.setAnimal(animal);
        }

        if(tagRepository.findByNumero(dto.numero()) != null){

        }

        tagRepository.save(tag);
    }
}
