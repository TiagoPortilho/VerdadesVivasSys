package verdadesvivassys.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:verdadesvivas.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Conexão estabelecida com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
        return conn;
    }

    public String executeQueryString(String sql, String column) throws SQLException {
        try (Connection conn = connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getString(column);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao executar consulta: " + e.getMessage());
        }
        return null;
    }

    public double executeQueryDouble(String sql, String column) throws SQLException {
        try (Connection conn = connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(column);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao executar consulta: " + e.getMessage());
        }
        return 0;
    }
    
    public int executeQueryInt(String sql, String column) throws SQLException {
        try (Connection conn = connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(column);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao executar consulta (Int): " + e.getMessage());
        }
        return -1;
    }

    public int executeUpdate(String sql) throws SQLException {
        try (Connection conn = connect();
             var stmt = conn.createStatement()) {

            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao executar atualização: " + e.getMessage());
        }
        return -1;
    }

}
