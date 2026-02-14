package com.organizadorfinancas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "categorias")
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome da categoria é obrigatório")
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private Boolean essencial;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCategoria tipo;
    
    @JsonIgnore
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transacao> transacoes;
    
    public Categoria() {}
    
    public Categoria(String nome, Boolean essencial, TipoCategoria tipo) {
        this.nome = nome;
        this.essencial = essencial;
        this.tipo = tipo;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public Boolean getEssencial() {
        return essencial;
    }
    
    public void setEssencial(Boolean essencial) {
        this.essencial = essencial;
    }
    
    public TipoCategoria getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoCategoria tipo) {
        this.tipo = tipo;
    }
    
    public List<Transacao> getTransacoes() {
        return transacoes;
    }
    
    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
    }
}
