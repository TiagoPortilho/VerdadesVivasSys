package verdadesvivassys.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import verdadesvivassys.connection.DatabaseConnection;
import verdadesvivassys.model.Cliente;
import verdadesvivassys.model.Livro;
import verdadesvivassys.model.Venda;

public class VendasDAO {

    /**
     * Insere uma venda e os vínculos (Venda_Livro) em transação. Retorna true
     * se tudo ocorreu bem.
     */
    public boolean addVenda(Venda venda) {
        String sqlVenda = "INSERT INTO Venda (cliente_id, total) VALUES (?, ?)";
        String sqlVendaLivro = "INSERT INTO Venda_Livro (venda_id, livro_id, quantidade) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {

                int clienteId = venda.getCliente() != null ? venda.getCliente().getId() : -1;
                psVenda.setInt(1, clienteId);
                psVenda.setFloat(2, venda.getTotal());
                psVenda.executeUpdate();

                int vendaId = -1;
                try (ResultSet rs = psVenda.getGeneratedKeys()) {
                    if (rs.next()) {
                        vendaId = rs.getInt(1);
                    }
                }

                try (PreparedStatement psVL = conn.prepareStatement(sqlVendaLivro)) {
                    for (Livro l : venda.getLivros()) {
                        psVL.setInt(1, vendaId);
                        psVL.setInt(2, l.getId());
                        psVL.setInt(3, l.getQuantidade());
                        psVL.addBatch();
                    }
                    psVL.executeBatch();
                }

                conn.commit();
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                System.out.println("Erro ao inserir venda (rollback): " + ex.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            System.out.println("Erro de conexão ao inserir venda: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Busca todas as vendas (id, nome do cliente, nomes dos livros concatenados
     * e total) — agora retornando também a lista de livros com quantidade.
     */
    public List<Venda> getAllVendas() {
        List<Venda> vendas = new ArrayList<>();

        String sql = """
        SELECT 
            v.id AS venda_id, 
            v.total,
            c.id AS cliente_id, 
            c.Nome AS cliente_nome,

            -- Texto para exibir
            GROUP_CONCAT(l.Nome || ' (x' || vl.quantidade || ')', ', ') AS livros,

            -- Meta compacta (id::nome::quantidade)||(...)
            GROUP_CONCAT(
                l.id || '::' || REPLACE(l.Nome, '::', ' ') || '::' || vl.quantidade,
                '||'
            ) AS livros_meta

        FROM Venda v
        JOIN Cliente c ON v.cliente_id = c.id
        JOIN Venda_Livro vl ON v.id = vl.venda_id
        JOIN Livro l ON vl.livro_id = l.id
        GROUP BY v.id
        ORDER BY v.id DESC
        """;

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Venda venda = new Venda();
                venda.setId(rs.getInt("venda_id"));
                venda.setTotal(rs.getFloat("total"));

                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("cliente_id"));
                cliente.setNome(rs.getString("cliente_nome"));
                venda.setCliente(cliente);

                // monta lista de livros
                List<Livro> livros = new ArrayList<>();
                String meta = rs.getString("livros_meta");

                if (meta != null && !meta.isBlank()) {
                    for (String item : meta.split("\\|\\|")) {

                        String[] p = item.split("::", 3);
                        if (p.length == 3) {
                            Livro l = new Livro();
                            l.setId(Integer.parseInt(p[0]));
                            l.setNome(p[1]);
                            l.setQuantidade(Integer.parseInt(p[2]));
                            livros.add(l);
                        }
                    }
                }

                venda.setLivros(livros);
                vendas.add(venda);
            }

        } catch (SQLException ex) {
            System.out.println("Erro ao buscar vendas: " + ex.getMessage());
        }

        return vendas;
    }

    /**
     * Remove uma venda e seus vínculos (Venda_Livro) dentro de transação.
     */
    public boolean deleteVenda(int vendaId) {
        String sqlDeleteVL = "DELETE FROM Venda_Livro WHERE venda_id = ?";
        String sqlDeleteV = "DELETE FROM Venda WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlDeleteVL); PreparedStatement ps2 = conn.prepareStatement(sqlDeleteV)) {

                ps1.setInt(1, vendaId);
                ps1.executeUpdate();

                ps2.setInt(1, vendaId);
                ps2.executeUpdate();

                conn.commit();
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                System.out.println("Erro ao deletar venda (rollback): " + ex.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            System.out.println("Erro de conexão ao deletar venda: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Atualiza apenas o total de uma venda.
     */
    public boolean updateTotalVenda(int vendaId, float novoTotal) {
        String sql = "UPDATE Venda SET total = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setFloat(1, novoTotal);
            ps.setInt(2, vendaId);
            ps.executeUpdate();
            return true;

        } catch (SQLException ex) {
            System.out.println("Erro ao atualizar total da venda: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Busca uma venda completa (com cliente e lista de livros) pelo ID.
     */
    public Venda getVendaById(int vendaId) {
        Venda venda = null;

        String sqlVenda = """
        SELECT 
            v.id AS venda_id, 
            v.total,
            c.id AS cliente_id, 
            c.Nome AS cliente_nome,
            c.Cidade AS cliente_cidade,
            c.Contato AS cliente_contato
        FROM Venda v
        JOIN Cliente c ON v.cliente_id = c.id
        WHERE v.id = ?
        """;

        String sqlLivros = """
        SELECT l.id, l.codigo, l.Nome, l.Valor, vl.quantidade
        FROM Venda_Livro vl
        JOIN Livro l ON vl.livro_id = l.id
        WHERE vl.venda_id = ?
        """;

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement psVenda = conn.prepareStatement(sqlVenda); PreparedStatement psLivros = conn.prepareStatement(sqlLivros)) {

            psVenda.setInt(1, vendaId);

            try (ResultSet rsVenda = psVenda.executeQuery()) {
                if (rsVenda.next()) {
                    venda = new Venda();
                    venda.setId(rsVenda.getInt("venda_id"));
                    venda.setTotal(rsVenda.getFloat("total"));

                    Cliente cliente = new Cliente();
                    cliente.setId(rsVenda.getInt("cliente_id"));
                    cliente.setNome(rsVenda.getString("cliente_nome"));
                    cliente.setCidade(rsVenda.getString("cliente_cidade"));
                    cliente.setContato(rsVenda.getString("cliente_contato"));
                    venda.setCliente(cliente);
                }
            }

            if (venda != null) {
                psLivros.setInt(1, vendaId);
                List<Livro> livros = new ArrayList<>();

                try (ResultSet rsLivros = psLivros.executeQuery()) {
                    while (rsLivros.next()) {
                        Livro livro = new Livro(
                                rsLivros.getInt("id"),
                                rsLivros.getString("codigo"),
                                rsLivros.getString("Nome"),
                                rsLivros.getFloat("Valor")
                        );

                        livro.setQuantidade(rsLivros.getInt("quantidade"));
                        livros.add(livro);
                    }
                }

                venda.setLivros(livros);
            }

        } catch (SQLException ex) {
            System.out.println("Erro ao buscar venda por ID: " + ex.getMessage());
        }

        return venda;
    }

    public boolean updateVenda(Venda venda) {
        String sqlDeleteVL = "DELETE FROM Venda_Livro WHERE venda_id = ?";
        String sqlUpdateV = "UPDATE Venda SET cliente_id = ?, total = ? WHERE id = ?";
        String sqlInsertVL = "INSERT INTO Venda_Livro (venda_id, livro_id, quantidade) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);

            // Atualiza venda
            try (PreparedStatement psV = conn.prepareStatement(sqlUpdateV)) {
                psV.setInt(1, venda.getCliente().getId());
                psV.setFloat(2, venda.getTotal());
                psV.setInt(3, venda.getId());
                psV.executeUpdate();
            }

            // Remove livros antigos
            try (PreparedStatement psDel = conn.prepareStatement(sqlDeleteVL)) {
                psDel.setInt(1, venda.getId());
                psDel.executeUpdate();
            }

            // Insere livros novos
            try (PreparedStatement psVL = conn.prepareStatement(sqlInsertVL)) {
                for (Livro l : venda.getLivros()) {
                    psVL.setInt(1, venda.getId());
                    psVL.setInt(2, l.getId());
                    psVL.setInt(3, l.getQuantidade());
                    psVL.addBatch();
                }
                psVL.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
