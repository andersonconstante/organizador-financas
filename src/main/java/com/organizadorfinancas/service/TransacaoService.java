package com.organizadorfinancas.service;

import com.organizadorfinancas.model.Transacao;
import com.organizadorfinancas.model.TipoTransacao;
import com.organizadorfinancas.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoService {
    
    @Autowired
    private TransacaoRepository transacaoRepository;
    
    public List<Transacao> findAll() {
        return transacaoRepository.findAll();
    }
    
    public Optional<Transacao> findById(Long id) {
        return transacaoRepository.findById(id);
    }
    
    public Transacao save(Transacao transacao) {
        return transacaoRepository.save(transacao);
    }
    
    public void deleteById(Long id) {
        transacaoRepository.deleteById(id);
    }
    
    public List<Transacao> findByTipo(TipoTransacao tipo) {
        return transacaoRepository.findByTipo(tipo);
    }
    
    public List<Transacao> findAllDespesas() {
        return transacaoRepository.findByTipo(TipoTransacao.DESPESA);
    }
    
    public List<Transacao> findAllReceitas() {
        return transacaoRepository.findByTipo(TipoTransacao.RECEITA);
    }
    
    public List<Transacao> findGastosRecorrentes() {
        return transacaoRepository.findGastosRecorrentes();
    }
    
    public List<Transacao> findGastosSuperfluos() {
        return transacaoRepository.findGastosSuperfluos();
    }
    
    public List<Transacao> findDespesasParceladas() {
        return transacaoRepository.findDespesasParceladas();
    }
    
    public List<Transacao> findByPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return transacaoRepository.findByDataBetween(dataInicio, dataFim);
    }
    
    public List<Transacao> findByCategoriaId(Long categoriaId) {
        return transacaoRepository.findByCategoriaId(categoriaId);
    }
    
    public BigDecimal getTotalDespesas() {
        return transacaoRepository.sumByTipoAndPeriodo(
            TipoTransacao.DESPESA, 
            LocalDate.now().withDayOfMonth(1), 
            LocalDate.now()
        );
    }
    
    public BigDecimal getTotalReceitas() {
        return transacaoRepository.sumByTipoAndPeriodo(
            TipoTransacao.RECEITA, 
            LocalDate.now().withDayOfMonth(1), 
            LocalDate.now()
        );
    }
    
    public BigDecimal getTotalGastosRecorrentes() {
        return transacaoRepository.sumByTipoAndRecorrente(TipoTransacao.DESPESA);
    }
    
    public BigDecimal getTotalDespesasEssenciais() {
        return transacaoRepository.sumByEssencialAndTipo(true, TipoTransacao.DESPESA);
    }
    
    public BigDecimal getTotalDespesasSuperfluas() {
        return transacaoRepository.sumByEssencialAndTipo(false, TipoTransacao.DESPESA);
    }
    
    public List<Object[]> getTotaisPorCategoria(TipoTransacao tipo) {
        return transacaoRepository.findTotalPorCategoria(tipo);
    }
    
    public BigDecimal getSaldoMensal() {
        BigDecimal receitas = getTotalReceitas();
        BigDecimal despesas = getTotalDespesas();
        return receitas.subtract(despesas);
    }
}
