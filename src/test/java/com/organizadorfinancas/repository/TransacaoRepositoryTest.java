package com.organizadorfinancas.repository;

import com.organizadorfinancas.model.Categoria;
import com.organizadorfinancas.model.TipoCategoria;
import com.organizadorfinancas.model.TipoTransacao;
import com.organizadorfinancas.model.Transacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes do TransacaoRepository")
class TransacaoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransacaoRepository transacaoRepository;

    private Categoria categoriaSalario;
    private Categoria categoriaAlimentacao;
    private Categoria categoriaStreaming;
    private Transacao transacaoSalario;
    private Transacao transacaoAlimentacao;
    private Transacao transacaoStreaming;
    private Transacao transacaoParcelada;

    @BeforeEach
    void setUp() {
        categoriaSalario = new Categoria("Salário", true, TipoCategoria.RENDA_FIXA);
        categoriaAlimentacao = new Categoria("Alimentação", true, TipoCategoria.DESPESA_ESSENCIAL);
        categoriaStreaming = new Categoria("Netflix", false, TipoCategoria.DESPESA_SUPERFLUA);

        entityManager.persist(categoriaSalario);
        entityManager.persist(categoriaAlimentacao);
        entityManager.persist(categoriaStreaming);

        transacaoSalario = new Transacao("Salário Fevereiro", new BigDecimal("5000.00"),
                LocalDate.of(2026, 2, 5), TipoTransacao.RECEITA, false, categoriaSalario);
        
        transacaoAlimentacao = new Transacao("Supermercado", new BigDecimal("400.00"),
                LocalDate.of(2026, 2, 10), TipoTransacao.DESPESA, true, categoriaAlimentacao);
        
        transacaoStreaming = new Transacao("Netflix", new BigDecimal("39.90"),
                LocalDate.of(2026, 2, 10), TipoTransacao.DESPESA, true, categoriaStreaming);
        
        transacaoParcelada = new Transacao("Notebook", new BigDecimal("3600.00"),
                LocalDate.of(2026, 2, 1), TipoTransacao.DESPESA, false, categoriaAlimentacao);
        transacaoParcelada.setParcelas(12);
        transacaoParcelada.setParcelaAtual(1);
    }

    @Test
    @DisplayName("Deve salvar transação com sucesso")
    void save_ShouldSaveTransacao() {
        Transacao savedTransacao = transacaoRepository.save(transacaoSalario);

        assertNotNull(savedTransacao.getId());
        assertEquals("Salário Fevereiro", savedTransacao.getDescricao());
        assertEquals(new BigDecimal("5000.00"), savedTransacao.getValor());
        assertEquals(TipoTransacao.RECEITA, savedTransacao.getTipo());
    }

    @Test
    @DisplayName("Deve buscar transação por ID quando existe")
    void findById_ShouldReturnTransacao_WhenExists() {
        Transacao savedTransacao = transacaoRepository.save(transacaoSalario);

        Optional<Transacao> resultado = transacaoRepository.findById(savedTransacao.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Salário Fevereiro", resultado.get().getDescricao());
    }

    @Test
    @DisplayName("Deve retornar vazio quando transação não existe")
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<Transacao> resultado = transacaoRepository.findById(999L);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve buscar todas as transações")
    void findAll_ShouldReturnAllTransacoes() {
        transacaoRepository.save(transacaoSalario);
        transacaoRepository.save(transacaoAlimentacao);
        transacaoRepository.save(transacaoStreaming);

        List<Transacao> resultado = transacaoRepository.findAll();

        assertEquals(3, resultado.size());
    }

    @Test
    @DisplayName("Deve buscar transações por tipo")
    void findByTipo_ShouldReturnTransacoesByTipo() {
        transacaoRepository.save(transacaoSalario);
        transacaoRepository.save(transacaoAlimentacao);

        List<Transacao> receitas = transacaoRepository.findByTipo(TipoTransacao.RECEITA);
        List<Transacao> despesas = transacaoRepository.findByTipo(TipoTransacao.DESPESA);

        assertEquals(1, receitas.size());
        assertEquals(TipoTransacao.RECEITA, receitas.get(0).getTipo());
        assertEquals(1, despesas.size());
        assertEquals(TipoTransacao.DESPESA, despesas.get(0).getTipo());
    }

    @Test
    @DisplayName("Deve buscar transações por recorrente")
    void findByRecorrente_ShouldReturnTransacoesByRecorrente() {
        transacaoRepository.save(transacaoAlimentacao);
        transacaoRepository.save(transacaoStreaming);
        transacaoRepository.save(transacaoParcelada);

        List<Transacao> recorrentes = transacaoRepository.findByRecorrente(true);
        List<Transacao> naoRecorrentes = transacaoRepository.findByRecorrente(false);

        assertEquals(2, recorrentes.size());
        recorrentes.forEach(transacao -> assertTrue(transacao.getRecorrente()));
        assertEquals(1, naoRecorrentes.size());
        assertFalse(naoRecorrentes.get(0).getRecorrente());
    }

    @Test
    @DisplayName("Deve buscar transações por categoria")
    void findByCategoriaId_ShouldReturnTransacoesByCategoria() {
        transacaoRepository.save(transacaoAlimentacao);
        transacaoRepository.save(transacaoParcelada);

        List<Transacao> resultado = transacaoRepository.findByCategoriaId(categoriaAlimentacao.getId());

        assertEquals(2, resultado.size());
        resultado.forEach(transacao -> 
                assertEquals(categoriaAlimentacao.getId(), transacao.getCategoria().getId()));
    }

    @Test
    @DisplayName("Deve buscar transações por período")
    void findByDataBetween_ShouldReturnTransacoesByPeriodo() {
        transacaoRepository.save(transacaoSalario);
        transacaoRepository.save(transacaoAlimentacao);
        transacaoRepository.save(transacaoParcelada);

        LocalDate dataInicio = LocalDate.of(2026, 2, 1);
        LocalDate dataFim = LocalDate.of(2026, 2, 28);

        List<Transacao> resultado = transacaoRepository.findByDataBetween(dataInicio, dataFim);

        assertEquals(3, resultado.size());
    }

    @Test
    @DisplayName("Deve buscar transações por tipo e período")
    void findByTipoAndDataBetween_ShouldReturnTransacoesByTipoAndPeriodo() {
        transacaoRepository.save(transacaoSalario);
        transacaoRepository.save(transacaoAlimentacao);

        LocalDate dataInicio = LocalDate.of(2026, 2, 1);
        LocalDate dataFim = LocalDate.of(2026, 2, 28);

        List<Transacao> receitas = transacaoRepository.findByTipoAndDataBetween(
                TipoTransacao.RECEITA, dataInicio, dataFim);
        List<Transacao> despesas = transacaoRepository.findByTipoAndDataBetween(
                TipoTransacao.DESPESA, dataInicio, dataFim);

        assertEquals(1, receitas.size());
        assertEquals(TipoTransacao.RECEITA, receitas.get(0).getTipo());
        assertEquals(1, despesas.size());
        assertEquals(TipoTransacao.DESPESA, despesas.get(0).getTipo());
    }

    @Test
    @DisplayName("Deve buscar gastos recorrentes")
    void findGastosRecorrentes_ShouldReturnGastosRecorrentes() {
        transacaoRepository.save(transacaoAlimentacao);
        transacaoRepository.save(transacaoStreaming);

        List<Transacao> resultado = transacaoRepository.findGastosRecorrentes();

        assertEquals(2, resultado.size());
        resultado.forEach(transacao -> {
            assertEquals(TipoTransacao.DESPESA, transacao.getTipo());
            assertTrue(transacao.getRecorrente());
        });
    }

    @Test
    @DisplayName("Deve buscar gastos supérfluos")
    void findGastosSuperfluos_ShouldReturnGastosSuperfluos() {
        transacaoRepository.save(transacaoStreaming);

        List<Transacao> resultado = transacaoRepository.findGastosSuperfluos();

        assertEquals(1, resultado.size());
        assertFalse(resultado.get(0).getCategoria().getEssencial());
    }

    @Test
    @DisplayName("Deve buscar despesas parceladas")
    void findDespesasParceladas_ShouldReturnDespesasParceladas() {
        transacaoRepository.save(transacaoParcelada);

        List<Transacao> resultado = transacaoRepository.findDespesasParceladas();

        assertEquals(1, resultado.size());
        assertEquals(12, resultado.get(0).getParcelas());
    }

    @Test
    @DisplayName("Deve somar transações por tipo e período")
    void sumByTipoAndPeriodo_ShouldReturnSumByTipoAndPeriodo() {
        transacaoRepository.save(transacaoSalario);
        transacaoRepository.save(transacaoAlimentacao);

        LocalDate dataInicio = LocalDate.of(2026, 2, 1);
        LocalDate dataFim = LocalDate.of(2026, 2, 28);

        BigDecimal totalReceitas = transacaoRepository.sumByTipoAndPeriodo(
                TipoTransacao.RECEITA, dataInicio, dataFim);
        BigDecimal totalDespesas = transacaoRepository.sumByTipoAndPeriodo(
                TipoTransacao.DESPESA, dataInicio, dataFim);

        assertEquals(new BigDecimal("5000.00"), totalReceitas);
        assertEquals(new BigDecimal("400.00"), totalDespesas);
    }

    @Test
    @DisplayName("Deve somar transações por tipo e recorrente")
    void sumByTipoAndRecorrente_ShouldReturnSumByTipoAndRecorrente() {
        transacaoRepository.save(transacaoAlimentacao);
        transacaoRepository.save(transacaoStreaming);

        BigDecimal totalRecorrentes = transacaoRepository.sumByTipoAndRecorrente(TipoTransacao.DESPESA);

        assertEquals(new BigDecimal("439.90"), totalRecorrentes);
    }

    @Test
    @DisplayName("Deve somar transações por essencialidade e tipo")
    void sumByEssencialAndTipo_ShouldReturnSumByEssencialAndTipo() {
        transacaoRepository.save(transacaoAlimentacao);
        transacaoRepository.save(transacaoStreaming);

        BigDecimal totalEssenciais = transacaoRepository.sumByEssencialAndTipo(true, TipoTransacao.DESPESA);
        BigDecimal totalSuperfluos = transacaoRepository.sumByEssencialAndTipo(false, TipoTransacao.DESPESA);

        assertEquals(new BigDecimal("400.00"), totalEssenciais);
        assertEquals(new BigDecimal("39.90"), totalSuperfluos);
    }

    @Test
    @DisplayName("Deve buscar totais por categoria")
    void findTotalPorCategoria_ShouldReturnTotalsByCategory() {
        transacaoRepository.save(transacaoAlimentacao);
        transacaoRepository.save(transacaoParcelada);

        List<Object[]> resultado = transacaoRepository.findTotalPorCategoria(TipoTransacao.DESPESA);

        assertEquals(1, resultado.size());
        assertEquals("Alimentação", resultado.get(0)[0]);
        assertEquals(new BigDecimal("4000.00"), resultado.get(0)[1]);
    }

    @Test
    @DisplayName("Deve excluir transação por ID")
    void deleteById_ShouldDeleteTransacao() {
        Transacao savedTransacao = transacaoRepository.save(transacaoSalario);
        Long id = savedTransacao.getId();

        transacaoRepository.deleteById(id);

        Optional<Transacao> resultado = transacaoRepository.findById(id);
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve atualizar transação com sucesso")
    void update_ShouldUpdateTransacao() {
        Transacao savedTransacao = transacaoRepository.save(transacaoSalario);
        savedTransacao.setDescricao("Salário Atualizado");
        savedTransacao.setValor(new BigDecimal("5500.00"));

        Transacao updatedTransacao = transacaoRepository.save(savedTransacao);

        assertEquals("Salário Atualizado", updatedTransacao.getDescricao());
        assertEquals(new BigDecimal("5500.00"), updatedTransacao.getValor());
        assertEquals(savedTransacao.getId(), updatedTransacao.getId());
    }

    @Test
    @DisplayName("Deve calcular valor mensal de transação parcelada")
    void getValorMensal_ShouldCalculateMonthlyValue() {
        Transacao savedTransacao = transacaoRepository.save(transacaoParcelada);

        BigDecimal valorMensal = savedTransacao.getValorMensal();

        assertEquals(new BigDecimal("300.00"), valorMensal);
    }

    @Test
    @DisplayName("Deve retornar valor total quando não é parcelado")
    void getValorMensal_ShouldReturnTotalValue_WhenNotParcelated() {
        Transacao savedTransacao = transacaoRepository.save(transacaoAlimentacao);

        BigDecimal valorMensal = savedTransacao.getValorMensal();

        assertEquals(new BigDecimal("400.00"), valorMensal);
    }
}
