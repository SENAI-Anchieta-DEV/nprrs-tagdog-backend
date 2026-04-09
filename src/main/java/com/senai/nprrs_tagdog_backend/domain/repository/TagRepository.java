package com.senai.nprrs_tagdog_backend.domain.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, String> {
    Optional<Tag> findFirstByNumeroOrderByDataCriadoDesc(String numero);

    @Query(value = """
            SELECT t1.*
            FROM tag t1
            INNER JOIN (
                SELECT numero, MAX(data_criado) as ultima_data
                FROM tag
                GROUP BY numero
            ) t2 ON t1.numero = t2.numero AND t1.data_criado = t2.ultima_data;
        """, nativeQuery = true)
    List<Tag> findUltimasPosicoesDeCadaTag();
}
