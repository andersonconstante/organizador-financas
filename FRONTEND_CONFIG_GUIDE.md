# 🔧 Guia de Configuração Frontend

## 🌐 Configuração CORS Ajustada

A classe `WebConfig` foi atualizada para permitir consumo completo do frontend:

### ✅ **Origens Permitidas:**
- `http://localhost:3000` - React development
- `http://127.0.0.1:3000` - React development alternative  
- `http://localhost:5173` - Vite development
- `http://127.0.0.1:5173` - Vite development alternative
- `https://seufrontend.com` - Produção (substitua pelo seu domínio)

### ✅ **Métodos HTTP Permitidos:**
- `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`, `PATCH`

### ✅ **Headers Permitidos:**
- Todos os headers (`*`)

### ✅ **Cache CORS:**
- 1 hora (3600 segundos)

---

## 📱 Configuração React

### 1. **Variáveis de Ambiente**
Crie `.env.local` no projeto React:
```env
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_ENVIRONMENT=development
```

### 2. **Configuração Axios**
```typescript
// src/services/api.ts
import axios from 'axios';

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Importante para CORS
});

export default api;
```

### 3. **Exemplo de Requisição**
```typescript
// src/services/categoriaService.ts
import api from './api';

export const categoriaService = {
  listar: async () => {
    const response = await api.get('/categorias');
    return response.data;
  },

  criar: async (categoria: any) => {
    const response = await api.post('/categorias', categoria);
    return response.data;
  }
};
```

---

## 🚀 Teste de Conexão

### **Componente de Teste**
```typescript
// src/components/TestApi.tsx
import React, { useState, useEffect } from 'react';
import api from '../services/api';

export const TestApi: React.FC = () => {
  const [data, setData] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const testConnection = async () => {
      try {
        const response = await api.get('/categorias');
        setData(response.data);
        console.log('✅ API conectada:', response.data);
      } catch (err: any) {
        setError(err.message);
        console.error('❌ Erro na API:', err);
      }
    };

    testConnection();
  }, []);

  if (error) {
    return <div>❌ Erro: {error}</div>;
  }

  if (!data) {
    return <div>🔄 Carregando...</div>;
  }

  return (
    <div>
      <h2>✅ API Conectada!</h2>
      <pre>{JSON.stringify(data, null, 2)}</pre>
    </div>
  );
};
```

---

## 🔍 Respostas da API

### **Sucesso (200)**
```json
{
  "id": 1,
  "nome": "Salário",
  "essencial": true,
  "tipo": "RENDA_FIXA"
}
```

### **Erro de Validação (400)**
```json
{
  "timestamp": "2026-02-14T00:45:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Dados inválidos",
  "errors": {
    "nome": "Nome é obrigatório",
    "tipo": "Tipo é obrigatório"
  },
  "path": "uri=/api/categorias"
}
```

### **Erro Interno (500)**
```json
{
  "timestamp": "2026-02-14T00:45:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Erro interno do servidor",
  "path": "uri=/api/transacoes"
}
```

---

## 🛠️ Configurações Avançadas

### **React + Vite**
```typescript
// vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
```

### **React + Create React App**
```json
// package.json
{
  "proxy": "http://localhost:8080"
}
```

---

## 🧪 Testes de Integração

### **1. Testar Conexão**
```bash
# Iniciar backend
mvn spring-boot:run

# Iniciar frontend (nova aba)
npm start
```

### **2. Verificar Console**
- Abra DevTools (F12)
- Veja aba Network
- Verifique requisições para `/api/*`

### **3. Testar Endpoints**
```javascript
// No console do navegador
fetch('http://localhost:8080/api/categorias')
  .then(response => response.json())
  .then(data => console.log('✅ Categorias:', data))
  .catch(error => console.error('❌ Erro:', error));
```

---

## 🚨 Solução de Problemas

### **CORS Error**
```
Access to fetch at 'http://localhost:8080/api/categorias' from origin 'http://localhost:3000' has been blocked by CORS policy
```
**Solução:** Verifique se o backend está rodando e a porta correta

### **Network Error**
```
Network Error
```
**Solução:** Verifique se o backend está online: `http://localhost:8080/swagger-ui.html`

### **404 Not Found**
```
Request failed with status code 404
```
**Solução:** Verifique a URL da API e os endpoints

---

## 📱 Exemplo Completo de Componente

```typescript
// src/components/CategoriaList.tsx
import React, { useState, useEffect } from 'react';
import api from '../services/api';

interface Categoria {
  id: number;
  nome: string;
  essencial: boolean;
  tipo: string;
}

export const CategoriaList: React.FC = () => {
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadCategorias = async () => {
      try {
        setLoading(true);
        const response = await api.get('/categorias');
        setCategorias(response.data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Erro ao carregar categorias');
      } finally {
        setLoading(false);
      }
    };

    loadCategorias();
  }, []);

  if (loading) return <div>🔄 Carregando...</div>;
  if (error) return <div>❌ {error}</div>;

  return (
    <div>
      <h2>📂 Categorias</h2>
      <ul>
        {categorias.map(categoria => (
          <li key={categoria.id}>
            {categoria.nome} ({categoria.tipo})
          </li>
        ))}
      </ul>
    </div>
  );
};
```

---

## ✅ Checklist Final

- [ ] Backend rodando em `http://localhost:8080`
- [ ] Frontend configurado com `REACT_APP_API_URL`
- [ ] CORS configurado no backend ✅
- [ ] Teste de conexão funcionando
- [ ] Componentes consumindo API corretamente

---

**🎉 Seu frontend está pronto para consumir a API!**
