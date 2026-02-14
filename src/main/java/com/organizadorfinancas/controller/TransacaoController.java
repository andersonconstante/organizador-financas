package com.organizadorfinancas.controller;

import com.organizadorfinancas.model.Transacao;
import com.organizadorfinancas.model.TipoTransacao;
import com.organizadorfinancas.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacoes")
@Tag(name = "Transações", description = "API para gerenciamento de transações financeiras")
public class TransacaoController {
    
    @Autowired
    private TransacaoService transacaoService;
    
    @GetMapping
    @Operation(summary = "Listar todas as transações", description = "Retorna uma lista de todas as transações cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de transações retornada com sucesso")
    public ResponseEntity<List<Transacao>> findAll() {
        List<Transacao> transacoes = transacaoService.findAll();
        return ResponseEntity.ok(transacoes);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar transação por ID", description = "Retorna uma transação específica pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transação encontrada"),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    })
    public ResponseEntity<Transacao> findById(@Parameter(description = "ID da transação") @PathVariable Long id) {
        Optional<Transacao> transacao = transacaoService.findById(id);
        return transacao.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Criar nova transação", description = "Cria uma nova transação no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<Transacao> create(@Valid @RequestBody Transacao transacao) {
        Transacao savedTransacao = transacaoService.save(transacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransacao);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar transação", description = "Atualiza os dados de uma transação existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transação atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<Transacao> update(@Parameter(description = "ID da transação") @PathVariable Long id, 
                                           @Valid @RequestBody Transacao transacao) {
        if (!transacaoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        transacao.setId(id);
        Transacao updatedTransacao = transacaoService.save(transacao);
        return ResponseEntity.ok(updatedTransacao);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir transação", description = "Exclui uma transação do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Transação excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    })
    public ResponseEntity<Void> deleteById(@Parameter(description = "ID da transação") @PathVariable Long id) {
        if (!transacaoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        transacaoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/despesas")
    @Operation(summary = "Listar todas as despesas", description = "Retorna todas as transações do tipo despesa")
    public ResponseEntity<List<Transacao>> findAllDespesas() {
        return ResponseEntity.ok(transacaoService.findAllDespesas());
    }
    
    @GetMapping("/receitas")
    @Operation(summary = "Listar todas as receitas", description = "Retorna todas as transações do tipo receita")
    public ResponseEntity<List<Transacao>> findAllReceitas() {
        return ResponseEntity.ok(transacaoService.findAllReceitas());
    }
    
    @GetMapping("/recorrentes")
    @Operation(summary = "Listar gastos recorrentes", description = "Retorna todas as despesas recorrentes")
    public ResponseEntity<List<Transacao>> findGastosRecorrentes() {
        return ResponseEntity.ok(transacaoService.findGastosRecorrentes());
    }
    
    @GetMapping("/superfluos")
    @Operation(summary = "Listar gastos supérfluos", description = "Retorna todas as despesas supérfluas")
    public ResponseEntity<List<Transacao>> findGastosSuperfluos() {
        return ResponseEntity.ok(transacaoService.findGastosSuperfluos());
    }
    
    @GetMapping("/parceladas")
    @Operation(summary = "Listar despesas parceladas", description = "Retorna todas as despesas parceladas")
    public ResponseEntity<List<Transacao>> findDespesasParceladas() {
        return ResponseEntity.ok(transacaoService.findDespesasParceladas());
    }
    
    @GetMapping("/periodo")
    @Operation(summary = "Buscar transações por período", description = "Retorna transações filtradas por período de datas")
    public ResponseEntity<List<Transacao>> findByPeriodo(
            @Parameter(description = "Data inicial") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data final") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<Transacao> transacoes = transacaoService.findByPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(transacoes);
    }
    
    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Buscar transações por categoria", description = "Retorna transações filtradas por categoria")
    public ResponseEntity<List<Transacao>> findByCategoria(@Parameter(description = "ID da categoria") @PathVariable Long categoriaId) {
        List<Transacao> transacoes = transacaoService.findByCategoriaId(categoriaId);
        return ResponseEntity.ok(transacoes);
    }
    
    @GetMapping("/resumo/despesas")
    @Operation(summary = "Total de despesas do mês", description = "Retorna o valor total de despesas do mês atual")
    public ResponseEntity<BigDecimal> getTotalDespesas() {
        return ResponseEntity.ok(transacaoService.getTotalDespesas());
    }
    
    @GetMapping("/resumo/receitas")
    @Operation(summary = "Total de receitas do mês", description = "Retorna o valor total de receitas do mês atual")
    public ResponseEntity<BigDecimal> getTotalReceitas() {
        return ResponseEntity.ok(transacaoService.getTotalReceitas());
    }
    
    @GetMapping("/resumo/recorrentes")
    @Operation(summary = "Total de gastos recorrentes", description = "Retorna o valor total de gastos recorrentes")
    public ResponseEntity<BigDecimal> getTotalGastosRecorrentes() {
        return ResponseEntity.ok(transacaoService.getTotalGastosRecorrentes());
    }
    
    @GetMapping("/resumo/essenciais")
    @Operation(summary = "Total de despesas essenciais", description = "Retorna o valor total de despesas essenciais")
    public ResponseEntity<BigDecimal> getTotalDespesasEssenciais() {
        return ResponseEntity.ok(transacaoService.getTotalDespesasEssenciais());
    }
    
    @GetMapping("/resumo/superfluas")
    @Operation(summary = "Total de despesas supérfluas", description = "Retorna o valor total de despesas supérfluas")
    public ResponseEntity<BigDecimal> getTotalDespesasSuperfluas() {
        return ResponseEntity.ok(transacaoService.getTotalDespesasSuperfluas());
    }
    
    @GetMapping("/resumo/saldo")
    @Operation(summary = "Saldo mensal", description = "Retorna o saldo mensal (receitas - despesas)")
    public ResponseEntity<BigDecimal> getSaldoMensal() {
        return ResponseEntity.ok(transacaoService.getSaldoMensal());
    }
    
    @GetMapping("/resumo/por-categoria")
    @Operation(summary = "Totais por categoria", description = "Retorna o total agrupado por categoria")
    public ResponseEntity<List<Object[]>> getTotaisPorCategoria(
            @Parameter(description = "Tipo da transação") @RequestParam TipoTransacao tipo) {
        return ResponseEntity.ok(transacaoService.getTotaisPorCategoria(tipo));
    }
}
