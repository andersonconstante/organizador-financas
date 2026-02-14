package com.organizadorfinancas.model;

public enum TipoCategoria {
    RENDA_FIXA("Renda Fixa"),
    RENDA_VARIAVEL("Renda Variável"),
    DESPESA_ESSENCIAL("Despesa Essencial"),
    DESPESA_SUPERFLUA("Despesa Supérflua"),
    GASTO_INVISIVEL("Gasto Invisível"),
    INVESTIMENTO("Investimento");
    
    private final String descricao;
    
    TipoCategoria(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
