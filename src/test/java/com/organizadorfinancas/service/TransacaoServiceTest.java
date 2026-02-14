package com.organizadorfinancas.service;

import com.organizadorfinancas.model.*;
import com.organizadorfinancas.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do TransacaoService")
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    private Categoria categoriaSalario;
    private Categoria categoriaAlimentacao;
    private Transacao transacaoSalario;
    private Transacao transacaoAlimentacao;
    private Transacao transacaoParcelada;

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

        transacaoParcelada = new Transacao("Notebook", new BigDecimal("3600.00"),
                LocalDate.of(2026, 2, 1), TipoTransacao.DESPESA, false, categoriaAlimentacao);
        transacaoParcelada.setParcelas(12);
        transacaoParcelada.setParcelaAtual(1);
        transacaoParcelada.setId(3L);
    }

    @Test
    @DisplayName("Deve retornar todas as transações")
    void findAll_ShouldReturnAllTransacoes() {
        List<Transacao> transacoes = Arrays.asList(transacaoSalario, transacaoAlimentacao, transacaoParcelada);
        when(transacaoRepository.findAll()).thenReturn(transacoes);

        List<Transacao> resultado = transacaoService.findAll();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals("Salário Fevereiro", resultado.get(0).getDescricao());
        verify(transacaoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar transação por ID quando existe")
    void findById_ShouldReturnTransacao_WhenExists() {
        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacaoSalario));

        Optional<Transacao> resultado = transacaoService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Salário Fevereiro", resultado.get().getDescricao());
        verify(transacaoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar vazio quando transação não existe")
    void findById_ShouldReturnEmpty_WhenNotExists() {
        when(transacaoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Transacao> resultado = transacaoService.findById(99L);

        assertFalse(resultado.isPresent());
        verify(transacaoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve salvar transação com sucesso")
    void save_ShouldSaveTransacao() {
        Transacao novaTransacao = new Transacao("Freelancer", new BigDecimal("1500.00"),
                LocalDate.of(2026, 2, 15), TipoTransacao.RECEITA, false, categoriaSalario);
        Transacao savedTransacao = new Transacao("Freelancer", new BigDecimal("1500.00"),
                LocalDate.of(2026, 2, 15), TipoTransacao.RECEITA, false, categoriaSalario);
        savedTransacao.setId(4L);

        when(transacaoRepository.save(any(Transacao.class))).thenReturn(savedTransacao);

        Transacao resultado = transacaoService.save(novaTransacao);

        assertNotNull(resultado);
        assertEquals(4L, resultado.getId());
        assertEquals("Freelancer", resultado.getDescricao());
        verify(transacaoRepository, times(1)).save(novaTransacao);
    }

    @Test
    @DisplayName("Deve excluir transação por ID")
    void deleteById_ShouldDeleteTransacao() {
        transacaoService.deleteById(1L);

        verify(transacaoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve buscar transações por tipo")
    void findByTipo_ShouldReturnTransacoesByTipo() {
        List<Transacao> receitas = Arrays.asList(transacaoSalario);
        when(transacaoRepository.findByTipo(TipoTransacao.RECEITA)).thenReturn(receitas);

        List<Transacao> resultado = transacaoService.findByTipo(TipoTransacao.RECEITA);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(TipoTransacao.RECEITA, resultado.get(0).getTipo());
        verify(transacaoRepository, times(1)).findByTipo(TipoTransacao.RECEITA);
    }

    @Test
    @DisplayName("Deve buscar todas as despesas")
    void findAllDespesas_ShouldReturnDespesas() {
        List<Transacao> despesas = Arrays.asList(transacaoAlimentacao, transacaoParcelada);
        when(transacaoRepository.findByTipo(TipoTransacao.DESPESA)).thenReturn(despesas);

        List<Transacao> resultado = transacaoService.findAllDespesas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        resultado.forEach(transacao -> assertEquals(TipoTransacao.DESPESA, transacao.getTipo()));
        verify(transacaoRepository, times(1)).findByTipo(TipoTransacao.DESPESA);
    }

    @Test
    @DisplayName("Deve buscar todas as receitas")
    void findAllReceitas_ShouldReturnReceitas() {
        List<Transacao> receitas = Arrays.asList(transacaoSalario);
        when(transacaoRepository.findByTipo(TipoTransacao.RECEITA)).thenReturn(receitas);

        List<Transacao> resultado = transacaoService.findAllReceitas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(TipoTransacao.RECEITA, resultado.get(0).getTipo());
        verify(transacaoRepository, times(1)).findByTipo(TipoTransacao.RECEITA);
    }

    @Test
    @DisplayName("Deve buscar gastos recorrentes")
    void findGastosRecorrentes_ShouldReturnGastosRecorrentes() {
        List<Transacao> recorrentes = Arrays.asList(transacaoAlimentacao);
        when(transacaoRepository.findGastosRecorrentes()).thenReturn(recorrentes);

        List<Transacao> resultado = transacaoService.findGastosRecorrentes();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getRecorrente());
        verify(transacaoRepository, times(1)).findGastosRecorrentes();
    }

    @Test
    @DisplayName("Deve buscar gastos supérfluos")
    void findGastosSuperfluos_ShouldReturnGastosSuperfluos() {
        Categoria categoriaStreaming = new Categoria("Netflix", false, TipoCategoria.DESPESA_SUPERFLUA);
        Transacao transacaoStreaming = new Transacao("Netflix", new BigDecimal("39.90"),
                LocalDate.of(2026, 2, 10), TipoTransacao.DESPESA, true, categoriaStreaming);
        List<Transacao> superfluos = Arrays.asList(transacaoStreaming);

        when(transacaoRepository.findGastosSuperfluos()).thenReturn(superfluos);

        List<Transacao> resultado = transacaoService.findGastosSuperfluos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(transacaoRepository, times(1)).findGastosSuperfluos();
    }

    @Test
    @DisplayName("Deve buscar despesas parceladas")
    void findDespesasParceladas_ShouldReturnDespesasParceladas() {
        List<Transacao> parceladas = Arrays.asList(transacaoParcelada);
        when(transacaoRepository.findDespesasParceladas()).thenReturn(parceladas);

        List<Transacao> resultado = transacaoService.findDespesasParceladas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(12, resultado.get(0).getParcelas());
        verify(transacaoRepository, times(1)).findDespesasParceladas();
    }

    @Test
    @DisplayName("Deve buscar transações por período")
    void findByPeriodo_ShouldReturnTransacoesByPeriodo() {
        List<Transacao> transacoes = Arrays.asList(transacaoSalario, transacaoAlimentacao);
        LocalDate dataInicio = LocalDate.of(2026, 2, 1);
        LocalDate dataFim = LocalDate.of(2026, 2, 28);

        when(transacaoRepository.findByDataBetween(dataInicio, dataFim)).thenReturn(transacoes);

        List<Transacao> resultado = transacaoService.findByPeriodo(dataInicio, dataFim);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(transacaoRepository, times(1)).findByDataBetween(dataInicio, dataFim);
    }

    @Test
    @DisplayName("Deve buscar transações por categoria")
    void findByCategoriaId_ShouldReturnTransacoesByCategoria() {
        List<Transacao> transacoes = Arrays.asList(transacaoAlimentacao);
        when(transacaoRepository.findByCategoriaId(2L)).thenReturn(transacoes);

        List<Transacao> resultado = transacaoService.findByCategoriaId(2L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(2L, resultado.get(0).getCategoria().getId());
        verify(transacaoRepository, times(1)).findByCategoriaId(2L);
    }

    @Test
    @DisplayName("Deve calcular total de despesas do mês")
    void getTotalDespesas_ShouldReturnTotalDespesas() {
        BigDecimal totalEsperado = new BigDecimal("1500.00");
        when(transacaoRepository.sumByTipoAndPeriodo(
                eq(TipoTransacao.DESPESA), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(totalEsperado);

        BigDecimal resultado = transacaoService.getTotalDespesas();

        assertEquals(totalEsperado, resultado);
        verify(transacaoRepository, times(1)).sumByTipoAndPeriodo(
                eq(TipoTransacao.DESPESA), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Deve calcular total de receitas do mês")
    void getTotalReceitas_ShouldReturnTotalReceitas() {
        BigDecimal totalEsperado = new BigDecimal("6500.00");
        when(transacaoRepository.sumByTipoAndPeriodo(
                eq(TipoTransacao.RECEITA), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(totalEsperado);

        BigDecimal resultado = transacaoService.getTotalReceitas();

        assertEquals(totalEsperado, resultado);
        verify(transacaoRepository, times(1)).sumByTipoAndPeriodo(
                eq(TipoTransacao.RECEITA), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Deve calcular total de gastos recorrentes")
    void getTotalGastosRecorrentes_ShouldReturnTotalGastosRecorrentes() {
        BigDecimal totalEsperado = new BigDecimal("800.00");
        when(transacaoRepository.sumByTipoAndRecorrente(TipoTransacao.DESPESA))
                .thenReturn(totalEsperado);

        BigDecimal resultado = transacaoService.getTotalGastosRecorrentes();

        assertEquals(totalEsperado, resultado);
        verify(transacaoRepository, times(1)).sumByTipoAndRecorrente(TipoTransacao.DESPESA);
    }

    @Test
    @DisplayName("Deve calcular total de despesas essenciais")
    void getTotalDespesasEssenciais_ShouldReturnTotalDespesasEssenciais() {
        BigDecimal totalEsperado = new BigDecimal("1200.00");
        when(transacaoRepository.sumByEssencialAndTipo(true, TipoTransacao.DESPESA))
                .thenReturn(totalEsperado);

        BigDecimal resultado = transacaoService.getTotalDespesasEssenciais();

        assertEquals(totalEsperado, resultado);
        verify(transacaoRepository, times(1)).sumByEssencialAndTipo(true, TipoTransacao.DESPESA);
    }

    @Test
    @DisplayName("Deve calcular total de despesas supérfluas")
    void getTotalDespesasSuperfluas_ShouldReturnTotalDespesasSuperfluas() {
        BigDecimal totalEsperado = new BigDecimal("300.00");
        when(transacaoRepository.sumByEssencialAndTipo(false, TipoTransacao.DESPESA))
                .thenReturn(totalEsperado);

        BigDecimal resultado = transacaoService.getTotalDespesasSuperfluas();

        assertEquals(totalEsperado, resultado);
        verify(transacaoRepository, times(1)).sumByEssencialAndTipo(false, TipoTransacao.DESPESA);
    }

    @Test
    @DisplayName("Deve calcular totais por categoria")
    void getTotaisPorCategoria_ShouldReturnTotaisPorCategoria() {
        Object[] total1 = {"Alimentação", new BigDecimal("400.00")};
        Object[] total2 = {"Transporte", new BigDecimal("200.00")};
        List<Object[]> totais = Arrays.asList(total1, total2);

        when(transacaoRepository.findTotalPorCategoria(TipoTransacao.DESPESA)).thenReturn(totais);

        List<Object[]> resultado = transacaoService.getTotaisPorCategoria(TipoTransacao.DESPESA);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Alimentação", resultado.get(0)[0]);
        assertEquals(new BigDecimal("400.00"), resultado.get(0)[1]);
        verify(transacaoRepository, times(1)).findTotalPorCategoria(TipoTransacao.DESPESA);
    }

    @Test
    @DisplayName("Deve calcular saldo mensal")
    void getSaldoMensal_ShouldReturnSaldoMensal() {
        BigDecimal receitas = new BigDecimal("6500.00");
        BigDecimal despesas = new BigDecimal("1500.00");
        BigDecimal saldoEsperado = new BigDecimal("5000.00");

        when(transacaoRepository.sumByTipoAndPeriodo(
                eq(TipoTransacao.RECEITA), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(receitas);
        when(transacaoRepository.sumByTipoAndPeriodo(
                eq(TipoTransacao.DESPESA), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(despesas);

        BigDecimal resultado = transacaoService.getSaldoMensal();

        assertEquals(saldoEsperado, resultado);
        verify(transacaoRepository, times(2)).sumByTipoAndPeriodo(
                any(TipoTransacao.class), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Deve calcular valor mensal de transação parcelada")
    void getValorMensal_ShouldReturnValorMensal_WhenParcelada() {
        BigDecimal valorTotal = new BigDecimal("3600.00");
        transacaoParcelada.setParcelas(12);
        
        BigDecimal resultado = transacaoParcelada.getValorMensal();
        
        assertEquals(new BigDecimal("300.00"), resultado);
    }

    @Test
    @DisplayName("Deve retornar valor total quando transação não é parcelada")
    void getValorMensal_ShouldReturnValorTotal_WhenNotParcelada() {
        BigDecimal valorTotal = new BigDecimal("400.00");
        transacaoAlimentacao.setParcelas(1);
        
        BigDecimal resultado = transacaoAlimentacao.getValorMensal();
        
        assertEquals(valorTotal, resultado);
    }
}
