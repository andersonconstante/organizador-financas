# Organizador de Finanças

API REST para gerenciamento de finanças pessoais desenvolvida com Spring Boot 3 e Java 17.

## 🎯 Objetivo

Este projeto é um MVP para organização financeira pessoal que implementa as seguintes regras de negócio:

- ✅ **Separar renda fixa e variável** - Categorias diferenciadas para tipos de renda
- ✅ **Listar todas as despesas** - Consulta completa de gastos
- ✅ **Dividir gastos por categorias** - Sistema de categorização robusto
- ✅ **Separar essenciais de supérfluos** - Classificação inteligente de despesas
- ✅ **Identificar gastos recorrentes** - Controle de despesas mensais fixas
- ✅ **Analisar gastos invisíveis** - Rastreamento de pequenos gastos do dia a dia
- ✅ **Quebrar despesas grandes em mensais** - Sistema de parcelamento
- ✅ **Visualizar tudo em uma lista simples** - Interface REST clara e objetiva
- ✅ **Eliminar o que não agrega valor** - Relatórios para identificar cortes possíveis

## 🛠️ Stack Tecnológico

- **Java 17** - Versão LTS mais recente
- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Segurança (configurado para MVP)
- **H2 Database** - Banco embutido para desenvolvimento
- **Swagger/OpenAPI 3** - Documentação automática
- **Maven** - Gerenciamento de dependências

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- IDE de sua preferência (IntelliJ, Eclipse, VS Code)

## 🚀 Executando a Aplicação

### 1. Clone o repositório
```bash
git clone <repositorio>
cd organizador-financas
```

### 2. Compile e execute
```bash
mvn clean install
mvn spring-boot:run
```

### 3. Acesse a aplicação

- **API Base**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Console H2**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:organizadorfinancas`
  - Username: `sa`
  - Password: (vazio)

## 📊 Modelo de Dados

### Entidades Principais

#### Categoria
- **id**: Identificador único
- **nome**: Nome da categoria
- **essencial**: Boolean (true = essencial, false = supérfluo)
- **tipo**: Enum (RENDA_FIXA, RENDA_VARIAVEL, DESPESA_ESSENCIAL, DESPESA_SUPERFLUA, GASTO_INVISIVEL, INVESTIMENTO)

#### Transação
- **id**: Identificador único
- **descricao**: Descrição da transação
- **valor**: Valor (BigDecimal)
- **data**: Data da transação
- **tipo**: Enum (RECEITA, DESPESA)
- **recorrente**: Boolean para gastos recorrentes
- **parcelas**: Número total de parcelas
- **parcelaAtual**: Parcela atual
- **categoria**: Relacionamento com Categoria
- **observacoes**: Campo livre para notas

## 🔌 Endpoints Principais

### Categorias
- `GET /api/categorias` - Listar todas
- `GET /api/categorias/{id}` - Buscar por ID
- `POST /api/categorias` - Criar nova
- `PUT /api/categorias/{id}` - Atualizar
- `DELETE /api/categorias/{id}` - Excluir
- `GET /api/categorias/renda-fixa` - Renda fixa
- `GET /api/categorias/renda-variavel` - Renda variável
- `GET /api/categorias/despesas-essenciais` - Despesas essenciais
- `GET /api/categorias/despesas-superfluas` - Despesas supérfluas
- `GET /api/categorias/gastos-invisiveis` - Gastos invisíveis

### Transações
- `GET /api/transacoes` - Listar todas
- `GET /api/transacoes/{id}` - Buscar por ID
- `POST /api/transacoes` - Criar nova
- `PUT /api/transacoes/{id}` - Atualizar
- `DELETE /api/transacoes/{id}` - Excluir
- `GET /api/transacoes/despesas` - Todas despesas
- `GET /api/transacoes/receitas` - Todas receitas
- `GET /api/transacoes/recorrentes` - Gastos recorrentes
- `GET /api/transacoes/superfluos` - Gastos supérfluos
- `GET /api/transacoes/parceladas` - Despesas parceladas

### Resumos Financeiros
- `GET /api/transacoes/resumo/despesas` - Total despesas mês atual
- `GET /api/transacoes/resumo/receitas` - Total receitas mês atual
- `GET /api/transacoes/resumo/saldo` - Saldo mensal
- `GET /api/transacoes/resumo/essenciais` - Total despesas essenciais
- `GET /api/transacoes/resumo/superfluas` - Total despesas supérfluas
- `GET /api/transacoes/resumo/recorrentes` - Total gastos recorrentes
- `GET /api/transacoes/resumo/por-categoria?tipo=DESPESA` - Totais por categoria

## 📈 Exemplos de Uso

### 1. Criar Categoria de Despesa Essencial
```json
POST /api/categorias
{
  "nome": "Supermercado",
  "essencial": true,
  "tipo": "DESPESA_ESSENCIAL"
}
```

### 2. Criar Transação de Despesa Recorrente
```json
POST /api/transacoes
{
  "descricao": "Aluguel",
  "valor": 1500.00,
  "data": "2026-02-01",
  "tipo": "DESPESA",
  "recorrente": true,
  "parcelas": 1,
  "parcelaAtual": 1,
  "categoria": {
    "id": 1
  },
  "observacoes": "Aluguel mensal do apartamento"
}
```

### 3. Criar Despesa Parcelada
```json
POST /api/transacoes
{
  "descricao": "Notebook Novo",
  "valor": 3600.00,
  "data": "2026-02-01",
  "tipo": "DESPESA",
  "recorrente": false,
  "parcelas": 12,
  "parcelaAtual": 1,
  "categoria": {
    "id": 3
  }
}
```

## 🎯 Funcionalidades Implementadas

### ✅ Separação de Renda
- **Renda Fixa**: Salário, aposentadoria, aluguéis
- **Renda Variável**: Freelancer, bônus, investimentos

### ✅ Categorização Inteligente
- **Essenciais**: Aluguel, alimentação, saúde, transporte
- **Supérfluos**: Streaming, restaurantes, compras não essenciais
- **Invisíveis**: Pequenos gastos do dia a dia (café, taxi, etc.)

### ✅ Controle de Gastos
- **Recorrentes**: Identificação automática de despesas mensais fixas
- **Parcelados**: Sistema para dividir grandes despesas
- **Por Período**: Filtros por datas específicas

### ✅ Relatórios e Análises
- **Saldo Mensal**: Receitas - Despesas
- **Totais por Categoria**: Análise de distribuição de gastos
- **Comparativos**: Essenciais vs Supérfluos

## 🔧 Configuração

### Banco de Dados
O projeto utiliza H2 em modo memória para desenvolvimento. Para produção, altere `application.properties`:

```properties
# PostgreSQL (produção)
spring.datasource.url=jdbc:postgresql://localhost:5432/organizadorfinancas
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

### Segurança
Para MVP, a segurança está configurada de forma básica. Para produção:

```properties
spring.security.user.name=admin
spring.security.password=senha_forte
```

## 📝 Próximos Passos (Roadmap)

- [ ] Autenticação JWT
- [ ] Integração com banco PostgreSQL
- [ ] Dashboard web (React/Vue)
- [ ] Exportação de relatórios (PDF/Excel)
- [ ] Metas financeiras
- [ ] Alertas e notificações
- [ ] Integração com APIs bancárias
- [ ] Machine Learning para previsões

## 🤝 Contribuindo

1. Fork o projeto
2. Crie sua feature branch (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adicionando nova funcionalidade'`)
4. Push para o branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está licenciado sob a Licença Apache 2.0 - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 👨‍💻 Autor

Desenvolvido como parte do projeto de organização financeira pessoal.

---

**Acesse a documentação completa em Swagger**: http://localhost:8080/swagger-ui.html
