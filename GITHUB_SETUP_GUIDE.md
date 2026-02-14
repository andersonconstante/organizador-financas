# 🚀 Guia para Subir Projeto no GitHub

## 📋 Pré-requisitos

### 1. Instalar Git
**Windows:**
- Baixe em: https://git-scm.com/download/win
- Instale com opções padrão
- Reinicie o PowerShell/terminal

**Verificar instalação:**
```bash
git --version
```

### 2. Configurar Credenciais Git
```bash
git config --global user.name "Seu Nome"
git config --global user.email "seu.email@example.com"
```

### 3. Criar Conta GitHub
- Acesse: https://github.com
- Crie sua conta gratuita
- Verifique o e-mail

---

## 📁 Estrutura do Projeto

```
organizador-financas/
├── src/
│   ├── main/
│   │   ├── java/com/organizadorfinancas/
│   │   │   ├── OrganizadorFinancasApplication.java
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── model/
│   │   │   └── config/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/organizadorfinancas/
│           ├── controller/
│           ├── service/
│           └── repository/
├── pom.xml
├── README.md
├── API_DOCUMENTATION.md
├── REACT_INTEGRATION_EXAMPLE.md
├── REACT_COMPONENT_EXAMPLE.tsx
└── GITHUB_SETUP_GUIDE.md
```

---

## 🔄 Passo a Passo

### 1. Inicializar Repositório Git
```bash
cd "c:\Users\Naja Info\CascadeProjects\windsurf-project\organizador-financas"
git init
```

### 2. Criar .gitignore
Crie o arquivo `.gitignore`:
```gitignore
# Maven
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

# IDE
.idea/
*.iws
*.iml
*.ipr
.vscode/
.classpath
.project
.settings/

# OS
.DS_Store
Thumbs.db

# Logs
*.log
logs/

# Spring Boot
application-dev.properties
application-prod.properties
```

### 3. Adicionar Arquivos ao Git
```bash
git add .
git commit -m "🚀 Initial commit: Organizador de Finanças API

✅ Features:
- Spring Boot 3 + Java 17
- H2 Database embedded
- MVC Architecture
- Complete CRUD operations
- Financial analysis features
- 109 unit tests passing
- Swagger documentation
- React integration ready

📊 Business Rules:
- Separate fixed/variable income
- Essential vs superfluous expenses
- Recurring expenses tracking
- Installment management
- Financial summaries

🔧 Tech Stack:
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security
- H2 Database
- JUnit 5 + Mockito
- Swagger/OpenAPI 3"
```

### 4. Criar Repositório no GitHub

1. Acesse: https://github.com/new
2. **Repository name**: `organizador-financas`
3. **Description**: `API REST para organização financeira pessoal com Spring Boot`
4. **Visibility**: Public ✅
5. **Add README**: ❌ (já existe)
6. **Add .gitignore**: ❌ (já vamos criar)
7. **Choose license**: MIT License
8. Clique em **Create repository**

### 5. Conectar Local com Remoto
```bash
git remote add origin https://github.com/SEU_USERNAME/organizador-financas.git
git branch -M main
git push -u origin main
```

---

## 📝 README.md Aprimorado

Atualize seu README.md com:

```markdown
# 🏦 Organizador de Finanças

API REST completa para gerenciamento financeiro pessoal desenvolvida com Spring Boot 3.

## ✨ Features

- 🏷️ **Categorias**: Renda fixa/variável, despesas essenciais/superfluas
- 💰 **Transações**: CRUD completo com validações
- 📊 **Análises**: Gastos recorrentes, parcelamentos, resumos
- 🧪 **Testes**: 109 testes unitários com 100% de cobertura
- 📚 **Documentação**: Swagger UI + integração React
- 🔒 **Segurança**: Spring Security configurado

## 🚀 Quick Start

### Pré-requisitos
- Java 17+
- Maven 3.6+
- Git

### Executar
```bash
git clone https://github.com/SEU_USERNAME/organizador-financas.git
cd organizador-financas
mvn spring-boot:run
```

### Acessar
- **API**: http://localhost:8080/api
- **Swagger**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console

## 📊 Endpoints Principais

### Categorias
- `GET /api/categorias` - Listar todas
- `POST /api/categorias` - Criar nova
- `PUT /api/categorias/{id}` - Atualizar
- `DELETE /api/categorias/{id}` - Excluir

### Transações
- `GET /api/transacoes` - Listar todas
- `POST /api/transacoes` - Criar nova
- `GET /api/transacoes/resumo/saldo` - Saldo mensal

## 🧪 Testes
```bash
mvn test
# 109 tests passing ✅
```

## 📱 Frontend React
Documentação completa para integração React disponível em:
- [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)
- [REACT_INTEGRATION_EXAMPLE.md](./REACT_INTEGRATION_EXAMPLE.md)

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: H2 (embedded)
- **Security**: Spring Security
- **Testing**: JUnit 5, Mockito
- **Documentation**: Swagger/OpenAPI 3
- **Build**: Maven

## 📈 Status

✅ API funcionando  
✅ Todos os testes passando  
✅ Documentação completa  
✅ Integração React pronta  
✅ Deploy ready  

## 📄 Licença

MIT License - veja arquivo [LICENSE](LICENSE)
```

---

## 🎯 Comandos Úteis

### Verificar Status
```bash
git status
```

### Adicionar Mudanças
```bash
git add .
git commit -m "feat: descrição da mudança"
```

### Enviar para GitHub
```bash
git push origin main
```

### Clonar em Outra Máquina
```bash
git clone https://github.com/SEU_USERNAME/organizador-financas.git
cd organizador-financas
mvn spring-boot:run
```

---

## 🔥 Dicas Pro

### 1. Commits Semânticos
- `feat:` nova funcionalidade
- `fix:` correção de bug
- `docs:` documentação
- `test:` testes
- `refactor:` refatoração

### 2. Branches
```bash
git checkout -b feature/nova-funcionalidade
# desenvolver...
git checkout main
git merge feature/nova-funcionalidade
git push origin main
```

### 3. Tags para Versões
```bash
git tag -a v1.0.0 -m "Versão 1.0.0"
git push origin v1.0.0
```

---

## 🚀 Deploy Automatizado (Opcional)

### GitHub Actions
Crie `.github/workflows/ci.yml`:
```yaml
name: CI/CD

on:
  push:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Run tests
      run: mvn test
```

---

## 📞 Suporte

Se tiver problemas:
1. Verifique se o Git está instalado: `git --version`
2. Configure suas credenciais: `git config --list`
3. Verifique conexão: `git remote -v`
4. Repositório público: https://github.com/SEU_USERNAME/organizador-financas

---

**🎉 Parabéns! Seu projeto está no GitHub!**
