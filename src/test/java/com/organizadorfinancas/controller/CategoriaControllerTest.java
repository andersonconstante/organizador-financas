package com.organizadorfinancas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.organizadorfinancas.config.TestSecurityConfig;
import com.organizadorfinancas.model.Categoria;
import com.organizadorfinancas.model.TipoCategoria;
import com.organizadorfinancas.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Testes do CategoriaController")
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Categoria categoria1;
    private Categoria categoria2;

    @BeforeEach
    void setUp() {
        categoria1 = new Categoria("Salário", true, TipoCategoria.RENDA_FIXA);
        categoria1.setId(1L);

        categoria2 = new Categoria("Alimentação", true, TipoCategoria.DESPESA_ESSENCIAL);
        categoria2.setId(2L);
    }

    @Test
    @DisplayName("Deve retornar todas as categorias com sucesso")
    void findAll_ShouldReturnAllCategorias() throws Exception {
        List<Categoria> categorias = Arrays.asList(categoria1, categoria2);
        when(categoriaService.findAll()).thenReturn(categorias);

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Salário"))
                .andExpect(jsonPath("$[0].essencial").value(true))
                .andExpect(jsonPath("$[0].tipo").value("RENDA_FIXA"));

        verify(categoriaService, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar categoria por ID com sucesso")
    void findById_ShouldReturnCategoria_WhenExists() throws Exception {
        when(categoriaService.findById(1L)).thenReturn(Optional.of(categoria1));

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Salário"));

        verify(categoriaService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 quando categoria não existe")
    void findById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        when(categoriaService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categorias/99"))
                .andExpect(status().isNotFound());

        verify(categoriaService, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve criar nova categoria com sucesso")
    void create_ShouldCreateCategoria_WhenValid() throws Exception {
        Categoria novaCategoria = new Categoria("Transporte", true, TipoCategoria.DESPESA_ESSENCIAL);
        Categoria savedCategoria = new Categoria("Transporte", true, TipoCategoria.DESPESA_ESSENCIAL);
        savedCategoria.setId(3L);

        when(categoriaService.existsByNome("Transporte")).thenReturn(false);
        when(categoriaService.save(any(Categoria.class))).thenReturn(savedCategoria);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novaCategoria)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nome").value("Transporte"));

        verify(categoriaService, times(1)).existsByNome("Transporte");
        verify(categoriaService, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve retornar 400 quando tentar criar categoria duplicada")
    void create_ShouldReturnBadRequest_WhenDuplicate() throws Exception {
        Categoria novaCategoria = new Categoria("Salário", true, TipoCategoria.RENDA_FIXA);

        when(categoriaService.existsByNome("Salário")).thenReturn(true);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novaCategoria)))
                .andExpect(status().isBadRequest());

        verify(categoriaService, times(1)).existsByNome("Salário");
        verify(categoriaService, never()).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve atualizar categoria com sucesso")
    void update_ShouldUpdateCategoria_WhenExists() throws Exception {
        Categoria categoriaAtualizada = new Categoria("Salário Atualizado", true, TipoCategoria.RENDA_FIXA);
        categoriaAtualizada.setId(1L);

        when(categoriaService.findById(1L)).thenReturn(Optional.of(categoria1));
        when(categoriaService.save(any(Categoria.class))).thenReturn(categoriaAtualizada);

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaAtualizada)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Salário Atualizado"));

        verify(categoriaService, times(1)).findById(1L);
        verify(categoriaService, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve retornar 404 quando tentar atualizar categoria inexistente")
    void update_ShouldReturnNotFound_WhenNotExists() throws Exception {
        Categoria categoriaAtualizada = new Categoria("Teste", true, TipoCategoria.RENDA_FIXA);

        when(categoriaService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/categorias/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaAtualizada)))
                .andExpect(status().isNotFound());

        verify(categoriaService, times(1)).findById(99L);
        verify(categoriaService, never()).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve excluir categoria com sucesso")
    void deleteById_ShouldDeleteCategoria_WhenExists() throws Exception {
        when(categoriaService.findById(1L)).thenReturn(Optional.of(categoria1));

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isNoContent());

        verify(categoriaService, times(1)).findById(1L);
        verify(categoriaService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 quando tentar excluir categoria inexistente")
    void deleteById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        when(categoriaService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/categorias/99"))
                .andExpect(status().isNotFound());

        verify(categoriaService, times(1)).findById(99L);
        verify(categoriaService, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve buscar categorias por tipo")
    void findByTipo_ShouldReturnCategoriasByTipo() throws Exception {
        List<Categoria> categorias = Arrays.asList(categoria1);
        when(categoriaService.findByTipo(TipoCategoria.RENDA_FIXA)).thenReturn(categorias);

        mockMvc.perform(get("/api/categorias/tipo/RENDA_FIXA"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("RENDA_FIXA"));

        verify(categoriaService, times(1)).findByTipo(TipoCategoria.RENDA_FIXA);
    }

    @Test
    @DisplayName("Deve buscar categorias por essencialidade")
    void findByEssencial_ShouldReturnCategoriasByEssencial() throws Exception {
        List<Categoria> categorias = Arrays.asList(categoria1, categoria2);
        when(categoriaService.findByEssencial(true)).thenReturn(categorias);

        mockMvc.perform(get("/api/categorias/essencial/true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(categoriaService, times(1)).findByEssencial(true);
    }

    @Test
    @DisplayName("Deve buscar renda fixa")
    void findRendaFixa_ShouldReturnRendaFixa() throws Exception {
        List<Categoria> categorias = Arrays.asList(categoria1);
        when(categoriaService.findRendaFixa()).thenReturn(categorias);

        mockMvc.perform(get("/api/categorias/renda-fixa"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(categoriaService, times(1)).findRendaFixa();
    }

    @Test
    @DisplayName("Deve buscar despesas essenciais")
    void findDespesasEssenciais_ShouldReturnDespesasEssenciais() throws Exception {
        List<Categoria> categorias = Arrays.asList(categoria2);
        when(categoriaService.findDespesasEssenciais()).thenReturn(categorias);

        mockMvc.perform(get("/api/categorias/despesas-essenciais"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(categoriaService, times(1)).findDespesasEssenciais();
    }

    @Test
    @DisplayName("Deve buscar despesas supérfluas")
    void findDespesasSuperfluas_ShouldReturnDespesasSuperfluas() throws Exception {
        Categoria categoriaSuperflua = new Categoria("Streaming", false, TipoCategoria.DESPESA_SUPERFLUA);
        List<Categoria> categorias = Arrays.asList(categoriaSuperflua);
        when(categoriaService.findDespesasSuperfluas()).thenReturn(categorias);

        mockMvc.perform(get("/api/categorias/despesas-superfluas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(categoriaService, times(1)).findDespesasSuperfluas();
    }

    @Test
    @DisplayName("Deve buscar gastos invisíveis")
    void findGastosInvisiveis_ShouldReturnGastosInvisiveis() throws Exception {
        Categoria categoriaInvisivel = new Categoria("Café", false, TipoCategoria.GASTO_INVISIVEL);
        List<Categoria> categorias = Arrays.asList(categoriaInvisivel);
        when(categoriaService.findGastosInvisiveis()).thenReturn(categorias);

        mockMvc.perform(get("/api/categorias/gastos-invisiveis"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(categoriaService, times(1)).findGastosInvisiveis();
    }
}
