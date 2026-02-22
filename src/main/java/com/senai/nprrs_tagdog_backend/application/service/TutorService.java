package com.senai.nprrs_tagdog_backend.application.service;

import com.senai.nprrs_tagdog_backend.application.dto.TutorDTO;
import com.senai.nprrs_tagdog_backend.domain.entity.Endereco;
import com.senai.nprrs_tagdog_backend.domain.entity.Tutor;
import com.senai.nprrs_tagdog_backend.domain.repository.AnimalRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.EnderecoRepository;
import com.senai.nprrs_tagdog_backend.domain.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TutorService {
    private final TutorRepository tutorRepository;
    private final AnimalRepository animalRepository;
    private final EnderecoRepository enderecoRepository;
    private final PasswordEncoder passwordEncoder;

    public TutorDTO.TutorResponseDTO registrarTutor(TutorDTO.TutorRegistroDTO dto) {
        if(dto.animais().isEmpty()){
            throw new RuntimeException(); //Entidade animal nao encontrado
        }

        Tutor tutor = dto.toEntity();
        tutor.setSenha(passwordEncoder.encode(dto.senha()));
        tutorRepository.save(tutor);
        animalRepository.saveAll(tutor.getAnimais());
        enderecoRepository.save(dto.endereco().toEntity());

        return TutorDTO.TutorResponseDTO.fromEntity(tutor);
    }

    @Transactional(readOnly = true)
    public List<TutorDTO.TutorResponseDTO> listarTutoresAtivos() {
        return tutorRepository.findAllByAtivoTrue()
                .stream()
                .map(TutorDTO.TutorResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TutorDTO.TutorResponseDTO buscarTutorAtivoPorEmailOuCpf(String emailOuCpf) {
        return TutorDTO.TutorResponseDTO.fromEntity(buscarTutorPorEmailOuCpfEAtivoTrue(emailOuCpf));
    }

    public TutorDTO.TutorResponseDTO atualizarTutor(String emailOuCpf, TutorDTO.TutorAtualizacaoDTO dto) {
        Tutor tutor = buscarTutorPorEmailOuCpfEAtivoTrue(emailOuCpf);

        tutor.setNome(dto.nome());
        tutor.setEmail(dto.email());
        tutor.setSenha(dto.senha()); //funcionario.setSenha(passwordEncoder.encode(dto.senha()));
        tutor.setCpf(dto.cpf());
        tutor.setTelefone(dto.telefone());

        Endereco endereco = enderecoRepository.save(dto.endereco().toEntity());
        tutor.setEndereco(endereco);

        return TutorDTO.TutorResponseDTO.fromEntity(tutorRepository.save(tutor));
    }

    public void desativarTutor(String emailOuCpf) {
        Tutor tutor = buscarTutorPorEmailOuCpfEAtivoTrue(emailOuCpf);

        tutor.setAtivo(false);
        tutorRepository.save(tutor);
    }

    private Tutor buscarTutorPorEmailOuCpfEAtivoTrue(String emailOuCpf){
        if(tutorRepository.findByEmailAndAtivoTrue(emailOuCpf) != null){
            return tutorRepository.findByEmailAndAtivoTrue(emailOuCpf);
        } else if(tutorRepository.findByCpfAndAtivoTrue(emailOuCpf) != null) {
            return tutorRepository.findByCpfAndAtivoTrue(emailOuCpf);
        } else {
            throw new RuntimeException(); //EntidadeNaoEncontradaException("Tutor")
        }
    }

}
