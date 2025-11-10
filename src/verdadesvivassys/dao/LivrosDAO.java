package verdadesvivassys.dao;

import verdadesvivassys.connection.DatabaseConnection;
import verdadesvivassys.model.Livro;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LivrosDAO {

    private final DatabaseConnection db = new DatabaseConnection();

    // Cria a tabela automaticamente se não existir
    public LivrosDAO() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Livro (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo TEXT NOT NULL UNIQUE,
                nome TEXT NOT NULL,
                valor REAL NOT NULL
            );
        """;
        try {
            db.executeUpdate(sql);
        } catch (Exception e) {
            System.out.println("Erro ao criar tabela Livro: " + e.getMessage());
        }
    }

    // Inserir livro
    public boolean insertLivro(Livro livro) {
        String sql = "INSERT INTO Livro (codigo, nome, valor) VALUES (?, ?, ?)";

        try (var conn = DatabaseConnection.connect(); var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, livro.getCodigo());
            stmt.setString(2, livro.getNome());
            stmt.setFloat(3, livro.getValor());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir Livro: " + e.getMessage());
            return false;
        }
    }

    // Atualizar livro
    public boolean updateLivro(Livro livro) {
        String sql = "UPDATE Livro SET codigo = ?, nome = ?, valor = ? WHERE id = ?";
        try (var conn = DatabaseConnection.connect(); var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, livro.getCodigo());
            stmt.setString(2, livro.getNome());
            stmt.setFloat(3, livro.getValor());
            stmt.setInt(4, livro.getId());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Livro: " + e.getMessage());
            return false;
        }
    }

    // Deletar livro
    public boolean deleteLivro(int id) {
        String sql = "DELETE FROM Livro WHERE id = ?";
        try (var conn = DatabaseConnection.connect(); var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao deletar Livro: " + e.getMessage());
            return false;
        }
    }

    // Buscar todos os livros
    public List<Livro> getAllLivros() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT * FROM Livro";

        try (var conn = DatabaseConnection.connect(); var stmt = conn.createStatement(); var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                livros.add(new Livro(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nome"),
                        rs.getFloat("valor")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar Livros: " + e.getMessage());
        }
        return livros;
    }

    // Buscar livro por ID
    public Livro getLivroById(int id) {
        String sql = "SELECT * FROM Livro WHERE id = ?";

        try (var conn = DatabaseConnection.connect(); var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                return new Livro(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nome"),
                        rs.getFloat("valor")
                );
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar Livro por ID: " + e.getMessage());
        }
        return null;
    }

    // Buscar livro por código
    public Livro getByCodigo(String codigo) {
        String sql = "SELECT * FROM Livro WHERE codigo = ?";

        try (var conn = DatabaseConnection.connect(); var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                return new Livro(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nome"),
                        rs.getFloat("valor")
                );
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar Livro por código: " + e.getMessage());
        }
        return null;
    }

    // Buscar livro por nome
    public Livro getByNome(String nome) {
        String sql = "SELECT * FROM Livro WHERE nome = ?";

        try (var conn = DatabaseConnection.connect(); var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                return new Livro(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nome"),
                        rs.getFloat("valor")
                );
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar Livro por nome: " + e.getMessage());
        }
        return null;
    }
}

