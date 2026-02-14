import React, { useState, useEffect } from 'react';
import { categoriaService } from '../services/categoriaService';
import { transacaoService } from '../services/transacaoService';
import { resumoService } from '../services/resumoService';
import { Categoria, TipoCategoria } from '../types/categoria';
import { Transacao, TipoTransacao } from '../types/transacao';

// Componente principal do Dashboard Financeiro
// Baseado nos testes unitários da API para garantir comunicação sem bugs
export const DashboardFinanceiro: React.FC = () => {
  // Estados baseados nos modelos testados
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [transacoes, setTransacoes] = useState<Transacao[]>([]);
  const [resumo, setResumo] = useState({
    totalDespesas: '0.00',
    totalReceitas: '0.00',
    saldo: '0.00'
  });

  // Estados de UI
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [mostrarFormTransacao, setMostrarFormTransacao] = useState(false);

  // Carregar dados iniciais
  useEffect(() => {
    carregarDados();
  }, []);

  const carregarDados = async () => {
    setLoading(true);
    setError(null);

    try {
      // Carregar categorias (testado: findAll_ShouldReturnAllCategorias)
      const categoriasData = await categoriaService.listarTodas();
      setCategorias(categoriasData);

      // Carregar transações (testado: findAll_ShouldReturnAllTransacoes)
      const transacoesData = await transacaoService.listarTodas();
      setTransacoes(transacoesData);

      // Carregar resumo financeiro
      const [despesas, receitas, saldo] = await Promise.all([
        resumoService.getTotalDespesas(),      // testado: getTotalDespesas_ShouldReturnTotalDespesas
        resumoService.getTotalReceitas(),      // testado: getTotalReceitas_ShouldReturnTotalReceitas
        resumoService.getSaldoMensal()        // testado: getSaldoMensal_ShouldReturnSaldoMensal
      ]);

      setResumo({
        totalDespesas: despesas,
        totalReceitas: receitas,
        saldo: saldo
      });

    } catch (err: any) {
      // Tratamento de erro baseado nos testes de API
      if (err.status === 500) {
        setError('Erro interno do servidor. Tente novamente mais tarde.');
      } else if (err.status === 403) {
        setError('Acesso negado. Verifique suas permissões.');
      } else {
        setError('Erro ao carregar dados. Tente novamente.');
      }
      console.error('Dashboard Error:', err);
    } finally {
      setLoading(false);
    }
  };

  // Formulário de nova transação
  const [formTransacao, setFormTransacao] = useState({
    descricao: '',
    valor: '',
    data: new Date().toISOString().split('T')[0], // Formato YYYY-MM-DD
    tipo: TipoTransacao.DESPESA,
    recorrente: false,
    parcelas: 1,
    parcelaAtual: 1,
    categoriaId: 0,
    observacoes: ''
  });

  // Validação baseada nos testes do backend
  const validarFormulario = (): string[] => {
    const erros: string[] = [];

    // Validações testadas em create_ShouldReturnBadRequest_WhenInvalid
    if (!formTransacao.descricao.trim()) {
      erros.push('Descrição é obrigatória');
    }

    if (!formTransacao.valor || parseFloat(formTransacao.valor) <= 0) {
      erros.push('Valor deve ser positivo');
    }

    if (!formTransacao.data) {
      erros.push('Data é obrigatória');
    }

    if (formTransacao.categoriaId === 0) {
      erros.push('Categoria é obrigatória');
    }

    if (formTransacao.parcelas < 1) {
      erros.push('Número de parcelas deve ser maior que zero');
    }

    return erros;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const erros = validarFormulario();
    if (erros.length > 0) {
      setError(erros.join('; '));
      return;
    }

    try {
      // Criar transação (testado: create_ShouldCreateTransacao_WhenValid)
      await transacaoService.criar(formTransacao);
      
      // Resetar formulário
      setFormTransacao({
        descricao: '',
        valor: '',
        data: new Date().toISOString().split('T')[0],
        tipo: TipoTransacao.DESPESA,
        recorrente: false,
        parcelas: 1,
        parcelaAtual: 1,
        categoriaId: 0,
        observacoes: ''
      });

      setMostrarFormTransacao(false);
      
      // Recarregar dados
      carregarDados();

    } catch (err: any) {
      // Tratamento baseado nos testes de erro da API
      if (err.status === 400) {
        setError('Dados inválidos. Verifique os campos obrigatórios.');
      } else if (err.status === 404) {
        setError('Categoria não encontrada.');
      } else {
        setError('Erro ao salvar transação. Tente novamente.');
      }
    }
  };

  // Renderização condicional baseada nos estados
  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-lg">Carregando dados financeiros...</div>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-8">Dashboard Financeiro</h1>

      {/* Mensagens de erro */}
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-6">
          <strong>Erro:</strong> {error}
          <button 
            onClick={() => setError(null)}
            className="float-right text-red-500 hover:text-red-700"
          >
            ×
          </button>
        </div>
      )}

      {/* Cards de Resumo Financeiro */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-green-100 p-6 rounded-lg">
          <h3 className="text-lg font-semibold text-green-800">Receitas do Mês</h3>
          <p className="text-2xl font-bold text-green-600">
            R$ {parseFloat(resumo.totalReceitas).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
          </p>
        </div>

        <div className="bg-red-100 p-6 rounded-lg">
          <h3 className="text-lg font-semibold text-red-800">Despesas do Mês</h3>
          <p className="text-2xl font-bold text-red-600">
            R$ {parseFloat(resumo.totalDespesas).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
          </p>
        </div>

        <div className={`p-6 rounded-lg ${parseFloat(resumo.saldo) >= 0 ? 'bg-blue-100' : 'bg-orange-100'}`}>
          <h3 className={`text-lg font-semibold ${parseFloat(resumo.saldo) >= 0 ? 'text-blue-800' : 'text-orange-800'}`}>
            Saldo Mensal
          </h3>
          <p className={`text-2xl font-bold ${parseFloat(resumo.saldo) >= 0 ? 'text-blue-600' : 'text-orange-600'}`}>
            R$ {parseFloat(resumo.saldo).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
          </p>
        </div>
      </div>

      {/* Botão de nova transação */}
      <div className="mb-6">
        <button
          onClick={() => setMostrarFormTransacao(true)}
          className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded"
        >
          + Nova Transação
        </button>
      </div>

      {/* Formulário de Nova Transação */}
      {mostrarFormTransacao && (
        <div className="bg-white p-6 rounded-lg shadow-lg mb-8">
          <h2 className="text-xl font-semibold mb-4">Nova Transação</h2>
          
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Descrição *
                </label>
                <input
                  type="text"
                  value={formTransacao.descricao}
                  onChange={(e) => setFormTransacao({...formTransacao, descricao: e.target.value})}
                  className="w-full p-2 border rounded"
                  placeholder="Ex: Salário, Supermercado"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Valor *
                </label>
                <input
                  type="number"
                  step="0.01"
                  min="0.01"
                  value={formTransacao.valor}
                  onChange={(e) => setFormTransacao({...formTransacao, valor: e.target.value})}
                  className="w-full p-2 border rounded"
                  placeholder="0.00"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Data *
                </label>
                <input
                  type="date"
                  value={formTransacao.data}
                  onChange={(e) => setFormTransacao({...formTransacao, data: e.target.value})}
                  className="w-full p-2 border rounded"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tipo *
                </label>
                <select
                  value={formTransacao.tipo}
                  onChange={(e) => setFormTransacao({...formTransacao, tipo: e.target.value as TipoTransacao})}
                  className="w-full p-2 border rounded"
                  required
                >
                  <option value={TipoTransacao.RECEITA}>Receita</option>
                  <option value={TipoTransacao.DESPESA}>Despesa</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Categoria *
                </label>
                <select
                  value={formTransacao.categoriaId}
                  onChange={(e) => setFormTransacao({...formTransacao, categoriaId: parseInt(e.target.value)})}
                  className="w-full p-2 border rounded"
                  required
                >
                  <option value={0}>Selecione...</option>
                  {categorias.map(categoria => (
                    <option key={categoria.id} value={categoria.id}>
                      {categoria.nome} ({categoria.tipo})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Parcelas
                </label>
                <input
                  type="number"
                  min="1"
                  value={formTransacao.parcelas}
                  onChange={(e) => setFormTransacao({...formTransacao, parcelas: parseInt(e.target.value)})}
                  className="w-full p-2 border rounded"
                />
              </div>
            </div>

            <div className="flex items-center space-x-4">
              <label className="flex items-center">
                <input
                  type="checkbox"
                  checked={formTransacao.recorrente}
                  onChange={(e) => setFormTransacao({...formTransacao, recorrente: e.target.checked})}
                  className="mr-2"
                />
                Recorrente
              </label>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Observações
              </label>
              <textarea
                value={formTransacao.observacoes}
                onChange={(e) => setFormTransacao({...formTransacao, observacoes: e.target.value})}
                className="w-full p-2 border rounded"
                rows={3}
                placeholder="Informações adicionais..."
              />
            </div>

            <div className="flex space-x-4">
              <button
                type="submit"
                className="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-6 rounded"
              >
                Salvar Transação
              </button>
              <button
                type="button"
                onClick={() => setMostrarFormTransacao(false)}
                className="bg-gray-500 hover:bg-gray-600 text-white font-bold py-2 px-6 rounded"
              >
                Cancelar
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Lista de Transações Recentes */}
      <div className="bg-white rounded-lg shadow">
        <h2 className="text-xl font-semibold p-6 border-b">Transações Recentes</h2>
        
        {transacoes.length === 0 ? (
          <div className="p-6 text-center text-gray-500">
            Nenhuma transação encontrada. Crie sua primeira transação!
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Data
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Descrição
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Categoria
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Valor
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Tipo
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {transacoes.slice(0, 10).map(transacao => (
                  <tr key={transacao.id}>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {new Date(transacao.data).toLocaleDateString('pt-BR')}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {transacao.descricao}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {transacao.categoria.nome}
                    </td>
                    <td className={`px-6 py-4 whitespace-nowrap text-sm font-medium ${
                      transacao.tipo === TipoTransacao.RECEITA ? 'text-green-600' : 'text-red-600'
                    }`}>
                      {transacao.tipo === TipoTransacao.RECEITA ? '+' : '-'}
                      R$ {parseFloat(transacao.valor).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                        transacao.tipo === TipoTransacao.RECEITA 
                          ? 'bg-green-100 text-green-800' 
                          : 'bg-red-100 text-red-800'
                      }`}>
                        {transacao.tipo}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default DashboardFinanceiro;
