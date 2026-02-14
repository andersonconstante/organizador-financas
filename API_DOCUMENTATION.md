# Documentação da API - Organizador de Finanças

## 📋 Visão Geral

API REST para gerenciamento de finanças pessoais desenvolvida com Spring Boot 3 e Java 17. Esta documentação foi criada com base nos testes unitários para garantir comunicação segura e sem bugs com frontend React.

**URL Base**: `http://localhost:8080/api`
**Content-Type**: `application/json`
**Autenticação**: Desabilitada para MVP (acesso público)

---

## 🔐 Considerações Importantes para Frontend

### ✅ **Validações Testadas**
- Todos os endpoints foram testados unitariamente
- Validações de campos obrigatórios implementadas
- Tratamento de erros HTTP padronizado
- Formatos de resposta consistentes

### 🚫 **Casos de Erro Conhecidos**
- **400**: Requisição inválida (campos obrigatórios faltando)
- **404**: Recurso não encontrado (ID inexistente)
- **403**: Acesso negado (se segurança ativada)
- **500**: Erro interno do servidor

### 📝 **Formatação de Dados**
- **Datas**: `YYYY-MM-DD` (ISO 8601)
- **Valores**: Decimal com 2 casas (ex: `1500.00`)
- **Boolean**: `true`/`false`
- **Enums**: String em MAIÚSCULAS (ex: `RECEITA`)

---

## 📂 Categorias

### 🏷️ **Modelo de Dados**

```typescript
interface Categoria {
  id: number;
  nome: string;
  essencial: boolean;
  tipo: TipoCategoria;
}

enum TipoCategoria {
  RENDA_FIXA = "RENDA_FIXA",
  RENDA_VARIAVEL = "RENDA_VARIAVEL", 
  DESPESA_ESSENCIAL = "DESPESA_ESSENCIAL",
  DESPESA_SUPERFLUA = "DESPESA_SUPERFLUA",
  GASTO_INVISIVEL = "GASTO_INVISIVEL",
  INVESTIMENTO = "INVESTIMENTO"
}
```

### 📡 **Endpoints**

#### **Listar Todas**
```http
GET /api/categorias
```

**Resposta (200)**:
```json
[
  {
    "id": 1,
    "nome": "Salário",
    "essencial": true,
    "tipo": "RENDA_FIXA"
  }
]
```

#### **Buscar por ID**
```http
GET /api/categorias/{id}
```

**Resposta (200)**:
```json
{
  "id": 1,
  "nome": "Salário", 
  "essencial": true,
  "tipo": "RENDA_FIXA"
}
```

**Resposta (404)**:
```json
{
  "timestamp": "2026-02-13T22:15:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Categoria não encontrada"
}
```

#### **Criar Nova**
```http
POST /api/categorias
```

**Corpo da Requisição**:
```json
{
  "nome": "Transporte",
  "essencial": true,
  "tipo": "DESPESA_ESSENCIAL"
}
```

**Validações Testadas**:
- `nome`: obrigatório, não pode ser duplicado
- `essencial`: obrigatório (boolean)
- `tipo`: obrigatório, deve ser um valor válido do enum

**Resposta (201)**:
```json
{
  "id": 4,
  "nome": "Transporte",
  "essencial": true,
  "tipo": "DESPESA_ESSENCIAL"
}
```

**Resposta (400)** - Nome duplicado:
```json
{
  "timestamp": "2026-02-13T22:15:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Categoria com nome 'Transporte' já existe"
}
```

#### **Atualizar**
```http
PUT /api/categorias/{id}
```

**Corpo da Requisição**: Mesmo modelo da criação

**Resposta (200)**: Categoria atualizada
**Resposta (404)**: Categoria não encontrada
**Resposta (400)**: Dados inválidos

#### **Excluir**
```http
DELETE /api/categorias/{id}
```

**Resposta (204)**: Sem conteúdo (excluído com sucesso)
**Resposta (404)**: Categoria não encontrada

### 🔍 **Endpoints Especializados**

#### **Renda Fixa**
```http
GET /api/categorias/renda-fixa
```

#### **Renda Variável**
```http
GET /api/categorias/renda-variavel
```

#### **Despesas Essenciais**
```http
GET /api/categorias/despesas-essenciais
```

#### **Despesas Supérfluas**
```http
GET /api/categorias/despesas-superfluas
```

#### **Gastos Invisíveis**
```http
GET /api/categorias/gastos-invisiveis
```

#### **Por Tipo**
```http
GET /api/categorias/tipo/{tipo}
```

#### **Por Essencialidade**
```http
GET /api/categorias/essencial/{essencial}
```

---

## 💰 Transações

### 🏷️ **Modelo de Dados**

```typescript
interface Transacao {
  id: number;
  descricao: string;
  valor: string;        // Decimal com 2 casas
  data: string;         // YYYY-MM-DD
  tipo: TipoTransacao;
  recorrente: boolean;
  parcelas: number;
  parcelaAtual: number;
  categoria: Categoria;
  observacoes?: string;  // Opcional
  valorMensal?: string; // Calculado automaticamente
}

enum TipoTransacao {
  RECEITA = "RECEITA",
  DESPESA = "DESPESA"
}
```

### 📡 **Endpoints**

#### **Listar Todas**
```http
GET /api/transacoes
```

#### **Buscar por ID**
```http
GET /api/transacoes/{id}
```

#### **Criar Nova**
```http
POST /api/transacoes
```

**Corpo da Requisição**:
```json
{
  "descricao": "Salário Fevereiro",
  "valor": "5000.00",
  "data": "2026-02-05",
  "tipo": "RECEITA",
  "recorrente": false,
  "parcelas": 1,
  "parcelaAtual": 1,
  "categoria": {
    "id": 1
  },
  "observacoes": "Salário mensal"
}
```

**Validações Testadas**:
- `descricao`: obrigatório, não pode ser vazia
- `valor`: obrigatório, deve ser positivo
- `data`: obrigatória, formato válido
- `tipo`: obrigatório, enum válido
- `categoria.id`: obrigatório, deve existir
- `parcelas`, `parcelaAtual`: obrigatórios, >= 1

**Resposta (201)**: Transação criada com ID
**Resposta (400)**: Dados inválidos

#### **Atualizar**
```http
PUT /api/transacoes/{id}
```

#### **Excluir**
```http
DELETE /api/transacoes/{id}
```

### 🔍 **Endpoints Especializados**

#### **Despesas**
```http
GET /api/transacoes/despesas
```

#### **Receitas**
```http
GET /api/transacoes/receitas
```

#### **Gastos Recorrentes**
```http
GET /api/transacoes/recorrentes
```

#### **Gastos Supérfluos**
```http
GET /api/transacoes/superfluos
```

#### **Despesas Parceladas**
```http
GET /api/transacoes/parceladas
```

#### **Por Período**
```http
GET /api/transacoes/periodo?dataInicio=2026-02-01&dataFim=2026-02-28
```

#### **Por Categoria**
```http
GET /api/transacoes/categoria/{categoriaId}
```

---

## 📊 Resumos Financeiros

### 💵 **Endpoints de Resumo**

#### **Total Despesas Mês**
```http
GET /api/transacoes/resumo/despesas
```

**Resposta (200)**:
```json
"1500.00"
```

#### **Total Receitas Mês**
```http
GET /api/transacoes/resumo/receitas
```

#### **Saldo Mensal**
```http
GET /api/transacoes/resumo/saldo
```

#### **Total Gastos Recorrentes**
```http
GET /api/transacoes/resumo/recorrentes
```

#### **Total Despesas Essenciais**
```http
GET /api/transacoes/resumo/essenciais
```

#### **Total Despesas Supérfluas**
```http
GET /api/transacoes/resumo/superfluas
```

#### **Totais por Categoria**
```http
GET /api/transacoes/resumo/por-categoria?tipo=DESPESA
```

**Resposta (200)**:
```json
[
  ["Alimentação", "400.00"],
  ["Transporte", "200.00"],
  ["Streaming", "39.90"]
]
```

---

## 🚨 **Guia de Implementação React**

### 📦 **Configuração Axios**

```typescript
// src/services/api.ts
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Tratamento global de erros
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 404) {
      // Tratar recurso não encontrado
      console.error('Recurso não encontrado');
    } else if (error.response?.status === 400) {
      // Tratar dados inválidos
      console.error('Dados inválidos:', error.response.data);
    }
    return Promise.reject(error);
  }
);

export default api;
```

### 🎯 **Exemplo de Service**

```typescript
// src/services/categoriaService.ts
import api from './api';

export const categoriaService = {
  listar: async () => {
    const response = await api.get('/categorias');
    return response.data;
  },

  buscarPorId: async (id: number) => {
    const response = await api.get(`/categorias/${id}`);
    return response.data;
  },

  criar: async (categoria: any) => {
    const response = await api.post('/categorias', categoria);
    return response.data;
  },

  atualizar: async (id: number, categoria: any) => {
    const response = await api.put(`/categorias/${id}`, categoria);
    return response.data;
  },

  excluir: async (id: number) => {
    await api.delete(`/categorias/${id}`);
  },

  listarRendaFixa: async () => {
    const response = await api.get('/categorias/renda-fixa');
    return response.data;
  },

  listarDespesasEssenciais: async () => {
    const response = await api.get('/categorias/despesas-essenciais');
    return response.data;
  }
};
```

### 🎨 **Exemplo de Componente React**

```typescript
// src/components/CategoriaForm.tsx
import React, { useState } from 'react';
import { categoriaService } from '../services/categoriaService';

interface CategoriaFormProps {
  categoria?: any;
  onSave: (categoria: any) => void;
  onCancel: () => void;
}

export const CategoriaForm: React.FC<CategoriaFormProps> = ({ 
  categoria, 
  onSave, 
  onCancel 
}) => {
  const [formData, setFormData] = useState({
    nome: categoria?.nome || '',
    essencial: categoria?.essencial ?? true,
    tipo: categoria?.tipo || 'DESPESA_ESSENCIAL'
  });

  const [errors, setErrors] = useState<string[]>([]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    try {
      if (categoria?.id) {
        await categoriaService.atualizar(categoria.id, formData);
      } else {
        await categoriaService.criar(formData);
      }
      onSave(formData);
    } catch (error: any) {
      if (error.response?.status === 400) {
        setErrors(['Dados inválidos. Verifique os campos obrigatórios.']);
      } else {
        setErrors(['Erro ao salvar categoria. Tente novamente.']);
      }
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {errors.map((error, index) => (
        <div key={index} className="error">{error}</div>
      ))}
      
      <div>
        <label>Nome*</label>
        <input
          type="text"
          value={formData.nome}
          onChange={(e) => setFormData({...formData, nome: e.target.value})}
          required
        />
      </div>

      <div>
        <label>Tipo*</label>
        <select
          value={formData.tipo}
          onChange={(e) => setFormData({...formData, tipo: e.target.value})}
          required
        >
          <option value="RENDA_FIXA">Renda Fixa</option>
          <option value="RENDA_VARIAVEL">Renda Variável</option>
          <option value="DESPESA_ESSENCIAL">Despesa Essencial</option>
          <option value="DESPESA_SUPERFLUA">Despesa Supérflua</option>
          <option value="GASTO_INVISIVEL">Gasto Invisível</option>
        </select>
      </div>

      <div>
        <label>
          <input
            type="checkbox"
            checked={formData.essencial}
            onChange={(e) => setFormData({...formData, essencial: e.target.checked})}
          />
          Essencial
        </label>
      </div>

      <button type="submit">
        {categoria?.id ? 'Atualizar' : 'Criar'}
      </button>
      <button type="button" onClick={onCancel}>
        Cancelar
      </button>
    </form>
  );
};
```

### ⚡ **Boas Práticas para Frontend**

#### **1. Validação Client-Side**
```typescript
// Validar antes de enviar para API
const validarCategoria = (categoria: any): string[] => {
  const errors: string[] = [];
  
  if (!categoria.nome?.trim()) {
    errors.push('Nome é obrigatório');
  }
  
  if (!categoria.tipo) {
    errors.push('Tipo é obrigatório');
  }
  
  return errors;
};
```

#### **2. Tratamento de Estados**
```typescript
const [loading, setLoading] = useState(false);
const [error, setError] = useState<string | null>(null);

const carregarCategorias = async () => {
  setLoading(true);
  setError(null);
  
  try {
    const categorias = await categoriaService.listar();
    setCategorias(categorias);
  } catch (err) {
    setError('Erro ao carregar categorias');
  } finally {
    setLoading(false);
  }
};
```

#### **3. Formatação de Valores**
```typescript
const formatarMoeda = (valor: string): string => {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(parseFloat(valor));
};

const formatarData = (data: string): string => {
  return new Date(data).toLocaleDateString('pt-BR');
};
```

---

## 🧪 **Testes de Integração Sugeridos**

### 📋 **Testes E2E para React**

```typescript
// cypress/e2e/categoria.cy.ts
describe('Categorias', () => {
  it('deve criar nova categoria', () => {
    cy.visit('/categorias/nova');
    
    cy.get('[data-testid="nome-input"]').type('Transporte');
    cy.get('[data-testid="tipo-select"]').select('DESPESA_ESSENCIAL');
    cy.get('[data-testid="essencial-checkbox"]').check();
    cy.get('[data-testid="salvar-button"]').click();
    
    cy.url().should('include', '/categorias');
    cy.contains('Transporte').should('be.visible');
  });

  it('deve validar campos obrigatórios', () => {
    cy.visit('/categorias/nova');
    cy.get('[data-testid="salvar-button"]').click();
    
    cy.get('[data-testid="error-nome"]').should('be.visible');
    cy.get('[data-testid="error-tipo"]').should('be.visible');
  });
});
```

---

## 🔧 **Ambiente de Desenvolvimento**

### 🌐 **URLs de Desenvolvimento**
- **API**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Console H2**: `http://localhost:8080/h2-console`

### 📝 **Variáveis de Ambiente**
```bash
# .env.local
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_ENABLE_MOCK=false
```

---

## 📞 **Suporte e Debug**

### 🐛 **Problemas Comuns**

#### **1. CORS**
Se encontrar erro de CORS, verifique:
- Backend está rodando na porta 8080
- Configuração de segurança permite requisições do frontend

#### **2. Formato de Data**
Sempre use formato `YYYY-MM-DD` para datas:
```typescript
const dataFormatada = new Date().toISOString().split('T')[0];
```

#### **3. Valores Decimais**
Envie valores como string com 2 casas decimais:
```typescript
const valor = "1500.00"; // ✅ Correto
const valor = 1500.00;     // ❌ Pode causar problemas
```

### 📊 **Monitoramento**
Implemente logging para debug:
```typescript
api.interceptors.request.use((request) => {
  console.log('Request:', request);
  return request;
});

api.interceptors.response.use((response) => {
  console.log('Response:', response);
  return response;
});
```

---

## 📚 **Recursos Adicionais**

- **Documentação Interativa**: http://localhost:8080/swagger-ui.html
- **Testes Unitários**: Verificar `src/test/` para exemplos
- **Banco de Testes**: Acessar console H2 para dados exemplo

---

**Última Atualização**: 13/02/2026  
**Versão**: 1.0.0  
**Baseado em**: Testes Unitários (109 testes passando)
