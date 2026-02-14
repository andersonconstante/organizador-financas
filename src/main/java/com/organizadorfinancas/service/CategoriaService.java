package com.organizadorfinancas.service;

import com.organizadorfinancas.model.Categoria;
import com.organizadorfinancas.model.TipoCategoria;
import com.organizadorfinancas.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }
    
    public Optional<Categoria> findById(Long id) {
        return categoriaRepository.findById(id);
    }
    
    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }
    
    public void deleteById(Long id) {
        categoriaRepository.deleteById(id);
    }
    
    public List<Categoria> findByTipo(TipoCategoria tipo) {
        return categoriaRepository.findByTipo(tipo);
    }
    
    public List<Categoria> findByEssencial(Boolean essencial) {
        return categoriaRepository.findByEssencial(essencial);
    }
    
    public List<Categoria> findRendaFixa() {
        return categoriaRepository.findByTipo(TipoCategoria.RENDA_FIXA);
    }
    
    public List<Categoria> findRendaVariavel() {
        return categoriaRepository.findByTipo(TipoCategoria.RENDA_VARIAVEL);
    }
    
    public List<Categoria> findDespesasEssenciais() {
        return categoriaRepository.findByTipoAndEssencial(TipoCategoria.DESPESA_ESSENCIAL, true);
    }
    
    public List<Categoria> findDespesasSuperfluas() {
        return categoriaRepository.findByTipoAndEssencial(TipoCategoria.DESPESA_SUPERFLUA, false);
    }
    
    public List<Categoria> findGastosInvisiveis() {
        return categoriaRepository.findByTipo(TipoCategoria.GASTO_INVISIVEL);
    }
    
    public Optional<Categoria> findByNome(String nome) {
        return categoriaRepository.findByNome(nome);
    }
    
    public boolean existsByNome(String nome) {
        return categoriaRepository.findByNome(nome).isPresent();
    }
    
    public Long countCategoriasEssenciais() {
        return categoriaRepository.countCategoriasEssenciais();
    }
    
    public Long countCategoriasSuperfluas() {
        return categoriaRepository.countCategoriasSuperfluas();
    }
}
