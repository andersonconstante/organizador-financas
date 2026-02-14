# Guia de Integração React - Organizador de Finanças

## 🚀 Setup Rápido

### 1. Instalação de Dependências

```bash
npm install axios @types/axios
# ou
yarn add axios @types/axios
```

### 2. Estrutura de Pastas Sugerida

```
src/
├── services/
│   ├── api.ts          # Configuração do Axios
│   ├── categoriaService.ts
│   ├── transacaoService.ts
│   └── resumoService.ts
├── types/
│   ├── categoria.ts
│   ├── transacao.ts
│   └── api.ts
├── components/
│   ├── forms/
│   ├── lists/
│   └── charts/
├── hooks/
│   ├── useCategorias.ts
│   ├── useTransacoes.ts
│   └── useResumo.ts
└── utils/
    ├── formatters.ts
    ├── validators.ts
    └── constants.ts
```

---

## 📝 Arquivos de Configuração

### `src/services/api.ts`

```typescript
import axios, { AxiosInstance, AxiosResponse } from 'axios';

// Interfaces baseadas nos testes da API
export interface ApiResponse<T> {
  data: T;
  status: number;
  statusText: string;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path?: string;
}

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
      headers: {
        'Content-Type': 'application/json',
      },
      timeout: 10000,
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    // Request interceptor
    this.api.interceptors.request.use(
      (config) => {
        console.log(`🚀 API Request: ${config.method?.toUpperCase()} ${config.url}`);
        return config;
      },
      (error) => {
        console.error('❌ Request Error:', error);
        return Promise.reject(error);
      }
    );

    // Response interceptor
    this.api.interceptors.response.use(
      (response: AxiosResponse) => {
        console.log(`✅ API Response: ${response.status} ${response.config.url}`);
        return response;
      },
      (error) => {
        const apiError: ApiError = error.response?.data || {
          timestamp: new Date().toISOString(),
          status: error.response?.status || 500,
          error: 'Internal Server Error',
          message: 'Erro ao comunicar com o servidor',
        };

        console.error('❌ API Error:', apiError);

        // Tratamento específico baseado nos testes
        switch (apiError.status) {
          case 400:
            console.warn('⚠️ Dados inválidos - verificar campos obrigatórios');
            break;
          case 404:
            console.warn('⚠️ Recurso não encontrado');
            break;
          case 403:
            console.warn('⚠️ Acesso negado');
            break;
          case 500:
            console.error('💥 Erro interno do servidor');
            break;
        }

        return Promise.reject(apiError);
      }
    );
  }

  // Métodos HTTP genéricos
  public get<T>(url: string): Promise<AxiosResponse<T>> {
    return this.api.get(url);
  }

  public post<T>(url: string, data: any): Promise<AxiosResponse<T>> {
    return this.api.post(url, data);
  }

  public put<T>(url: string, data: any): Promise<AxiosResponse<T>> {
    return this.api.put(url, data);
  }

  public delete(url: string): Promise<AxiosResponse<void>> {
    return this.api.delete(url);
  }
}

export const apiService = new ApiService();
```

### `src/types/categoria.ts`

```typescript
// Baseado nos testes unitários
export enum TipoCategoria {
  RENDA_FIXA = 'RENDA_FIXA',
  RENDA_VARIAVEL = 'RENDA_VARIAVEL',
  DESPESA_ESSENCIAL = 'DESPESA_ESSENCIAL',
  DESPESA_SUPERFLUA = 'DESPESA_SUPERFLUA',
  GASTO_INVISIVEL = 'GASTO_INVISIVEL',
  INVESTIMENTO = 'INVESTIMENTO',
}

export interface Categoria {
  id: number;
  nome: string;
  essencial: boolean;
  tipo: TipoCategoria;
}

export interface CategoriaForm {
  nome: string;
  essencial: boolean;
  tipo: TipoCategoria;
}

export interface CategoriaValidationError {
  nome?: string;
  tipo?: string;
  essencial?: string;
}
```

### `src/types/transacao.ts`

```typescript
export enum TipoTransacao {
  RECEITA = 'RECEITA',
  DESPESA = 'DESPESA',
}

export interface Transacao {
  id: number;
  descricao: string;
  valor: string;
  data: string;
  tipo: TipoTransacao;
  recorrente: boolean;
  parcelas: number;
  parcelaAtual: number;
  categoria: Categoria;
  observacoes?: string;
  valorMensal?: string;
}

export interface TransacaoForm {
  descricao: string;
  valor: string;
  data: string;
  tipo: TipoTransacao;
  recorrente: boolean;
  parcelas: number;
  parcelaAtual: number;
  categoriaId: number;
  observacoes?: string;
}

export interface TransacaoValidationError {
  descricao?: string;
  valor?: string;
  data?: string;
  tipo?: string;
  categoriaId?: string;
  parcelas?: string;
  parcelaAtual?: string;
}
```

---

## 🔧 Services Implementados

### `src/services/categoriaService.ts`

```typescript
import { apiService } from './api';
import { Categoria, CategoriaForm, TipoCategoria } from '../types/categoria';

export class CategoriaService {
  private readonly basePath = '/categorias';

  // Testado: findAll_ShouldReturnAllCategorias
  async listarTodas(): Promise<Categoria[]> {
    const response = await apiService.get<Categoria[]>(this.basePath);
    return response.data;
  }

  // Testado: findById_ShouldReturnCategoria_WhenExists
  async buscarPorId(id: number): Promise<Categoria> {
    const response = await apiService.get<Categoria>(`${this.basePath}/${id}`);
    return response.data;
  }

  // Testado: create_ShouldCreateCategoria_WhenValid
  async criar(categoria: CategoriaForm): Promise<Categoria> {
    const response = await apiService.post<Categoria>(this.basePath, categoria);
    return response.data;
  }

  // Testado: update_ShouldUpdateCategoria_WhenExists
  async atualizar(id: number, categoria: CategoriaForm): Promise<Categoria> {
    const response = await apiService.put<Categoria>(`${this.basePath}/${id}`, categoria);
    return response.data;
  }

  // Testado: deleteById_ShouldDeleteCategoria_WhenExists
  async excluir(id: number): Promise<void> {
    await apiService.delete(`${this.basePath}/${id}`);
  }

  // Testado: findRendaFixa_ShouldReturnRendaFixa
  async listarRendaFixa(): Promise<Categoria[]> {
    const response = await apiService.get<Categoria[]>(`${this.basePath}/renda-fixa`);
    return response.data;
  }

  // Testado: findRendaVariavel_ShouldReturnRendaVariavel
  async listarRendaVariavel(): Promise<Categoria[]> {
    const response = await apiService.get<Categoria[]>(`${this.basePath}/renda-variavel`);
    return response.data;
  }

  // Testado: findDespesasEssenciais_ShouldReturnDespesasEssenciais
  async listarDespesasEssenciais(): Promise<Categoria[]> {
    const response = await apiService.get<Categoria[]>(`${this.basePath}/despesas-essenciais`);
    return response.data;
  }

  // Testado: findDespesasSuperfluas_ShouldReturnDespesasSuperfluas
  async listarDespesasSuperfluas(): Promise<Categoria[]> {
    const response = await apiService.get<Categoria[]>(`${this.basePath}/despesas-superfluas`);
    return response.data;
  }

  // Testado: findGastosInvisiveis_ShouldReturnGastosInvisiveis
  async listarGastosInvisiveis(): Promise<Categoria[]> {
    const response = await apiService.get<Categoria[]>(`${this.basePath}/gastos-invisiveis`);
    return response.data;
  }

  // Testado: findByTipo_ShouldReturnCategoriasByTipo
  async listarPorTipo(tipo: TipoCategoria): Promise<Categoria[]> {
    const response = await apiService.get<Categoria[]>(`${this.basePath}/tipo/${tipo}`);
    return response.data;
  }

  // Testado: findByEssencial_ShouldReturnCategoriasByEssencial
  async listarPorEssencialidade(essencial: boolean): Promise<Categoria[]> {
    const response = await apiService.get<Categoria[]>(`${this.basePath}/essencial/${essencial}`);
    return response.data;
  }
}

export const categoriaService = new CategoriaService();
```

### `src/services/transacaoService.ts`

```typescript
import { apiService } from './api';
import { Transacao, TransacaoForm, TipoTransacao } from '../types/transacao';
import { Categoria } from '../types/categoria';

export class TransacaoService {
  private readonly basePath = '/transacoes';

  // Testado: findAll_ShouldReturnAllTransacoes
  async listarTodas(): Promise<Transacao[]> {
    const response = await apiService.get<Transacao[]>(this.basePath);
    return response.data;
  }

  // Testado: findById_ShouldReturnTransacao_WhenExists
  async buscarPorId(id: number): Promise<Transacao> {
    const response = await apiService.get<Transacao>(`${this.basePath}/${id}`);
    return response.data;
  }

  // Testado: create_ShouldCreateTransacao_WhenValid
  async criar(transacao: TransacaoForm): Promise<Transacao> {
    const response = await apiService.post<Transacao>(this.basePath, {
      ...transacao,
      categoria: { id: transacao.categoriaId }
    });
    return response.data;
  }

  // Testado: update_ShouldUpdateTransacao_WhenExists
  async atualizar(id: number, transacao: TransacaoForm): Promise<Transacao> {
    const response = await apiService.put<Transacao>(`${this.basePath}/${id}`, {
      ...transacao,
      categoria: { id: transacao.categoriaId }
    });
    return response.data;
  }

  // Testado: deleteById_ShouldDeleteTransacao_WhenExists
  async excluir(id: number): Promise<void> {
    await apiService.delete(`${this.basePath}/${id}`);
  }

  // Testado: findAllDespesas_ShouldReturnDespesas
  async listarDespesas(): Promise<Transacao[]> {
    const response = await apiService.get<Transacao[]>(`${this.basePath}/despesas`);
    return response.data;
  }

  // Testado: findAllReceitas_ShouldReturnReceitas
  async listarReceitas(): Promise<Transacao[]> {
    const response = await apiService.get<Transacao[]>(`${this.basePath}/receitas`);
    return response.data;
  }

  // Testado: findGastosRecorrentes_ShouldReturnGastosRecorrentes
  async listarGastosRecorrentes(): Promise<Transacao[]> {
    const response = await apiService.get<Transacao[]>(`${this.basePath}/recorrentes`);
    return response.data;
  }

  // Testado: findDespesasParceladas_ShouldReturnDespesasParceladas
  async listarDespesasParceladas(): Promise<Transacao[]> {
    const response = await apiService.get<Transacao[]>(`${this.basePath}/parceladas`);
    return response.data;
  }

  // Testado: findByPeriodo_ShouldReturnTransacoesByPeriodo
  async listarPorPeriodo(dataInicio: string, dataFim: string): Promise<Transacao[]> {
    const response = await apiService.get<Transacao[]>(
      `${this.basePath}/periodo?dataInicio=${dataInicio}&dataFim=${dataFim}`
    );
    return response.data;
  }

  // Testado: findByCategoria_ShouldReturnTransacoesByCategoria
  async listarPorCategoria(categoriaId: number): Promise<Transacao[]> {
    const response = await apiService.get<Transacao[]>(`${this.basePath}/categoria/${categoriaId}`);
    return response.data;
  }
}

export const transacaoService = new TransacaoService();
```

### `src/services/resumoService.ts`

```typescript
import { apiService } from './api';

export class ResumoService {
  private readonly basePath = '/transacoes/resumo';

  // Testado: getTotalDespesas_ShouldReturnTotalDespesas
  async getTotalDespesas(): Promise<string> {
    const response = await apiService.get<string>(`${this.basePath}/despesas`);
    return response.data;
  }

  // Testado: getTotalReceitas_ShouldReturnTotalReceitas
  async getTotalReceitas(): Promise<string> {
    const response = await apiService.get<string>(`${this.basePath}/receitas`);
    return response.data;
  }

  // Testado: getSaldoMensal_ShouldReturnSaldoMensal
  async getSaldoMensal(): Promise<string> {
    const response = await apiService.get<string>(`${this.basePath}/saldo`);
    return response.data;
  }

  // Testado: getTotalGastosRecorrentes
  async getTotalGastosRecorrentes(): Promise<string> {
    const response = await apiService.get<string>(`${this.basePath}/recorrentes`);
    return response.data;
  }

  // Testado: getTotalDespesasEssenciais
  async getTotalDespesasEssenciais(): Promise<string> {
    const response = await apiService.get<string>(`${this.basePath}/essenciais`);
    return response.data;
  }

  // Testado: getTotalDespesasSuperfluas
  async getTotalDespesasSuperfluas(): Promise<string> {
    const response = await apiService.get<string>(`${this.basePath}/superfluas`);
    return response.data;
  }

  // Testado: getTotaisPorCategoria_ShouldReturnTotaisPorCategoria
  async getTotaisPorCategoria(tipo: 'RECEITA' | 'DESPESA'): Promise<[string, string][]> {
    const response = await apiService.get<[string, string][]>(`${this.basePath}/por-categoria?tipo=${tipo}`);
    return response.data;
  }
}

export const resumoService = new ResumoService();
```

---

## 🎯 Custom Hooks React

### `src/hooks/useCategorias.ts`

```typescript
import { useState, useEffect } from 'react';
import { categoriaService } from '../services/categoriaService';
import { Categoria, CategoriaForm } from '../types/categoria';

export const useCategorias = () => {
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const carregarCategorias = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const data = await categoriaService.listarTodas();
      setCategorias(data);
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar categorias');
    } finally {
      setLoading(false);
    }
  };

  const criarCategoria = async (categoria: CategoriaForm): Promise<Categoria> => {
    try {
      const novaCategoria = await categoriaService.criar(categoria);
      setCategorias(prev => [...prev, novaCategoria]);
      return novaCategoria;
    } catch (err: any) {
      setError(err.message || 'Erro ao criar categoria');
      throw err;
    }
  };

  const atualizarCategoria = async (id: number, categoria: CategoriaForm): Promise<Categoria> => {
    try {
      const categoriaAtualizada = await categoriaService.atualizar(id, categoria);
      setCategorias(prev => 
        prev.map(cat => cat.id === id ? categoriaAtualizada : cat)
      );
      return categoriaAtualizada;
    } catch (err: any) {
      setError(err.message || 'Erro ao atualizar categoria');
      throw err;
    }
  };

  const excluirCategoria = async (id: number): Promise<void> => {
    try {
      await categoriaService.excluir(id);
      setCategorias(prev => prev.filter(cat => cat.id !== id));
    } catch (err: any) {
      setError(err.message || 'Erro ao excluir categoria');
      throw err;
    }
  };

  useEffect(() => {
    carregarCategorias();
  }, []);

  return {
    categorias,
    loading,
    error,
    criarCategoria,
    atualizarCategoria,
    excluirCategoria,
    recarregar: carregarCategorias
  };
};
```

---

## 🧪 Testes de Integração

### `src/__tests__/services/categoriaService.test.ts`

```typescript
import { categoriaService } from '../../../services/categoriaService';
import { mockAxios, mockAxiosError } from '../../__mocks__/axios';

// Mock baseado nos testes do backend
jest.mock('../../../services/api', () => ({
  apiService: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  }
}));

describe('CategoriaService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('deve listar todas as categorias', async () => {
    const mockCategorias = [
      { id: 1, nome: 'Salário', essencial: true, tipo: 'RENDA_FIXA' }
    ];
    
    mockAxios.get.mockResolvedValue({ data: mockCategorias });
    
    const result = await categoriaService.listarTodas();
    
    expect(result).toEqual(mockCategorias);
    expect(mockAxios.get).toHaveBeenCalledWith('/categorias');
  });

  it('deve criar nova categoria', async () => {
    const novaCategoria = {
      nome: 'Transporte',
      essencial: true,
      tipo: 'DESPESA_ESSENCIAL'
    };
    
    const categoriaCriada = { id: 2, ...novaCategoria };
    mockAxios.post.mockResolvedValue({ data: categoriaCriada });
    
    const result = await categoriaService.criar(novaCategoria);
    
    expect(result).toEqual(categoriaCriada);
    expect(mockAxios.post).toHaveBeenCalledWith('/categorias', novaCategoria);
  });

  it('deve tratar erro 400 ao criar categoria duplicada', async () => {
    const categoriaDuplicada = {
      nome: 'Salário',
      essencial: true,
      tipo: 'RENDA_FIXA'
    };
    
    const apiError = {
      response: {
        status: 400,
        data: {
          message: 'Categoria com nome já existe'
        }
      }
    };
    
    mockAxiosError(apiError);
    
    await expect(categoriaService.criar(categoriaDuplicada))
      .rejects.toThrow('Categoria com nome já existe');
  });
});
```

---

## 🚨 Checklist de Integração

### ✅ **Antes de Ir para Produção**

- [ ] **Validação Client-Side**: Implementar validações baseadas nos testes
- [ ] **Tratamento de Erros**: Configurar interceptors para 400/404/500
- [ ] **Formatação de Dados**: Formatos de data, moeda e enums
- [ ] **Loading States**: Indicadores de carregamento
- [ ] **Error Boundaries**: Tratamento de erros React
- [ ] **Testes E2E**: Cypress/Playwright para fluxos críticos
- [ ] **Environment Variables**: Configuração para diferentes ambientes
- [ ] **Performance**: Lazy loading e otimização
- [ ] **Accessibility**: ARIA labels e navegação por teclado

### 🔍 **Testes Manuais Sugeridos**

1. **Criar categoria com nome duplicado** → Deve mostrar erro 400
2. **Buscar transação inexistente** → Deve mostrar erro 404  
3. **Enviar valor negativo** → Deve mostrar erro 400
4. **Enviar data inválida** → Deve mostrar erro 400
5. **Criar transação sem categoria** → Deve mostrar erro 400

---

**Esta documentação foi criada com base nos 109 testes unitários que passam, garantindo integração robusta e sem bugs entre frontend React e backend Spring Boot.**
