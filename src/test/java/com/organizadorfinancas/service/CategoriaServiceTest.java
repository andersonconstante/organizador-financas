package com.organizadorfinancas.service;

import com.organizadorfinancas.model.Categoria;
import com.organizadorfinancas.model.TipoCategoria;
import com.organizadorfinancas.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CategoriaService")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoriaSalario;
    private Categoria categoriaAlimentacao;
    private Categoria categoriaStreaming;

    @BeforeEach
    void setUp() {
        categoriaSalario = new Categoria("Salário", true, TipoCategoria.RENDA_FIXA);
        categoriaSalario.setId(1L);

        categoriaAlimentacao = new Categoria("Alimentação", true, TipoCategoria.DESPESA_ESSENCIAL);
        categoriaAlimentacao.setId(2L);

        categoriaStreaming = new Categoria("Netflix", false, TipoCategoria.DESPESA_SUPERFLUA);
        categoriaStreaming.setId(3L);
    }

    @Test
    @DisplayName("Deve retornar todas as categorias")
    void findAll_ShouldReturnAllCategorias() {
        List<Categoria> categorias = Arrays.asList(categoriaSalario, categoriaAlimentacao, categoriaStreaming);
        when(categoriaRepository.findAll()).thenReturn(categorias);

        List<Categoria> resultado = categoriaService.findAll();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals("Salário", resultado.get(0).getNome());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar categoria por ID quando existe")
    void findById_ShouldReturnCategoria_WhenExists() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaSalario));

        Optional<Categoria> resultado = categoriaService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Salário", resultado.get().getNome());
        verify(categoriaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar vazio quando categoria não existe")
    void findById_ShouldReturnEmpty_WhenNotExists() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Categoria> resultado = categoriaService.findById(99L);

        assertFalse(resultado.isPresent());
        verify(categoriaRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve salvar categoria com sucesso")
    void save_ShouldSaveCategoria() {
        Categoria novaCategoria = new Categoria("Transporte", true, TipoCategoria.DESPESA_ESSENCIAL);
        Categoria savedCategoria = new Categoria("Transporte", true, TipoCategoria.DESPESA_ESSENCIAL);
        savedCategoria.setId(4L);

        when(categoriaRepository.save(any(Categoria.class))).thenReturn(savedCategoria);

        Categoria resultado = categoriaService.save(novaCategoria);

        assertNotNull(resultado);
        assertEquals(4L, resultado.getId());
        assertEquals("Transporte", resultado.getNome());
        verify(categoriaRepository, times(1)).save(novaCategoria);
    }

    @Test
    @DisplayName("Deve excluir categoria por ID")
    void deleteById_ShouldDeleteCategoria() {
        categoriaService.deleteById(1L);

        verify(categoriaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve buscar categorias por tipo")
    void findByTipo_ShouldReturnCategoriasByTipo() {
        List<Categoria> categoriasRenda = Arrays.asList(categoriaSalario);
        when(categoriaRepository.findByTipo(TipoCategoria.RENDA_FIXA)).thenReturn(categoriasRenda);

        List<Categoria> resultado = categoriaService.findByTipo(TipoCategoria.RENDA_FIXA);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(TipoCategoria.RENDA_FIXA, resultado.get(0).getTipo());
        verify(categoriaRepository, times(1)).findByTipo(TipoCategoria.RENDA_FIXA);
    }

    @Test
    @DisplayName("Deve buscar categorias por essencialidade")
    void findByEssencial_ShouldReturnCategoriasByEssencial() {
        List<Categoria> categoriasEssenciais = Arrays.asList(categoriaSalario, categoriaAlimentacao);
        when(categoriaRepository.findByEssencial(true)).thenReturn(categoriasEssenciais);

        List<Categoria> resultado = categoriaService.findByEssencial(true);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.get(0).getEssencial());
        assertTrue(resultado.get(1).getEssencial());
        verify(categoriaRepository, times(1)).findByEssencial(true);
    }

    @Test
    @DisplayName("Deve buscar renda fixa")
    void findRendaFixa_ShouldReturnRendaFixa() {
        List<Categoria> rendaFixa = Arrays.asList(categoriaSalario);
        when(categoriaRepository.findByTipo(TipoCategoria.RENDA_FIXA)).thenReturn(rendaFixa);

        List<Categoria> resultado = categoriaService.findRendaFixa();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(TipoCategoria.RENDA_FIXA, resultado.get(0).getTipo());
        verify(categoriaRepository, times(1)).findByTipo(TipoCategoria.RENDA_FIXA);
    }

    @Test
    @DisplayName("Deve buscar renda variável")
    void findRendaVariavel_ShouldReturnRendaVariavel() {
        Categoria categoriaFreelancer = new Categoria("Freelancer", false, TipoCategoria.RENDA_VARIAVEL);
        List<Categoria> rendaVariavel = Arrays.asList(categoriaFreelancer);
        when(categoriaRepository.findByTipo(TipoCategoria.RENDA_VARIAVEL)).thenReturn(rendaVariavel);

        List<Categoria> resultado = categoriaService.findRendaVariavel();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(TipoCategoria.RENDA_VARIAVEL, resultado.get(0).getTipo());
        verify(categoriaRepository, times(1)).findByTipo(TipoCategoria.RENDA_VARIAVEL);
    }

    @Test
    @DisplayName("Deve buscar despesas essenciais")
    void findDespesasEssenciais_ShouldReturnDespesasEssenciais() {
        List<Categoria> despesasEssenciais = Arrays.asList(categoriaAlimentacao);
        when(categoriaRepository.findByTipoAndEssencial(TipoCategoria.DESPESA_ESSENCIAL, true))
                .thenReturn(despesasEssenciais);

        List<Categoria> resultado = categoriaService.findDespesasEssenciais();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(TipoCategoria.DESPESA_ESSENCIAL, resultado.get(0).getTipo());
        assertTrue(resultado.get(0).getEssencial());
        verify(categoriaRepository, times(1)).findByTipoAndEssencial(TipoCategoria.DESPESA_ESSENCIAL, true);
    }

    @Test
    @DisplayName("Deve buscar despesas supérfluas")
    void findDespesasSuperfluas_ShouldReturnDespesasSuperfluas() {
        List<Categoria> despesasSuperfluas = Arrays.asList(categoriaStreaming);
        when(categoriaRepository.findByTipoAndEssencial(TipoCategoria.DESPESA_SUPERFLUA, false))
                .thenReturn(despesasSuperfluas);

        List<Categoria> resultado = categoriaService.findDespesasSuperfluas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(TipoCategoria.DESPESA_SUPERFLUA, resultado.get(0).getTipo());
        assertFalse(resultado.get(0).getEssencial());
        verify(categoriaRepository, times(1)).findByTipoAndEssencial(TipoCategoria.DESPESA_SUPERFLUA, false);
    }

    @Test
    @DisplayName("Deve buscar gastos invisíveis")
    void findGastosInvisiveis_ShouldReturnGastosInvisiveis() {
        Categoria categoriaCafe = new Categoria("Café", false, TipoCategoria.GASTO_INVISIVEL);
        List<Categoria> gastosInvisiveis = Arrays.asList(categoriaCafe);
        when(categoriaRepository.findByTipo(TipoCategoria.GASTO_INVISIVEL)).thenReturn(gastosInvisiveis);

        List<Categoria> resultado = categoriaService.findGastosInvisiveis();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(TipoCategoria.GASTO_INVISIVEL, resultado.get(0).getTipo());
        verify(categoriaRepository, times(1)).findByTipo(TipoCategoria.GASTO_INVISIVEL);
    }

    @Test
    @DisplayName("Deve buscar categoria por nome quando existe")
    void findByNome_ShouldReturnCategoria_WhenExists() {
        when(categoriaRepository.findByNome("Salário")).thenReturn(Optional.of(categoriaSalario));

        Optional<Categoria> resultado = categoriaService.findByNome("Salário");

        assertTrue(resultado.isPresent());
        assertEquals("Salário", resultado.get().getNome());
        verify(categoriaRepository, times(1)).findByNome("Salário");
    }

    @Test
    @DisplayName("Deve retornar vazio quando buscar por nome inexistente")
    void findByNome_ShouldReturnEmpty_WhenNotExists() {
        when(categoriaRepository.findByNome("Inexistente")).thenReturn(Optional.empty());

        Optional<Categoria> resultado = categoriaService.findByNome("Inexistente");

        assertFalse(resultado.isPresent());
        verify(categoriaRepository, times(1)).findByNome("Inexistente");
    }

    @Test
    @DisplayName("Deve verificar se categoria existe por nome")
    void existsByNome_ShouldReturnTrue_WhenExists() {
        when(categoriaRepository.findByNome("Salário")).thenReturn(Optional.of(categoriaSalario));

        boolean resultado = categoriaService.existsByNome("Salário");

        assertTrue(resultado);
        verify(categoriaRepository, times(1)).findByNome("Salário");
    }

    @Test
    @DisplayName("Deve retornar falso quando categoria não existe por nome")
    void existsByNome_ShouldReturnFalse_WhenNotExists() {
        when(categoriaRepository.findByNome("Inexistente")).thenReturn(Optional.empty());

        boolean resultado = categoriaService.existsByNome("Inexistente");

        assertFalse(resultado);
        verify(categoriaRepository, times(1)).findByNome("Inexistente");
    }

    @Test
    @DisplayName("Deve contar categorias essenciais")
    void countCategoriasEssenciais_ShouldReturnCount() {
        when(categoriaRepository.countCategoriasEssenciais()).thenReturn(5L);

        Long resultado = categoriaService.countCategoriasEssenciais();

        assertEquals(5L, resultado);
        verify(categoriaRepository, times(1)).countCategoriasEssenciais();
    }

    @Test
    @DisplayName("Deve contar categorias supérfluas")
    void countCategoriasSuperfluas_ShouldReturnCount() {
        when(categoriaRepository.countCategoriasSuperfluas()).thenReturn(3L);

        Long resultado = categoriaService.countCategoriasSuperfluas();

        assertEquals(3L, resultado);
        verify(categoriaRepository, times(1)).countCategoriasSuperfluas();
    }
}
