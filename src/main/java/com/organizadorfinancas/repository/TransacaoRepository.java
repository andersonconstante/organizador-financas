package com.organizadorfinancas.repository;

import com.organizadorfinancas.model.Transacao;
import com.organizadorfinancas.model.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    
    List<Transacao> findByTipo(TipoTransacao tipo);
    
    List<Transacao> findByRecorrente(Boolean recorrente);
    
    List<Transacao> findByCategoriaId(Long categoriaId);
    
    List<Transacao> findByDataBetween(LocalDate dataInicio, LocalDate dataFim);
    
    List<Transacao> findByTipoAndDataBetween(TipoTransacao tipo, LocalDate dataInicio, LocalDate dataFim);
    
    @Query("SELECT t FROM Transacao t WHERE t.recorrente = true ORDER BY t.data DESC")
    List<Transacao> findGastosRecorrentes();
    
    @Query("SELECT t FROM Transacao t WHERE t.categoria.essencial = false ORDER BY t.valor DESC")
    List<Transacao> findGastosSuperfluos();
    
    @Query("SELECT t FROM Transacao t WHERE t.parcelas > 1 ORDER BY t.data DESC")
    List<Transacao> findDespesasParceladas();
    
    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.tipo = :tipo AND t.data BETWEEN :dataInicio AND :dataFim")
    BigDecimal sumByTipoAndPeriodo(@Param("tipo") TipoTransacao tipo, 
                                   @Param("dataInicio") LocalDate dataInicio, 
                                   @Param("dataFim") LocalDate dataFim);
    
    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.tipo = :tipo AND t.recorrente = true")
    BigDecimal sumByTipoAndRecorrente(@Param("tipo") TipoTransacao tipo);
    
    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.categoria.essencial = :essencial AND t.tipo = :tipo")
    BigDecimal sumByEssencialAndTipo(@Param("essencial") Boolean essencial, 
                                     @Param("tipo") TipoTransacao tipo);
    
    @Query("SELECT t.categoria.nome, SUM(t.valor) FROM Transacao t WHERE t.tipo = :tipo GROUP BY t.categoria.nome ORDER BY SUM(t.valor) DESC")
    List<Object[]> findTotalPorCategoria(@Param("tipo") TipoTransacao tipo);
}
