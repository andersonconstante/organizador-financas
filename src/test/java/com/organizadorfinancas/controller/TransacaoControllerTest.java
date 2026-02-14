package com.organizadorfinancas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.organizadorfinancas.config.TestSecurityConfig;
import com.organizadorfinancas.model.*;
import com.organizadorfinancas.service.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransacaoController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Testes do TransacaoController")
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransacaoService transacaoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Categoria categoriaSalario;
    private Categoria categoriaAlimentacao;
    private Transacao transacaoSalario;
    private Transacao transacaoAlimentacao;

    @BeforeEach
    void setUp() {
        categoriaSalario = new Categoria("Salário", true, TipoCategoria.RENDA_FIXA);
        categoriaSalario.setId(1L);

        categoriaAlimentacao = new Categoria("Alimentação", true, TipoCategoria.DESPESA_ESSENCIAL);
        categoriaAlimentacao.setId(2L);

        transacaoSalario = new Transacao("Salário Fevereiro", new BigDecimal("5000.00"),
                LocalDate.of(2026, 2, 5), TipoTransacao.RECEITA, false, categoriaSalario);
        transacaoSalario.setId(1L);

        transacaoAlimentacao = new Transacao("Supermercado", new BigDecimal("400.00"),
                LocalDate.of(2026, 2, 10), TipoTransacao.DESPESA, true, categoriaAlimentacao);
        transacaoAlimentacao.setId(2L);
    }

    @Test
    @DisplayName("Deve retornar todas as transações com sucesso")
    void findAll_ShouldReturnAllTransacoes() throws Exception {
        List<Transacao> transacoes = Arrays.asList(transacaoSalario, transacaoAlimentacao);
        when(transacaoService.findAll()).thenReturn(transacoes);

        mockMvc.perform(get("/api/transacoes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].descricao").value("Salário Fevereiro"))
                .andExpect(jsonPath("$[0].valor").value(5000.00))
                .andExpect(jsonPath("$[0].tipo").value("RECEITA"));

        verify(transacaoService, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar transação por ID com sucesso")
    void findById_ShouldReturnTransacao_WhenExists() throws Exception {
        when(transacaoService.findById(1L)).thenReturn(Optional.of(transacaoSalario));

        mockMvc.perform(get("/api/transacoes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descricao").value("Salário Fevereiro"))
                .andExpect(jsonPath("$.valor").value(5000.00));

        verify(transacaoService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 quando transação não existe")
    void findById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        when(transacaoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/transacoes/99"))
                .andExpect(status().isNotFound());

        verify(transacaoService, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve criar nova transação com sucesso")
    void create_ShouldCreateTransacao_WhenValid() throws Exception {
        Transacao novaTransacao = new Transacao("Freelancer", new BigDecimal("1500.00"),
                LocalDate.of(2026, 2, 15), TipoTransacao.RECEITA, false, categoriaSalario);
        Transacao savedTransacao = new Transacao("Freelancer", new BigDecimal("1500.00"),
                LocalDate.of(2026, 2, 15), TipoTransacao.RECEITA, false, categoriaSalario);
        savedTransacao.setId(3L);

        when(transacaoService.save(any(Transacao.class))).thenReturn(savedTransacao);

        mockMvc.perform(post("/api/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novaTransacao)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.descricao").value("Freelancer"))
                .andExpect(jsonPath("$.valor").value(1500.00));

        verify(transacaoService, times(1)).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve retornar 400 quando criar transação inválida")
    void create_ShouldReturnBadRequest_WhenInvalid() throws Exception {
        Transacao transacaoInvalida = new Transacao();
        transacaoInvalida.setDescricao(""); // Descrição vazia
        transacaoInvalida.setValor(BigDecimal.ZERO); // Valor zero
        transacaoInvalida.setParcelas(1); // Evitar null pointer
        transacaoInvalida.setParcelaAtual(1);

        mockMvc.perform(post("/api/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transacaoInvalida)))
                .andExpect(status().isBadRequest());

        verify(transacaoService, never()).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve atualizar transação com sucesso")
    void update_ShouldUpdateTransacao_WhenExists() throws Exception {
        Transacao transacaoAtualizada = new Transacao("Salário Atualizado", new BigDecimal("5500.00"),
                LocalDate.of(2026, 2, 5), TipoTransacao.RECEITA, false, categoriaSalario);
        transacaoAtualizada.setId(1L);

        when(transacaoService.findById(1L)).thenReturn(Optional.of(transacaoSalario));
        when(transacaoService.save(any(Transacao.class))).thenReturn(transacaoAtualizada);

        mockMvc.perform(put("/api/transacoes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transacaoAtualizada)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descricao").value("Salário Atualizado"))
                .andExpect(jsonPath("$.valor").value(5500.00));

        verify(transacaoService, times(1)).findById(1L);
        verify(transacaoService, times(1)).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve retornar 404 quando tentar atualizar transação inexistente")
    void update_ShouldReturnNotFound_WhenNotExists() throws Exception {
        Transacao transacaoAtualizada = new Transacao("Teste", new BigDecimal("100.00"),
                LocalDate.now(), TipoTransacao.DESPESA, false, categoriaAlimentacao);

        when(transacaoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/transacoes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transacaoAtualizada)))
                .andExpect(status().isNotFound());

        verify(transacaoService, times(1)).findById(99L);
        verify(transacaoService, never()).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve excluir transação com sucesso")
    void deleteById_ShouldDeleteTransacao_WhenExists() throws Exception {
        when(transacaoService.findById(1L)).thenReturn(Optional.of(transacaoSalario));

        mockMvc.perform(delete("/api/transacoes/1"))
                .andExpect(status().isNoContent());

        verify(transacaoService, times(1)).findById(1L);
        verify(transacaoService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 quando tentar excluir transação inexistente")
    void deleteById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        when(transacaoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/transacoes/99"))
                .andExpect(status().isNotFound());

        verify(transacaoService, times(1)).findById(99L);
        verify(transacaoService, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve buscar todas as despesas")
    void findAllDespesas_ShouldReturnDespesas() throws Exception {
        List<Transacao> despesas = Arrays.asList(transacaoAlimentacao);
        when(transacaoService.findAllDespesas()).thenReturn(despesas);

        mockMvc.perform(get("/api/transacoes/despesas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("DESPESA"));

        verify(transacaoService, times(1)).findAllDespesas();
    }

    @Test
    @DisplayName("Deve buscar todas as receitas")
    void findAllReceitas_ShouldReturnReceitas() throws Exception {
        List<Transacao> receitas = Arrays.asList(transacaoSalario);
        when(transacaoService.findAllReceitas()).thenReturn(receitas);

        mockMvc.perform(get("/api/transacoes/receitas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("RECEITA"));

        verify(transacaoService, times(1)).findAllReceitas();
    }

    @Test
    @DisplayName("Deve buscar gastos recorrentes")
    void findGastosRecorrentes_ShouldReturnGastosRecorrentes() throws Exception {
        List<Transacao> recorrentes = Arrays.asList(transacaoAlimentacao);
        when(transacaoService.findGastosRecorrentes()).thenReturn(recorrentes);

        mockMvc.perform(get("/api/transacoes/recorrentes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].recorrente").value(true));

        verify(transacaoService, times(1)).findGastosRecorrentes();
    }

    @Test
    @DisplayName("Deve buscar gastos supérfluos")
    void findGastosSuperfluos_ShouldReturnGastosSuperfluos() throws Exception {
        Categoria categoriaStreaming = new Categoria("Netflix", false, TipoCategoria.DESPESA_SUPERFLUA);
        Transacao transacaoStreaming = new Transacao("Netflix", new BigDecimal("39.90"),
                LocalDate.of(2026, 2, 10), TipoTransacao.DESPESA, true, categoriaStreaming);
        List<Transacao> superfluos = Arrays.asList(transacaoStreaming);

        when(transacaoService.findGastosSuperfluos()).thenReturn(superfluos);

        mockMvc.perform(get("/api/transacoes/superfluos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(transacaoService, times(1)).findGastosSuperfluos();
    }

    @Test
    @DisplayName("Deve buscar despesas parceladas")
    void findDespesasParceladas_ShouldReturnDespesasParceladas() throws Exception {
        Transacao transacaoParcelada = new Transacao("Notebook", new BigDecimal("3600.00"),
                LocalDate.of(2026, 2, 1), TipoTransacao.DESPESA, false, categoriaAlimentacao);
        transacaoParcelada.setParcelas(12);
        transacaoParcelada.setParcelaAtual(1);
        List<Transacao> parceladas = Arrays.asList(transacaoParcelada);

        when(transacaoService.findDespesasParceladas()).thenReturn(parceladas);

        mockMvc.perform(get("/api/transacoes/parceladas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].parcelas").value(12));

        verify(transacaoService, times(1)).findDespesasParceladas();
    }

    @Test
    @DisplayName("Deve buscar transações por período")
    void findByPeriodo_ShouldReturnTransacoesByPeriodo() throws Exception {
        List<Transacao> transacoes = Arrays.asList(transacaoSalario, transacaoAlimentacao);
        when(transacaoService.findByPeriodo(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28)))
                .thenReturn(transacoes);

        mockMvc.perform(get("/api/transacoes/periodo")
                        .param("dataInicio", "2026-02-01")
                        .param("dataFim", "2026-02-28"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(transacaoService, times(1)).findByPeriodo(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28));
    }

    @Test
    @DisplayName("Deve buscar transações por categoria")
    void findByCategoria_ShouldReturnTransacoesByCategoria() throws Exception {
        List<Transacao> transacoes = Arrays.asList(transacaoAlimentacao);
        when(transacaoService.findByCategoriaId(2L)).thenReturn(transacoes);

        mockMvc.perform(get("/api/transacoes/categoria/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(transacaoService, times(1)).findByCategoriaId(2L);
    }

    @Test
    @DisplayName("Deve retornar total de despesas do mês")
    void getTotalDespesas_ShouldReturnTotalDespesas() throws Exception {
        when(transacaoService.getTotalDespesas()).thenReturn(new BigDecimal("1500.00"));

        mockMvc.perform(get("/api/transacoes/resumo/despesas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("1500.00"));

        verify(transacaoService, times(1)).getTotalDespesas();
    }

    @Test
    @DisplayName("Deve retornar total de receitas do mês")
    void getTotalReceitas_ShouldReturnTotalReceitas() throws Exception {
        when(transacaoService.getTotalReceitas()).thenReturn(new BigDecimal("6500.00"));

        mockMvc.perform(get("/api/transacoes/resumo/receitas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("6500.00"));

        verify(transacaoService, times(1)).getTotalReceitas();
    }

    @Test
    @DisplayName("Deve retornar saldo mensal")
    void getSaldoMensal_ShouldReturnSaldoMensal() throws Exception {
        when(transacaoService.getSaldoMensal()).thenReturn(new BigDecimal("5000.00"));

        mockMvc.perform(get("/api/transacoes/resumo/saldo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("5000.00"));

        verify(transacaoService, times(1)).getSaldoMensal();
    }

    @Test
    @DisplayName("Deve retornar totais por categoria")
    void getTotaisPorCategoria_ShouldReturnTotaisPorCategoria() throws Exception {
        Object[] total1 = {"Alimentação", new BigDecimal("400.00")};
        Object[] total2 = {"Transporte", new BigDecimal("200.00")};
        List<Object[]> totais = Arrays.asList(total1, total2);

        when(transacaoService.getTotaisPorCategoria(TipoTransacao.DESPESA)).thenReturn(totais);

        mockMvc.perform(get("/api/transacoes/resumo/por-categoria")
                        .param("tipo", "DESPESA"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(transacaoService, times(1)).getTotaisPorCategoria(TipoTransacao.DESPESA);
    }
}
