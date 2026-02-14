package com.organizadorfinancas.repository;

import com.organizadorfinancas.model.Categoria;
import com.organizadorfinancas.model.TipoCategoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes do CategoriaRepository")
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoriaSalario;
    private Categoria categoriaAlimentacao;
    private Categoria categoriaStreaming;

    @BeforeEach
    void setUp() {
        categoriaSalario = new Categoria("Salário", true, TipoCategoria.RENDA_FIXA);
        categoriaAlimentacao = new Categoria("Alimentação", true, TipoCategoria.DESPESA_ESSENCIAL);
        categoriaStreaming = new Categoria("Netflix", false, TipoCategoria.DESPESA_SUPERFLUA);
    }

    @Test
    @DisplayName("Deve salvar categoria com sucesso")
    void save_ShouldSaveCategoria() {
        Categoria savedCategoria = categoriaRepository.save(categoriaSalario);

        assertNotNull(savedCategoria.getId());
        assertEquals("Salário", savedCategoria.getNome());
        assertTrue(savedCategoria.getEssencial());
        assertEquals(TipoCategoria.RENDA_FIXA, savedCategoria.getTipo());
    }

    @Test
    @DisplayName("Deve buscar categoria por ID quando existe")
    void findById_ShouldReturnCategoria_WhenExists() {
        Categoria savedCategoria = categoriaRepository.save(categoriaSalario);

        Optional<Categoria> resultado = categoriaRepository.findById(savedCategoria.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Salário", resultado.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar vazio quando categoria não existe")
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<Categoria> resultado = categoriaRepository.findById(999L);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve buscar todas as categorias")
    void findAll_ShouldReturnAllCategorias() {
        categoriaRepository.save(categoriaSalario);
        categoriaRepository.save(categoriaAlimentacao);
        categoriaRepository.save(categoriaStreaming);

        List<Categoria> resultado = categoriaRepository.findAll();

        assertEquals(3, resultado.size());
    }

    @Test
    @DisplayName("Deve buscar categorias por tipo")
    void findByTipo_ShouldReturnCategoriasByTipo() {
        categoriaRepository.save(categoriaSalario);
        Categoria categoriaFreelancer = new Categoria("Freelancer", false, TipoCategoria.RENDA_VARIAVEL);
        categoriaRepository.save(categoriaFreelancer);

        List<Categoria> rendaFixa = categoriaRepository.findByTipo(TipoCategoria.RENDA_FIXA);
        List<Categoria> rendaVariavel = categoriaRepository.findByTipo(TipoCategoria.RENDA_VARIAVEL);

        assertEquals(1, rendaFixa.size());
        assertEquals(TipoCategoria.RENDA_FIXA, rendaFixa.get(0).getTipo());
        assertEquals(1, rendaVariavel.size());
        assertEquals(TipoCategoria.RENDA_VARIAVEL, rendaVariavel.get(0).getTipo());
    }

    @Test
    @DisplayName("Deve buscar categorias por essencialidade")
    void findByEssencial_ShouldReturnCategoriasByEssencial() {
        categoriaRepository.save(categoriaSalario);
        categoriaRepository.save(categoriaAlimentacao);
        categoriaRepository.save(categoriaStreaming);

        List<Categoria> essenciais = categoriaRepository.findByEssencial(true);
        List<Categoria> naoEssenciais = categoriaRepository.findByEssencial(false);

        assertEquals(2, essenciais.size());
        essenciais.forEach(categoria -> assertTrue(categoria.getEssencial()));
        assertEquals(1, naoEssenciais.size());
        assertFalse(naoEssenciais.get(0).getEssencial());
    }

    @Test
    @DisplayName("Deve buscar categorias por tipo e essencialidade")
    void findByTipoAndEssencial_ShouldReturnCategoriasByTipoAndEssencial() {
        categoriaRepository.save(categoriaSalario);
        categoriaRepository.save(categoriaAlimentacao);
        categoriaRepository.save(categoriaStreaming);

        List<Categoria> despesasEssenciais = categoriaRepository.findByTipoAndEssencial(
                TipoCategoria.DESPESA_ESSENCIAL, true);
        List<Categoria> despesasSuperfluas = categoriaRepository.findByTipoAndEssencial(
                TipoCategoria.DESPESA_SUPERFLUA, false);

        assertEquals(1, despesasEssenciais.size());
        assertEquals(TipoCategoria.DESPESA_ESSENCIAL, despesasEssenciais.get(0).getTipo());
        assertTrue(despesasEssenciais.get(0).getEssencial());
        assertEquals(1, despesasSuperfluas.size());
        assertEquals(TipoCategoria.DESPESA_SUPERFLUA, despesasSuperfluas.get(0).getTipo());
        assertFalse(despesasSuperfluas.get(0).getEssencial());
    }

    @Test
    @DisplayName("Deve buscar categoria por nome quando existe")
    void findByNome_ShouldReturnCategoria_WhenExists() {
        categoriaRepository.save(categoriaSalario);

        Optional<Categoria> resultado = categoriaRepository.findByNome("Salário");

        assertTrue(resultado.isPresent());
        assertEquals("Salário", resultado.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar vazio quando buscar por nome inexistente")
    void findByNome_ShouldReturnEmpty_WhenNotExists() {
        Optional<Categoria> resultado = categoriaRepository.findByNome("Inexistente");

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve buscar categorias por lista de tipos")
    void findByTipoIn_ShouldReturnCategoriasByTipoIn() {
        categoriaRepository.save(categoriaSalario);
        categoriaRepository.save(categoriaAlimentacao);
        categoriaRepository.save(categoriaStreaming);

        List<TipoCategoria> tipos = List.of(TipoCategoria.RENDA_FIXA, TipoCategoria.DESPESA_ESSENCIAL);
        List<Categoria> resultado = categoriaRepository.findByTipoIn(tipos);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(c -> c.getTipo() == TipoCategoria.RENDA_FIXA));
        assertTrue(resultado.stream().anyMatch(c -> c.getTipo() == TipoCategoria.DESPESA_ESSENCIAL));
    }

    @Test
    @DisplayName("Deve contar categorias essenciais")
    void countCategoriasEssenciais_ShouldReturnCount() {
        categoriaRepository.save(categoriaSalario);
        categoriaRepository.save(categoriaAlimentacao);
        categoriaRepository.save(categoriaStreaming);

        Long resultado = categoriaRepository.countCategoriasEssenciais();

        assertEquals(2L, resultado);
    }

    @Test
    @DisplayName("Deve contar categorias supérfluas")
    void countCategoriasSuperfluas_ShouldReturnCount() {
        categoriaRepository.save(categoriaSalario);
        categoriaRepository.save(categoriaAlimentacao);
        categoriaRepository.save(categoriaStreaming);

        Long resultado = categoriaRepository.countCategoriasSuperfluas();

        assertEquals(1L, resultado);
    }

    @Test
    @DisplayName("Deve excluir categoria por ID")
    void deleteById_ShouldDeleteCategoria() {
        Categoria savedCategoria = categoriaRepository.save(categoriaSalario);
        Long id = savedCategoria.getId();

        categoriaRepository.deleteById(id);

        Optional<Categoria> resultado = categoriaRepository.findById(id);
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve atualizar categoria com sucesso")
    void update_ShouldUpdateCategoria() {
        Categoria savedCategoria = categoriaRepository.save(categoriaSalario);
        savedCategoria.setNome("Salário Atualizado");
        savedCategoria.setEssencial(false);

        Categoria updatedCategoria = categoriaRepository.save(savedCategoria);

        assertEquals("Salário Atualizado", updatedCategoria.getNome());
        assertFalse(updatedCategoria.getEssencial());
        assertEquals(savedCategoria.getId(), updatedCategoria.getId());
    }
}
