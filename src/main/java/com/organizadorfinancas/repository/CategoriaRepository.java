package com.organizadorfinancas.repository;

import com.organizadorfinancas.model.Categoria;
import com.organizadorfinancas.model.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    List<Categoria> findByTipo(TipoCategoria tipo);
    
    List<Categoria> findByEssencial(Boolean essencial);
    
    List<Categoria> findByTipoAndEssencial(TipoCategoria tipo, Boolean essencial);
    
    Optional<Categoria> findByNome(String nome);
    
    @Query("SELECT c FROM Categoria c WHERE c.tipo IN :tipos")
    List<Categoria> findByTipoIn(List<TipoCategoria> tipos);
    
    @Query("SELECT COUNT(c) FROM Categoria c WHERE c.essencial = true")
    Long countCategoriasEssenciais();
    
    @Query("SELECT COUNT(c) FROM Categoria c WHERE c.essencial = false")
    Long countCategoriasSuperfluas();
}
