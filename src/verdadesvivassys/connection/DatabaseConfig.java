package verdadesvivassys.connection;

import java.sql.*;

public class DatabaseConfig {
    private static final String URL = "jdbc:sqlite:verdadesvivas.db";

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            // 🧱 Cria tabela Cliente
            String createClienteTable = """
                CREATE TABLE IF NOT EXISTS Cliente (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    Nome TEXT NOT NULL,
                    Cidade TEXT NOT NULL,
                    Contato TEXT NOT NULL
                );
            """;
            stmt.execute(createClienteTable);

            // 📚 Cria tabela Livro
            String createLivroTable = """
                CREATE TABLE IF NOT EXISTS Livro (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    codigo TEXT,
                    Nome TEXT NOT NULL,
                    Valor REAL NOT NULL
                );
            """;
            stmt.execute(createLivroTable);

            // 💰 Cria tabela Venda
            String createVendaTable = """
                CREATE TABLE IF NOT EXISTS Venda (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cliente_id INTEGER NOT NULL,
                    total REAL NOT NULL DEFAULT 0,
                    FOREIGN KEY(cliente_id) REFERENCES Cliente(id)
                );
            """;
            stmt.execute(createVendaTable);

            // 🔗 Cria tabela intermediária Venda_Livro (N:N)
            String createVendaLivroTable = """
                CREATE TABLE IF NOT EXISTS Venda_Livro (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    venda_id INTEGER NOT NULL,
                    livro_id INTEGER NOT NULL,
                    quantidade INTEGER NOT NULL DEFAULT 1,
                    FOREIGN KEY (venda_id) REFERENCES Venda(id) ON DELETE CASCADE,
                    FOREIGN KEY (livro_id) REFERENCES Livro(id)
                );
            """;
            stmt.execute(createVendaLivroTable);

            System.out.println("✅ Tabelas criadas/atualizadas com sucesso.");

            // 📦 Verifica se os livros já existem
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM Livro;");
            int total = rs.getInt("total");

            if (total == 0) {
                System.out.println("📚 Inserindo livros no banco...");
                String insertLivros = """
                    INSERT INTO Livro (codigo, Nome, Valor) VALUES
                    ('B01','Romanos - (Brochura)',39.6),
                    ('B02','1 Coríntios',22.7),
                    ('B03','2 Coríntios',20.3),
                    ('B04','Gálatas',18.2),
                    ('B05','Efésios',18.2),
                    ('B06','Filipenses',18.2),
                    ('B07','Colossenses',16.8),
                    ('B08','1 e 2 Tessalonissences',18.8),
                    ('B09','1 Timóteo',18.8),
                    ('B10','2 Timóteo',16.8),
                    ('B11','Tito e Filemom',15.9),
                    ('B12','Hebreus',30.8),
                    ('B13','Tiago',17.6),
                    ('B14','1 e 2 Pedro',24.2),
                    ('B15','João e Judas',23.6),
                    ('B16','Apocalipse',22),
                    ('B21','A Ordem de Deus',35),
                    ('B22','Um Só Corpo na Prática',23.1),
                    ('B23','A Procura',33),
                    ('B24','Acontecimentos Proféticos - (Brochura)',27.5),
                    ('B25','Os Irmãos Abertos',19.8),
                    ('B26','Perguntas que os Jovens Fazem - V 1',18.7),
                    ('B27','Perguntas que os Jovens Fazem - V 2',18.1),
                    ('B50','Provérbios',17),
                    ('B51','A Transição - João',7.1),
                    ('B52','As Coisas que Servem para a Paz',7.7),
                    ('B53','Propósito das Provações',10.8),
                    ('B54','Eterna Segurança do Crente',7.7),
                    ('B55','A Lacuna',5.5),
                    ('B56','A Morte, o Estado Eterno...',7.7),
                    ('B57','A Importância de Ter um Espírito Correto',7.7),
                    ('B58','Doze Homens de Paulo',6),
                    ('B59','Como Deus nos Torna Dispostos',5.5),
                    ('B60','Lições da Vida de Abraão e de Ló',4.4),
                    ('B61','Como Saber a Vontade de Deus',13.2),
                    ('B62','A Presença e Poder do Espírito Santo',6),
                    ('B90','Romanos - (Espiral)',20),
                    ('CBO','Calendário de Bolso (50 uni)',1),
                    ('CME','Calendário de Mesa',25),
                    ('CMM','Mini Cal. Mesa - 4 pág COM 10',5),
                    ('CP4','Calendário de Parede - 4 pág.',1),
                    ('CPA','Calendário de Parede - 12 pág.',1),
                    ('P01','Pacote Econômico com 100 folhetos',5),
                    ('EC1','Pacote Econômico com 1000 folhetos',43.5),
                    ('EC4','Pacote Econômico com 4000 folhetos',165),
                    ('EC8','Pacote Econômico com 8000 folhetos',320),
                    ('F80','A Igreja de Deus nos dias de hoje- pct c/ 10 unidades',3.2),
                    ('F81','Naamã, O leproso - pct c/ 10 unidades',3.2),
                    ('H01','Mateus',20.9),
                    ('H02','Marcos',18.1),
                    ('H03','Lucas',18.7),
                    ('H04','João',27.5),
                    ('H05','Atos dos Apóstolos',19.8),
                    ('H06','O Livro de Jó',16.5),
                    ('HIN','Hinário VV',16),
                    ('N01','Breves Esboços dos Livros da Bíblia',23.1),
                    ('N02','Prosseguindo para o Alvo',24.2),
                    ('N03','Para Que Te Vá Bem',18.7),
                    ('N04','Duas Naturezas - N. Simon',8),
                    ('N05','Justificação - N. Simon',6.1),
                    ('N06','Espírito Santo - N. Simon',8),
                    ('N07','Eleição - N. Simon',9),
                    ('N08','A Lei - N. Simon',6.1),
                    ('V01','A ceia do Senhor',4.6),
                    ('V02','Cristo, o Centro',3.3),
                    ('V03','Cristo para meus pecados, Cristo para meus cuidados',3),
                    ('V04','Cristo, A Sabedoria de Deus para o caminho do crente',3.7),
                    ('V06','Os Dez Mandamentos',3.7),
                    ('V07','Um Deus Justo e Salvador',2.8),
                    ('V09','O Espírito Santo',2.6),
                    ('V11','A Humanidade de Cristo',2.8),
                    ('V12','A Humanidade sem Pecado do Senhor Jesus Cristo',2.4),
                    ('V14','O livro de Ester',3.9),
                    ('V15','Línguas e outros Dons de Sinais',3),
                    ('V16','A Mulher: Seu lugar nas Escrituras',3.3),
                    ('V19','A Oração e o Mundo Invisível',3.9),
                    ('V20','Os Sofrimentos e as Glórias de Cristo - Salmo 22',2.8),
                    ('V21','Por Causa dos Anjos',2.6),
                    ('V22','Parábolas de Mateus 13',4.6),
                    ('V25','Resumo Profético',2.6),
                    ('V26','Santificação',3.5),
                    ('V27','Segurança, Certeza e Gozo',3.5),
                    ('V28','Sucesso!',2.8),
                    ('V29','Televisão',2.6),
                    ('V32','Como educar a criança?',3.7),
                    ('V33','Mefibosete - A Bondade de Deus',3.3),
                    ('V34','Espere!',3.5),
                    ('V35','As Três Manifestações de Cristo',3.9),
                    ('V36','O Velho Homem, O Novo Homem, o Eu',3.9),
                    ('V37','Porque Tivemos nossa Casa Batizada',2.7),
                    ('V70','Luz para as Almas Ansiosas',12.3),
                    ('V71','Carta aos Evangelistas',12.8),
                    ('V72','A Obra do Evangelho',14.3),
                    ('V73','O Caminho de Deus para Descanso',9),
                    ('D01','definições doutrinais',40),
                    ('HIC','HINARIO CRIANÇA',6),
                    ('PCP','cinco cartas - pizi',35),
                    ('PM1','Profetas menores - 1 Reis',37.5),
                    ('PM2','Profetas menores - 2 Reis',46.5),
                    ('PM3','Profetas menores - Profeta volume 1',25),
                    ('PMC','Profetas menores - Crescimento Cristão',9),
                    ('PMR','Profetas menores - Rute',9),
                    ('PME','Profetas menores - Ester',6.5),
                    ('HNM','Hinário novo capa mole',15),
                    ('HND','Hinário novo capa dura',28),
                    ('EGL','Evangelho de Lucas',38),
                    ('CFP','Cuidado Fraterno e ofença pessoal',2.5),
                    ('DIN','Os dias iniciais',5),
                    ('CES','O que é a ceia do Senhor',3),
                    ('GRA','GRAÇA',10),
                    ('TPA','TEOLOGIA DO PACTO',40.5);
                """;
                stmt.execute(insertLivros);
                System.out.println("✅ Livros inseridos com sucesso!");
            } else {
                System.out.println("📘 Livros já existem no banco. Inserção ignorada.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao inicializar o banco de dados: " + e.getMessage());
        }
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
