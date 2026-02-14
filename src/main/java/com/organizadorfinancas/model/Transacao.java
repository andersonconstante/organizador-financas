package com.organizadorfinancas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transacoes")
public class Transacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Descrição é obrigatória")
    @Column(nullable = false)
    private String descricao;
    
    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @NotNull(message = "Data é obrigatória")
    @Column(nullable = false)
    private LocalDate data;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;
    
    @Column(nullable = false)
    private Boolean recorrente;
    
    @Column(nullable = false)
    private Integer parcelas;
    
    @Column(nullable = false)
    private Integer parcelaAtual;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    @JsonIgnoreProperties({"transacoes"})
    private Categoria categoria;
    
    @Column(length = 500)
    private String observacoes;
    
    public Transacao() {}
    
    public Transacao(String descricao, BigDecimal valor, LocalDate data, 
                    TipoTransacao tipo, Boolean recorrente, Categoria categoria) {
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.tipo = tipo;
        this.recorrente = recorrente;
        this.categoria = categoria;
        this.parcelas = 1;
        this.parcelaAtual = 1;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public BigDecimal getValor() {
        return valor;
    }
    
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    public LocalDate getData() {
        return data;
    }
    
    public void setData(LocalDate data) {
        this.data = data;
    }
    
    public TipoTransacao getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }
    
    public Boolean getRecorrente() {
        return recorrente;
    }
    
    public void setRecorrente(Boolean recorrente) {
        this.recorrente = recorrente;
    }
    
    public Integer getParcelas() {
        return parcelas;
    }
    
    public void setParcelas(Integer parcelas) {
        this.parcelas = parcelas;
    }
    
    public Integer getParcelaAtual() {
        return parcelaAtual;
    }
    
    public void setParcelaAtual(Integer parcelaAtual) {
        this.parcelaAtual = parcelaAtual;
    }
    
    public Categoria getCategoria() {
        return categoria;
    }
    
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public BigDecimal getValorMensal() {
        if (parcelas > 1) {
            return valor.divide(BigDecimal.valueOf(parcelas), 2, java.math.RoundingMode.HALF_UP);
        }
        return valor;
    }
}
