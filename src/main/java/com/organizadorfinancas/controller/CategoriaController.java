package com.organizadorfinancas.controller;

import com.organizadorfinancas.model.Categoria;
import com.organizadorfinancas.model.TipoCategoria;
import com.organizadorfinancas.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "API para gerenciamento de categorias financeiras")
public class CategoriaController {
    
    @Autowired
    private CategoriaService categoriaService;
    
    @GetMapping
    @Operation(summary = "Listar todas as categorias", description = "Retorna uma lista de todas as categorias cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso")
    public ResponseEntity<List<Categoria>> findAll() {
        List<Categoria> categorias = categoriaService.findAll();
        return ResponseEntity.ok(categorias);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID", description = "Retorna uma categoria específica pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    public ResponseEntity<Categoria> findById(@Parameter(description = "ID da categoria") @PathVariable Long id) {
        Optional<Categoria> categoria = categoriaService.findById(id);
        return categoria.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Criar nova categoria", description = "Cria uma nova categoria no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<Categoria> create(@Valid @RequestBody Categoria categoria) {
        if (categoriaService.existsByNome(categoria.getNome())) {
            return ResponseEntity.badRequest().build();
        }
        Categoria savedCategoria = categoriaService.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoria);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria", description = "Atualiza os dados de uma categoria existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<Categoria> update(@Parameter(description = "ID da categoria") @PathVariable Long id, 
                                           @Valid @RequestBody Categoria categoria) {
        if (!categoriaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        categoria.setId(id);
        Categoria updatedCategoria = categoriaService.save(categoria);
        return ResponseEntity.ok(updatedCategoria);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir categoria", description = "Exclui uma categoria do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    public ResponseEntity<Void> deleteById(@Parameter(description = "ID da categoria") @PathVariable Long id) {
        if (!categoriaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar categorias por tipo", description = "Retorna categorias filtradas por tipo (RENDA_FIXA, RENDA_VARIAVEL, etc.)")
    public ResponseEntity<List<Categoria>> findByTipo(@Parameter(description = "Tipo da categoria") @PathVariable TipoCategoria tipo) {
        List<Categoria> categorias = categoriaService.findByTipo(tipo);
        return ResponseEntity.ok(categorias);
    }
    
    @GetMapping("/essencial/{essencial}")
    @Operation(summary = "Buscar categorias por essencialidade", description = "Retorna categorias filtradas por essencialidade (true/false)")
    public ResponseEntity<List<Categoria>> findByEssencial(@Parameter(description = "Se é essencial") @PathVariable Boolean essencial) {
        List<Categoria> categorias = categoriaService.findByEssencial(essencial);
        return ResponseEntity.ok(categorias);
    }
    
    @GetMapping("/renda-fixa")
    @Operation(summary = "Listar renda fixa", description = "Retorna todas as categorias de renda fixa")
    public ResponseEntity<List<Categoria>> findRendaFixa() {
        return ResponseEntity.ok(categoriaService.findRendaFixa());
    }
    
    @GetMapping("/renda-variavel")
    @Operation(summary = "Listar renda variável", description = "Retorna todas as categorias de renda variável")
    public ResponseEntity<List<Categoria>> findRendaVariavel() {
        return ResponseEntity.ok(categoriaService.findRendaVariavel());
    }
    
    @GetMapping("/despesas-essenciais")
    @Operation(summary = "Listar despesas essenciais", description = "Retorna todas as categorias de despesas essenciais")
    public ResponseEntity<List<Categoria>> findDespesasEssenciais() {
        return ResponseEntity.ok(categoriaService.findDespesasEssenciais());
    }
    
    @GetMapping("/despesas-superfluas")
    @Operation(summary = "Listar despesas supérfluas", description = "Retorna todas as categorias de despesas supérfluas")
    public ResponseEntity<List<Categoria>> findDespesasSuperfluas() {
        return ResponseEntity.ok(categoriaService.findDespesasSuperfluas());
    }
    
    @GetMapping("/gastos-invisiveis")
    @Operation(summary = "Listar gastos invisíveis", description = "Retorna todas as categorias de gastos invisíveis")
    public ResponseEntity<List<Categoria>> findGastosInvisiveis() {
        return ResponseEntity.ok(categoriaService.findGastosInvisiveis());
    }
}
