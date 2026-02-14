package com.organizadorfinancas.config;

import com.organizadorfinancas.model.Categoria;
import com.organizadorfinancas.model.TipoCategoria;
import com.organizadorfinancas.model.TipoTransacao;
import com.organizadorfinancas.model.Transacao;
import com.organizadorfinancas.repository.CategoriaRepository;
import com.organizadorfinancas.repository.TransacaoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataLoader {
    
    @Bean
    CommandLineRunner initDatabase(CategoriaRepository categoriaRepository, 
                                 TransacaoRepository transacaoRepository) {
        return args -> {
            
            // Categorias de Renda
            Categoria salario = new Categoria("Salário", true, TipoCategoria.RENDA_FIXA);
            Categoria freelancer = new Categoria("Freelancer", false, TipoCategoria.RENDA_VARIAVEL);
            Categoria investimentos = new Categoria("Investimentos", false, TipoCategoria.RENDA_VARIAVEL);
            
            // Categorias de Despesas Essenciais
            Categoria aluguel = new Categoria("Aluguel", true, TipoCategoria.DESPESA_ESSENCIAL);
            Categoria alimentacao = new Categoria("Alimentação", true, TipoCategoria.DESPESA_ESSENCIAL);
            Categoria transporte = new Categoria("Transporte", true, TipoCategoria.DESPESA_ESSENCIAL);
            Categoria saude = new Categoria("Saúde", true, TipoCategoria.DESPESA_ESSENCIAL);
            Categoria contas = new Categoria("Contas (Água/Luz/Telefone)", true, TipoCategoria.DESPESA_ESSENCIAL);
            
            // Categorias de Despesas Supérfluas
            Categoria streaming = new Categoria("Streaming", false, TipoCategoria.DESPESA_SUPERFLUA);
            Categoria restaurantes = new Categoria("Restaurantes", false, TipoCategoria.DESPESA_SUPERFLUA);
            Categoria compras = new Categoria("Compras", false, TipoCategoria.DESPESA_SUPERFLUA);
            Categoria viagens = new Categoria("Viagens", false, TipoCategoria.DESPESA_SUPERFLUA);
            
            // Categorias de Gastos Invisíveis
            Categoria cafe = new Categoria("Café", false, TipoCategoria.GASTO_INVISIVEL);
            Categoria taxi = new Categoria("Taxi/Uber", false, TipoCategoria.GASTO_INVISIVEL);
            Categoria pequenasCompras = new Categoria("Pequenas Compras", false, TipoCategoria.GASTO_INVISIVEL);
            
            // Salvar categorias
            categoriaRepository.saveAll(java.util.Arrays.asList(
                salario, freelancer, investimentos,
                aluguel, alimentacao, transporte, saude, contas,
                streaming, restaurantes, compras, viagens,
                cafe, taxi, pequenasCompras
            ));
            
            // Transações de Receita
            Transacao salarioMes = new Transacao("Salário Fevereiro", new BigDecimal("5000.00"), 
                LocalDate.of(2026, 2, 5), TipoTransacao.RECEITA, false, salario);
            
            Transacao projetoFreelancer = new Transacao("Projeto Website", new BigDecimal("1500.00"), 
                LocalDate.of(2026, 2, 15), TipoTransacao.RECEITA, false, freelancer);
            
            // Transações de Despesas Essenciais
            Transacao aluguelMes = new Transacao("Aluguel Fevereiro", new BigDecimal("1500.00"), 
                LocalDate.of(2026, 2, 1), TipoTransacao.DESPESA, true, aluguel);
            
            Transacao supermercado = new Transacao("Supermercado Semanal", new BigDecimal("400.00"), 
                LocalDate.of(2026, 2, 10), TipoTransacao.DESPESA, true, alimentacao);
            
            Transacao combustivel = new Transacao("Combustível", new BigDecimal("200.00"), 
                LocalDate.of(2026, 2, 8), TipoTransacao.DESPESA, true, transporte);
            
            Transacao planoSaude = new Transacao("Plano de Saúde", new BigDecimal("300.00"), 
                LocalDate.of(2026, 2, 5), TipoTransacao.DESPESA, true, saude);
            
            Transacao contaLuz = new Transacao("Conta de Luz", new BigDecimal("150.00"), 
                LocalDate.of(2026, 2, 6), TipoTransacao.DESPESA, true, contas);
            
            // Transações de Despesas Supérfluas
            Transacao netflix = new Transacao("Netflix", new BigDecimal("39.90"), 
                LocalDate.of(2026, 2, 10), TipoTransacao.DESPESA, true, streaming);
            
            Transacao jantarRestaurante = new Transacao("Jantar Restaurante", new BigDecimal("120.00"), 
                LocalDate.of(2026, 2, 12), TipoTransacao.DESPESA, false, restaurantes);
            
            Transacao compraRoupa = new Transacao("Roupas", new BigDecimal("250.00"), 
                LocalDate.of(2026, 2, 14), TipoTransacao.DESPESA, false, compras);
            
            // Transações de Gastos Invisíveis
            Transacao cafeDiario = new Transacao("Café da Manhã", new BigDecimal("15.00"), 
                LocalDate.of(2026, 2, 13), TipoTransacao.DESPESA, false, cafe);
            
            Transacao corridaUber = new Transacao("Corrida Uber", new BigDecimal("35.00"), 
                LocalDate.of(2026, 2, 11), TipoTransacao.DESPESA, false, taxi);
            
            // Despesa parcelada grande
            Transacao notebook = new Transacao("Notebook Novo", new BigDecimal("3600.00"), 
                LocalDate.of(2026, 2, 1), TipoTransacao.DESPESA, false, compras);
            notebook.setParcelas(12);
            notebook.setParcelaAtual(1);
            
            // Salvar transações
            transacaoRepository.saveAll(java.util.Arrays.asList(
                salarioMes, projetoFreelancer,
                aluguelMes, supermercado, combustivel, planoSaude, contaLuz,
                netflix, jantarRestaurante, compraRoupa,
                cafeDiario, corridaUber, notebook
            ));
            
            System.out.println("=== DADOS INICIAIS CARREGADOS ===");
            System.out.println("Categorias: " + categoriaRepository.count());
            System.out.println("Transações: " + transacaoRepository.count());
            System.out.println("Acesse: http://localhost:8080/swagger-ui.html");
            System.out.println("Console H2: http://localhost:8080/h2-console");
        };
    }
}
