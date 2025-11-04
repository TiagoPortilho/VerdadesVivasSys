package verdadesvivassys.dao;

import verdadesvivassys.connection.DatabaseConnection;
import verdadesvivassys.model.Cliente;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientesDAO {

    private final DatabaseConnection db = new DatabaseConnection();

    // Cria a tabela automaticamente se não existir
    public ClientesDAO() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Cliente (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                Nome TEXT NOT NULL,
                Cidade TEXT NOT NULL,
                Contato TEXT NOT NULL
            );
        """;
        try {
            db.executeUpdate(sql);
        } catch (Exception e) {
            System.out.println("Erro ao criar tabela Cliente: " + e.getMessage());
        }
    }

    // Inserir cliente
    public boolean insertCliente(Cliente cliente) {
        String sql = "INSERT INTO Cliente (Nome, Cidade, Contato) VALUES (?, ?, ?)";

        try (var conn = DatabaseConnection.connect();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCidade());
            stmt.setString(3, cliente.getContato());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir Cliente: " + e.getMessage());
            return false;
        }
    }

    // Atualizar cliente
    public boolean updateCliente(Cliente cliente) {
        String sql = "UPDATE Cliente SET Nome = ?, Cidade = ?, Contato = ? WHERE id = ?";

        try (var conn = DatabaseConnection.connect();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCidade());
            stmt.setString(3, cliente.getContato());
            stmt.setInt(4, cliente.getId());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Cliente: " + e.getMessage());
            return false;
        }
    }

    // Deletar cliente
    public boolean deleteCliente(int id) {
        String sql = "DELETE FROM Cliente WHERE id = ?";

        try (var conn = DatabaseConnection.connect();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao deletar Cliente: " + e.getMessage());
            return false;
        }
    }

    // Buscar todos os clientes
    public List<Cliente> getAllClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Cliente ORDER BY Nome";

        try (var conn = DatabaseConnection.connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getInt("id"),
                        rs.getString("Nome"),
                        rs.getString("Cidade"),
                        rs.getString("Contato")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar Clientes: " + e.getMessage());
        }

        return clientes;
    }

    // Buscar cliente por ID
    public Cliente getClienteById(int id) {
        String sql = "SELECT * FROM Cliente WHERE id = ?";

        try (var conn = DatabaseConnection.connect();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                return new Cliente(
                        rs.getInt("id"),
                        rs.getString("Nome"),
                        rs.getString("Cidade"),
                        rs.getString("Contato")
                );
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar Cliente por ID: " + e.getMessage());
        }

        return null;
    }

    // Buscar cliente por nome
    public List<Cliente> getByNome(String nome) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Cliente WHERE Nome LIKE ?";

        try (var conn = DatabaseConnection.connect();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            var rs = stmt.executeQuery();

            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getInt("id"),
                        rs.getString("Nome"),
                        rs.getString("Cidade"),
                        rs.getString("Contato")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar Cliente por nome: " + e.getMessage());
        }

        return clientes;
    }

    // Buscar cliente por cidade
    public List<Cliente> getByCidade(String cidade) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Cliente WHERE Cidade LIKE ?";

        try (var conn = DatabaseConnection.connect();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + cidade + "%");
            var rs = stmt.executeQuery();

            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getInt("id"),
                        rs.getString("Nome"),
                        rs.getString("Cidade"),
                        rs.getString("Contato")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar Cliente por cidade: " + e.getMessage());
        }

        return clientes;
    }
}
