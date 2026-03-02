package verdadesvivassys.views;

import verdadesvivassys.views.venda.VendaForm;
import verdadesvivassys.views.livro.LivroForm;
import verdadesvivassys.views.cliente.ClienteForm;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import verdadesvivassys.connection.DatabaseConfig;
import verdadesvivassys.dao.ClientesDAO;
import verdadesvivassys.dao.DAOFactory;
import verdadesvivassys.dao.LivrosDAO;
import verdadesvivassys.dao.VendasDAO;
import verdadesvivassys.model.Cliente;
import verdadesvivassys.model.Livro;
import verdadesvivassys.model.Venda;
import verdadesvivassys.views.cliente.ClienteDetail;
import verdadesvivassys.views.livro.LivroDetail;
import verdadesvivassys.views.venda.VendaDetail;

public class Menu extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Menu.class.getName());
    private static final String DATABASE_FILE_NAME = "verdadesvivas.db";
    private static final String DEFAULT_GITHUB_REPOSITORY = "TiagoPortilho/VerdadesVivasSys";
    private static final Pattern RELEASE_TAG_PATTERN = Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern JAR_DOWNLOAD_PATTERN = Pattern.compile("\"browser_download_url\"\\s*:\\s*\"([^\"]+\\.jar)\"");

    private LivrosDAO livrosDAO = DAOFactory.getLivrosDAO();
    private ClientesDAO clientesDAO = DAOFactory.getClientesDAO();
    private VendasDAO vendasDAO = DAOFactory.getVendasDAO();

    public Menu() {
        new DatabaseConfig().initialize();
        initComponents();
        setResizable(false);

        carregarLivrosNaTabela();
        carregarClientesNaTabela();
        carregarVendasNaTabela();

        loadClientes();
        loadLivros();
        loadCidades();

        AutoCompleteDecorator.decorate(cmbCliente);
        AutoCompleteDecorator.decorate(cmbLivros);
        AutoCompleteDecorator.decorate(cmbCidades);
        AutoCompleteDecorator.decorate(cmbClientes2);
        AutoCompleteDecorator.decorate(cmbCidades2);
        AutoCompleteDecorator.decorate(cmbLivros);
        AutoCompleteDecorator.decorate(cmbNomeLivro);
        AutoCompleteDecorator.decorate(cmbCodigoLivro);

        this.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                loadClientes();
                loadLivros();
                loadCidades();
                
                carregarLivrosNaTabela();
                carregarClientesNaTabela();
                carregarVendasNaTabela();
                
            }

            @Override
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                // nada a fazer
            }
        });
    }

    // =================**LIVROS**=================
    private void carregarLivrosNaTabela() {
        List<Livro> livros = livrosDAO.getAllLivros();

        DefaultTableModel model = (DefaultTableModel) tblLivros.getModel();
        model.setRowCount(0); // limpa as linhas atuais

        for (Livro livro : livros) {
            model.addRow(new Object[]{
                livro.getId(),
                livro.getCodigo(),
                livro.getNome(),
                livro.getValor()
            });
        }
    }

    private int getIdLivroSelecionado() {
        int row = tblLivros.getSelectedRow();
        if (row < 0) {
            return -1;
        }
        return (int) tblLivros.getValueAt(row, 0);
    }

    private void loadLivros() {
        cmbLivros.removeAllItems();
        cmbNomeLivro.removeAllItems();
        cmbCodigoLivro.removeAllItems();

        for (Livro l : livrosDAO.getAllLivros()) {
            if (l != null) {
                if (l.getNome() != null && !l.getNome().isBlank()) {
                    cmbLivros.addItem(l.getNome());
                    cmbNomeLivro.addItem(l.getNome());
                }
                if (l.getCodigo() != null && !l.getCodigo().isBlank()) {
                    cmbCodigoLivro.addItem(l.getCodigo());
                }
            }
        }

        AutoCompleteDecorator.decorate(cmbLivros);
        AutoCompleteDecorator.decorate(cmbNomeLivro);
        AutoCompleteDecorator.decorate(cmbCodigoLivro);

        cmbLivros.setSelectedIndex(-1);
        cmbNomeLivro.setSelectedIndex(-1);
        cmbCodigoLivro.setSelectedIndex(-1);
    }

    // =================**Clientes**=================
    private void carregarClientesNaTabela() {
        List<Cliente> clientes = clientesDAO.getAllClientes();

        DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();
        model.setRowCount(0);

        for (Cliente cliente : clientes) {
            model.addRow(new Object[]{
                cliente.getId(),
                cliente.getNome(),
                cliente.getCidade(),
                cliente.getContato()
            });
        }
    }

    private int getIdClienteSelecionado() {
        int row = tblClientes.getSelectedRow();
        if (row < 0) {
            return -1;
        }
        return (int) tblClientes.getValueAt(row, 0);
    }

    private void loadClientes() {
        cmbCliente.removeAllItems();
        cmbClientes2.removeAllItems(); // novo

        for (Cliente c : clientesDAO.getAllClientes()) {
            if (c != null && c.getNome() != null && !c.getNome().isBlank()) {
                cmbCliente.addItem(c.getNome());
                cmbClientes2.addItem(c.getNome()); // adiciona no segundo combo também
            }
        }

        // aplicar autocomplete nos dois
        AutoCompleteDecorator.decorate(cmbCliente);
        AutoCompleteDecorator.decorate(cmbClientes2);

        cmbCliente.setSelectedIndex(-1);
        cmbClientes2.setSelectedIndex(-1);
    }

    private void loadCidades() {
        cmbCidades.removeAllItems();
        cmbCidades2.removeAllItems();

        List<String> cidades = clientesDAO.getAllCidades().stream()
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());

        cidades.forEach(c -> {
            cmbCidades.addItem(c);
            cmbCidades2.addItem(c);
        });

        AutoCompleteDecorator.decorate(cmbCidades);
        AutoCompleteDecorator.decorate(cmbCidades2);

        cmbCidades.setSelectedIndex(-1);
        cmbCidades2.setSelectedIndex(-1);
    }

    // =================**Vendas**=================
    private void carregarVendasNaTabela() {
        List<Venda> vendas = vendasDAO.getAllVendas();

        DefaultTableModel model = (DefaultTableModel) tblVendas.getModel();
        model.setRowCount(0); // limpa as linhas

        for (Venda venda : vendas) {
            String nomeCliente = venda.getCliente() != null ? venda.getCliente().getNome() : "—";

            // monta nomes com somatório de quantidade por livro
            String nomesLivrosFormatados = "—";

            if (venda.getLivros() != null && !venda.getLivros().isEmpty()) {
                // soma quantidade por nome (caso o DAO já retorne quantidade)
                Map<String, Integer> mapaQtd = venda.getLivros().stream()
                        .collect(Collectors.toMap(
                                Livro::getNome,
                                l -> l.getQuantidade(), // valor inicial
                                Integer::sum // se tiver mesmo nome, soma
                        ));

                nomesLivrosFormatados = mapaQtd.entrySet().stream()
                        .map(e -> {
                            String nome = e.getKey();
                            int qtd = e.getValue();
                            return qtd > 1 ? nome + " (x" + qtd + ")" : nome;
                        })
                        .collect(Collectors.joining(", "));
            }

            model.addRow(new Object[]{
                venda.getId(),
                nomeCliente,
                nomesLivrosFormatados,
                String.format("R$ %.2f", venda.getTotal())
            });
        }
    }

    private int getIdVendaSelecionada() {
        int row = tblVendas.getSelectedRow();
        if (row < 0) {
            return -1;
        }
        return (int) tblVendas.getValueAt(row, 0);
    }

    private Path getCurrentDatabasePath() {
        return Path.of(DATABASE_FILE_NAME).toAbsolutePath().normalize();
    }

    private String buildJdbcUrl(Path dbPath) {
        return "jdbc:sqlite:" + dbPath.toAbsolutePath().normalize();
    }

    private Path withExtension(Path path, String extension) {
        String fileName = path.getFileName().toString();
        if (fileName.toLowerCase(Locale.ROOT).endsWith(extension)) {
            return path;
        }
        Path parent = path.getParent();
        String newName = fileName + extension;
        return parent == null ? Path.of(newName) : parent.resolve(newName);
    }

    private String timestampSuffix() {
        return DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
    }

    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT 1 FROM sqlite_master WHERE type = 'table' AND lower(name) = lower(?) LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        String sql = "PRAGMA table_info(" + tableName + ")";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isBlank(String text) {
        return text == null || text.isBlank();
    }

    private void exportDatabaseCopy(Path destinationPath) throws IOException {
        Path sourceDbPath = getCurrentDatabasePath();
        if (!Files.exists(sourceDbPath)) {
            throw new IOException("Banco atual nao foi encontrado em: " + sourceDbPath);
        }

        Path normalizedDestination = destinationPath.toAbsolutePath().normalize();
        if (normalizedDestination.equals(sourceDbPath)) {
            throw new IOException("Escolha um destino diferente do banco atual.");
        }

        if (normalizedDestination.getParent() != null) {
            Files.createDirectories(normalizedDestination.getParent());
        }

        Files.copy(sourceDbPath, normalizedDestination, StandardCopyOption.REPLACE_EXISTING);
    }

    private String sanitizeSheetName(String name) {
        String sanitized = name.replaceAll("[\\\\/:*?\\[\\]]", "_");
        if (sanitized.length() > 31) {
            return sanitized.substring(0, 31);
        }
        return sanitized;
    }

    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private void writeCell(BufferedWriter writer, Object value, String styleId) throws IOException {
        String styleAttr = isBlank(styleId) ? "" : " ss:StyleID=\"" + styleId + "\"";
        if (value instanceof Number number) {
            writer.write("        <Cell" + styleAttr + "><Data ss:Type=\"Number\">");
            writer.write(escapeXml(number.toString()));
            writer.write("</Data></Cell>\n");
            return;
        }

        writer.write("        <Cell" + styleAttr + "><Data ss:Type=\"String\">");
        writer.write(escapeXml(value == null ? "" : value.toString()));
        writer.write("</Data></Cell>\n");
    }

    private void writeSheetFromQuery(BufferedWriter writer,
                                     Connection conn,
                                     String sheetName,
                                     String query,
                                     String[] headers,
                                     int[] columnWidths,
                                     Set<Integer> currencyColumns) throws SQLException, IOException {

        writer.write("  <Worksheet ss:Name=\"");
        writer.write(escapeXml(sanitizeSheetName(sheetName)));
        writer.write("\">\n");
        writer.write("    <Table>\n");

        if (columnWidths != null && columnWidths.length > 0) {
            for (int width : columnWidths) {
                writer.write("      <Column ss:AutoFitWidth=\"0\" ss:Width=\"" + Math.max(30, width) + "\"/>\n");
            }
        }

        writer.write("      <Row>\n");
        for (String header : headers) {
            writer.write("        <Cell ss:StyleID=\"Header\"><Data ss:Type=\"String\">");
            writer.write(escapeXml(header));
            writer.write("</Data></Cell>\n");
        }
        writer.write("      </Row>\n");

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                writer.write("      <Row>\n");
                for (int i = 1; i <= headers.length; i++) {
                    String style = (currencyColumns != null && currencyColumns.contains(i)) ? "Currency" : "Cell";
                    writeCell(writer, rs.getObject(i), style);
                }
                writer.write("      </Row>\n");
            }
        }

        writer.write("    </Table>\n");
        writer.write("  </Worksheet>\n");
    }

    private void exportDatabaseToExcel(Path destinationPath) throws IOException, SQLException {
        Path dbPath = getCurrentDatabasePath();
        if (!Files.exists(dbPath)) {
            throw new IOException("Banco atual nao foi encontrado em: " + dbPath);
        }

        Path normalizedDestination = destinationPath.toAbsolutePath().normalize();
        if (normalizedDestination.getParent() != null) {
            Files.createDirectories(normalizedDestination.getParent());
        }

        try (Connection conn = DriverManager.getConnection(buildJdbcUrl(dbPath));
             BufferedWriter writer = Files.newBufferedWriter(
                     normalizedDestination,
                     StandardCharsets.UTF_8,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING)) {

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<?mso-application progid=\"Excel.Sheet\"?>\n");
            writer.write("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n");
            writer.write(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n");
            writer.write(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n");
            writer.write(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">\n");
            writer.write(" <Styles>\n");
            writer.write("  <Style ss:ID=\"Header\">\n");
            writer.write("   <Font ss:Bold=\"1\" ss:Color=\"#FFFFFF\"/>\n");
            writer.write("   <Interior ss:Color=\"#1F4E78\" ss:Pattern=\"Solid\"/>\n");
            writer.write("   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n");
            writer.write("   <Borders>\n");
            writer.write("    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
            writer.write("    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
            writer.write("    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
            writer.write("    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
            writer.write("   </Borders>\n");
            writer.write("  </Style>\n");
            writer.write("  <Style ss:ID=\"Cell\">\n");
            writer.write("   <Alignment ss:Vertical=\"Center\" ss:WrapText=\"1\"/>\n");
            writer.write("   <Borders>\n");
            writer.write("    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
            writer.write("    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
            writer.write("    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
            writer.write("    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
            writer.write("   </Borders>\n");
            writer.write("  </Style>\n");
            writer.write("  <Style ss:ID=\"Currency\">\n");
            writer.write("   <Alignment ss:Horizontal=\"Right\" ss:Vertical=\"Center\"/>\n");
            writer.write("   <NumberFormat ss:Format=\"&quot;R$&quot; #,##0.00\"/>\n");
            writer.write("   <Borders>\n");
            writer.write("    <Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
            writer.write("    <Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
            writer.write("    <Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
            writer.write("    <Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/>\n");
            writer.write("   </Borders>\n");
            writer.write("  </Style>\n");
            writer.write(" </Styles>\n");

            writeSheetFromQuery(
                    writer,
                    conn,
                    "Clientes",
                    "SELECT id, Nome, Cidade, Contato FROM Cliente ORDER BY id ASC",
                    new String[]{"ID", "Nome", "Cidade", "Contato"},
                    new int[]{60, 220, 160, 180},
                    Set.of()
            );

            writeSheetFromQuery(
                    writer,
                    conn,
                    "Livros",
                    "SELECT id, codigo, Nome, Valor FROM Livro ORDER BY id ASC",
                    new String[]{"ID", "Codigo", "Nome", "Valor"},
                    new int[]{60, 90, 320, 100},
                    Set.of(4)
            );

            boolean hasVendaLivroTable = tableExists(conn, "Venda_Livro");
            boolean hasLivroTable = tableExists(conn, "Livro");
            boolean hasQuantidadeColumn = hasVendaLivroTable && columnExists(conn, "Venda_Livro", "quantidade");

            String vendasQuery;
            if (hasVendaLivroTable && hasLivroTable) {
                vendasQuery = hasQuantidadeColumn
                        ? """
                          SELECT
                              v.id,
                              c.Nome AS cliente,
                              c.Cidade AS cidade,
                              COALESCE(
                                  GROUP_CONCAT(
                                      CASE
                                          WHEN l.Nome IS NULL THEN NULL
                                          WHEN vl.quantidade > 1 THEN l.Nome || ' (x' || vl.quantidade || ')'
                                          ELSE l.Nome
                                      END,
                                      ', '
                                  ),
                                  ''
                              ) AS livros,
                              CASE
                                  WHEN v.total > 0 THEN v.total
                                  ELSE COALESCE(SUM(vl.quantidade * l.Valor), 0)
                              END AS total
                          FROM Venda v
                          JOIN Cliente c ON c.id = v.cliente_id
                          LEFT JOIN Venda_Livro vl ON vl.venda_id = v.id
                          LEFT JOIN Livro l ON l.id = vl.livro_id
                          GROUP BY v.id, c.Nome, c.Cidade, v.total
                          ORDER BY v.id ASC
                          """
                        : """
                          SELECT
                              v.id,
                              c.Nome AS cliente,
                              c.Cidade AS cidade,
                              COALESCE(GROUP_CONCAT(l.Nome, ', '), '') AS livros,
                              CASE
                                  WHEN v.total > 0 THEN v.total
                                  ELSE COALESCE(SUM(l.Valor), 0)
                              END AS total
                          FROM Venda v
                          JOIN Cliente c ON c.id = v.cliente_id
                          LEFT JOIN Venda_Livro vl ON vl.venda_id = v.id
                          LEFT JOIN Livro l ON l.id = vl.livro_id
                          GROUP BY v.id, c.Nome, c.Cidade, v.total
                          ORDER BY v.id ASC
                          """;
            } else {
                vendasQuery = """
                        SELECT
                            v.id,
                            c.Nome AS cliente,
                            c.Cidade AS cidade,
                            '' AS livros,
                            v.total
                        FROM Venda v
                        JOIN Cliente c ON c.id = v.cliente_id
                        ORDER BY v.id ASC
                        """;
            }

            writeSheetFromQuery(
                    writer,
                    conn,
                    "Vendas",
                    vendasQuery,
                    new String[]{"ID Venda", "Cliente", "Cidade", "Livros", "Total"},
                    new int[]{70, 220, 160, 500, 120},
                    Set.of(5)
            );

            writer.write("</Workbook>\n");
        }
    }

    private String getGithubRepository() {
        String configured = System.getProperty("vv.github.repo");
        return isBlank(configured) ? DEFAULT_GITHUB_REPOSITORY : configured.trim();
    }

    private String extractFirstGroup(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String parseAssetFileName(String downloadUrl) {
        String urlWithoutParams = downloadUrl.split("\\?")[0];
        int slash = urlWithoutParams.lastIndexOf('/');
        if (slash >= 0 && slash < urlWithoutParams.length() - 1) {
            return urlWithoutParams.substring(slash + 1);
        }
        return "VerdadesVivasSys.jar";
    }

    private ReleaseAsset fetchLatestReleaseJarAsset() throws IOException, InterruptedException {
        String apiUrl = "https://api.github.com/repos/" + getGithubRepository() + "/releases/latest";

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "VerdadesVivasSys-Updater")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new IOException("Falha ao consultar o release no GitHub. HTTP " + response.statusCode());
        }

        String body = response.body();
        String tagName = extractFirstGroup(body, RELEASE_TAG_PATTERN);
        String jarUrl = extractFirstGroup(body, JAR_DOWNLOAD_PATTERN);

        if (isBlank(jarUrl)) {
            throw new IOException("Nenhum arquivo .jar foi encontrado no ultimo release.");
        }

        jarUrl = jarUrl.replace("\\/", "/");
        return new ReleaseAsset(
                isBlank(tagName) ? "latest" : tagName,
                parseAssetFileName(jarUrl),
                jarUrl
        );
    }

    private Path downloadLatestJar(String downloadUrl) throws IOException, InterruptedException {
        Path downloadedJar = Files.createTempFile("verdadesvivas-update-", ".jar");

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(downloadUrl))
                .header("User-Agent", "VerdadesVivasSys-Updater")
                .GET()
                .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() != 200) {
            throw new IOException("Falha ao baixar o JAR do release. HTTP " + response.statusCode());
        }

        try (InputStream in = response.body()) {
            Files.copy(in, downloadedJar, StandardCopyOption.REPLACE_EXISTING);
        }

        return downloadedJar;
    }

    private Path resolveRunningJarPath() throws URISyntaxException {
        var codeSource = Menu.class.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return null;
        }

        Path location = Path.of(codeSource.getLocation().toURI()).toAbsolutePath().normalize();
        if (Files.isRegularFile(location) && location.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".jar")) {
            return location;
        }
        return null;
    }

    private String resolveJavaCommand() {
        Path javaw = Path.of(System.getProperty("java.home"), "bin", "javaw.exe");
        if (Files.exists(javaw)) {
            return javaw.toAbsolutePath().toString();
        }

        Path java = Path.of(System.getProperty("java.home"), "bin", "java.exe");
        if (Files.exists(java)) {
            return java.toAbsolutePath().toString();
        }

        return "javaw";
    }

    private Path createUpdateScript(Path runningJar, Path downloadedJar) throws IOException {
        Path scriptPath = Files.createTempFile("verdadesvivas-updater-", ".bat");
        String javaCommand = resolveJavaCommand();

        String script = String.join("\r\n",
                "@echo off",
                "setlocal",
                "timeout /t 2 /nobreak >nul",
                "copy /Y \"" + downloadedJar.toAbsolutePath() + "\" \"" + runningJar.toAbsolutePath() + "\" >nul",
                "if errorlevel 1 (",
                "  echo Falha ao atualizar o arquivo do sistema.",
                "  pause",
                "  exit /b 1",
                ")",
                "start \"\" \"" + javaCommand + "\" -jar \"" + runningJar.toAbsolutePath() + "\"",
                "del /f /q \"" + downloadedJar.toAbsolutePath() + "\"",
                "del /f /q \"%~f0\""
        );

        Files.writeString(
                scriptPath,
                script,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        return scriptPath;
    }

    private void runUpdateScript(Path scriptPath) throws IOException {
        new ProcessBuilder("cmd.exe", "/c", scriptPath.toAbsolutePath().toString()).start();
    }

    private PendingUpdate updateSystemFromGithub() throws Exception {
        Path runningJarPath = resolveRunningJarPath();
        if (runningJarPath == null) {
            throw new IllegalStateException("A atualizacao automatica funciona apenas quando o sistema e executado pelo arquivo .jar.");
        }

        ReleaseAsset releaseAsset = fetchLatestReleaseJarAsset();
        Path downloadedJar = downloadLatestJar(releaseAsset.downloadUrl());
        Path updateScript = createUpdateScript(runningJarPath, downloadedJar);
        return new PendingUpdate(releaseAsset, updateScript);
    }

    private record ReleaseAsset(String tag, String fileName, String downloadUrl) {
    }

    private record PendingUpdate(ReleaseAsset releaseAsset, Path scriptPath) {
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVendas = new javax.swing.JTable();
        lblVendas = new javax.swing.JLabel();
        btnDeleteVendas = new javax.swing.JButton();
        btnNewVendas = new javax.swing.JButton();
        btnUpdateVendas = new javax.swing.JButton();
        cmbCliente = new javax.swing.JComboBox<>();
        lblCliente = new javax.swing.JLabel();
        lblLivro = new javax.swing.JLabel();
        cmbLivros = new javax.swing.JComboBox<>();
        btnFiltrarVendas = new javax.swing.JButton();
        btnLimparVendas = new javax.swing.JButton();
        lblCidade = new javax.swing.JLabel();
        cmbCidades = new javax.swing.JComboBox<>();
        btnDetalhesVenda = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        lblClientes = new javax.swing.JLabel();
        btnDeleteClientes = new javax.swing.JButton();
        btnNewClientes = new javax.swing.JButton();
        btnUpdateClientes = new javax.swing.JButton();
        lblCliente2 = new javax.swing.JLabel();
        lblCidade2 = new javax.swing.JLabel();
        cmbClientes2 = new javax.swing.JComboBox<>();
        cmbCidades2 = new javax.swing.JComboBox<>();
        btnLimpar2 = new javax.swing.JButton();
        btnFiltrar2 = new javax.swing.JButton();
        btnDetalhesCliente = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblLivros = new javax.swing.JTable();
        lblLivros = new javax.swing.JLabel();
        btnDeleteLivros = new javax.swing.JButton();
        btnNewLivros = new javax.swing.JButton();
        btnUpdateLivros = new javax.swing.JButton();
        lblNomeLivro = new javax.swing.JLabel();
        lblCodigo = new javax.swing.JLabel();
        cmbNomeLivro = new javax.swing.JComboBox<>();
        cmbCodigoLivro = new javax.swing.JComboBox<>();
        btnLimpar3 = new javax.swing.JButton();
        btnFiltrar3 = new javax.swing.JButton();
        btnDetalhesLivros = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        lblImportar = new javax.swing.JLabel();
        lblExportar = new javax.swing.JLabel();
        btnExportDatabase = new javax.swing.JButton();
        btnExportExcel = new javax.swing.JButton();
        lblExportar1 = new javax.swing.JLabel();
        btnUpdateSystem = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VerdadesVivasSys");

        tblVendas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblVendas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id", "Cliente", "Livros", "Valor Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblVendas);

        lblVendas.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblVendas.setText("Vendas");

        btnDeleteVendas.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnDeleteVendas.setText("- Deletar");
        btnDeleteVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteVendasActionPerformed(evt);
            }
        });

        btnNewVendas.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnNewVendas.setText("+ Nova Venda");
        btnNewVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewVendasActionPerformed(evt);
            }
        });

        btnUpdateVendas.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnUpdateVendas.setText("Atualizar");
        btnUpdateVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateVendasActionPerformed(evt);
            }
        });

        cmbCliente.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbCliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbClienteActionPerformed(evt);
            }
        });

        lblCliente.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblCliente.setText("Cliente:");

        lblLivro.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblLivro.setText("Livro:");

        cmbLivros.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbLivros.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbLivros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLivrosActionPerformed(evt);
            }
        });

        btnFiltrarVendas.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnFiltrarVendas.setText("Filtrar");
        btnFiltrarVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarVendasActionPerformed(evt);
            }
        });

        btnLimparVendas.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnLimparVendas.setText("Limpar");
        btnLimparVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparVendasActionPerformed(evt);
            }
        });

        lblCidade.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblCidade.setText("Cidade: ");

        cmbCidades.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbCidades.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCidades.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCidadesActionPerformed(evt);
            }
        });

        btnDetalhesVenda.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnDetalhesVenda.setText("Detalhes");
        btnDetalhesVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetalhesVendaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblVendas)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 809, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnNewVendas)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdateVendas)
                        .addGap(18, 18, 18)
                        .addComponent(btnDeleteVendas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDetalhesVenda))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCliente)
                            .addComponent(lblLivro))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cmbLivros, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnLimparVendas)
                                .addGap(18, 18, 18)
                                .addComponent(btnFiltrarVendas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cmbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblCidade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbCidades, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lblVendas)
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCliente)
                    .addComponent(lblCidade)
                    .addComponent(cmbCidades, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLivro)
                    .addComponent(cmbLivros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimparVendas)
                    .addComponent(btnFiltrarVendas))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNewVendas, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateVendas, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteVendas, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDetalhesVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33))
        );

        jTabbedPane2.addTab("Vendas", jPanel1);

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id", "Nome", "Cidade", "Contato"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblClientes);

        lblClientes.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblClientes.setText("Clientes");

        btnDeleteClientes.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnDeleteClientes.setText("- Deletar");
        btnDeleteClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteClientesActionPerformed(evt);
            }
        });

        btnNewClientes.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnNewClientes.setText("+ Novo Cliente");
        btnNewClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewClientesActionPerformed(evt);
            }
        });

        btnUpdateClientes.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnUpdateClientes.setText("Atualizar");
        btnUpdateClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateClientesActionPerformed(evt);
            }
        });

        lblCliente2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblCliente2.setText("Cliente:");

        lblCidade2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblCidade2.setText("Cidade:");

        cmbClientes2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbClientes2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbCidades2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbCidades2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCidades2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCidades2ActionPerformed(evt);
            }
        });

        btnLimpar2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnLimpar2.setText("Limpar");
        btnLimpar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpar2ActionPerformed(evt);
            }
        });

        btnFiltrar2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnFiltrar2.setText("Filtrar");
        btnFiltrar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar2ActionPerformed(evt);
            }
        });

        btnDetalhesCliente.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnDetalhesCliente.setText("Detalhes");
        btnDetalhesCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetalhesClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblClientes)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblCliente2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblCidade2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cmbCidades2, javax.swing.GroupLayout.PREFERRED_SIZE, 537, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnLimpar2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnFiltrar2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cmbClientes2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                            .addComponent(btnNewClientes)
                            .addGap(18, 18, 18)
                            .addComponent(btnUpdateClientes)
                            .addGap(18, 18, 18)
                            .addComponent(btnDeleteClientes)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnDetalhesCliente))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 802, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(43, 43, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lblClientes)
                .addGap(29, 29, 29)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCliente2)
                    .addComponent(cmbClientes2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbCidades2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCidade2)
                    .addComponent(btnLimpar2)
                    .addComponent(btnFiltrar2))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNewClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDetalhesCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        jTabbedPane2.addTab("Clientes", jPanel2);

        tblLivros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id", "Cod", "Nome", "Valor"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblLivros);

        lblLivros.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblLivros.setText("Livros");

        btnDeleteLivros.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnDeleteLivros.setText("- Deletar");
        btnDeleteLivros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteLivrosActionPerformed(evt);
            }
        });

        btnNewLivros.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnNewLivros.setText("+ Novo Livro");
        btnNewLivros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewLivrosActionPerformed(evt);
            }
        });

        btnUpdateLivros.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnUpdateLivros.setText("Atualizar");
        btnUpdateLivros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateLivrosActionPerformed(evt);
            }
        });

        lblNomeLivro.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblNomeLivro.setText("Livro: ");

        lblCodigo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblCodigo.setText("Código:");

        cmbNomeLivro.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbNomeLivro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbCodigoLivro.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbCodigoLivro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnLimpar3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnLimpar3.setText("Limpar");
        btnLimpar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpar3ActionPerformed(evt);
            }
        });

        btnFiltrar3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnFiltrar3.setText("Filtrar");
        btnFiltrar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar3ActionPerformed(evt);
            }
        });

        btnDetalhesLivros.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnDetalhesLivros.setText("Detalhes");
        btnDetalhesLivros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetalhesLivrosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblLivros)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnNewLivros)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdateLivros)
                        .addGap(18, 18, 18)
                        .addComponent(btnDeleteLivros)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDetalhesLivros))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 809, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNomeLivro)
                            .addComponent(lblCodigo))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(cmbCodigoLivro, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btnLimpar3, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnFiltrar3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cmbNomeLivro, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(lblLivros)
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNomeLivro)
                    .addComponent(cmbNomeLivro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCodigo)
                    .addComponent(cmbCodigoLivro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar3)
                    .addComponent(btnLimpar3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNewLivros, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateLivros, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteLivros, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDetalhesLivros, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44))
        );

        jTabbedPane2.addTab("Livros", jPanel3);

        lblImportar.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblImportar.setText("Importar");

        lblExportar.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblExportar.setText("Exportar");

        btnExportDatabase.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnExportDatabase.setText("Banco de dados");
        btnExportDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportDatabaseActionPerformed(evt);
            }
        });

        btnExportExcel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnExportExcel.setText("Excel");
        btnExportExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportExcelActionPerformed(evt);
            }
        });

        lblExportar1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblExportar1.setText("Outros");

        btnUpdateSystem.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnUpdateSystem.setText("Atualizar Sistema");
        btnUpdateSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateSystemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnExportExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(78, 78, 78)
                            .addComponent(lblImportar)
                            .addGap(223, 223, 223)
                            .addComponent(lblExportar))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(349, 349, 349)
                            .addComponent(btnExportDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 138, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(btnUpdateSystem, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(lblExportar1)
                        .addGap(107, 107, 107))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblImportar)
                    .addComponent(lblExportar)
                    .addComponent(lblExportar1))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExportDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateSystem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnExportExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(418, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Extra", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewVendasActionPerformed
        new VendaForm().setVisible(true);
    }//GEN-LAST:event_btnNewVendasActionPerformed

    private void btnNewClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewClientesActionPerformed
        new ClienteForm().setVisible(true);
    }//GEN-LAST:event_btnNewClientesActionPerformed

    private void btnNewLivrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewLivrosActionPerformed
        new LivroForm().setVisible(true);
    }//GEN-LAST:event_btnNewLivrosActionPerformed

    private void btnUpdateLivrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateLivrosActionPerformed
        int idlivro = getIdLivroSelecionado();
        if (idlivro >= 0) {
            new LivroForm(livrosDAO.getLivroById(idlivro)).setVisible(true);
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Selecione um livro na tabela",
                    "Erro de identificação",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateLivrosActionPerformed

    private void btnDeleteLivrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteLivrosActionPerformed
        int idlivro = getIdLivroSelecionado();
        if (idlivro >= 0) {
            int continuar = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja deletar o livro selecionado?",
                    "Confirmar Deleção",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (continuar == JOptionPane.YES_OPTION) {
                livrosDAO.deleteLivro(idlivro);
                carregarLivrosNaTabela();
            }

        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Selecione um livro na tabela",
                    "Erro de identificação",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteLivrosActionPerformed

    private void btnDeleteClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteClientesActionPerformed
        int idcliente = getIdClienteSelecionado();
        if (idcliente >= 0) {
            int continuar = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja deletar o cliente selecionado?",
                    "Confirmar Deleção",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (continuar == JOptionPane.YES_OPTION) {
                clientesDAO.deleteCliente(idcliente);
                carregarClientesNaTabela();
            }

        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Selecione um cliente na tabela",
                    "Erro de identificação",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteClientesActionPerformed

    private void btnUpdateClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateClientesActionPerformed
        int idcliente = getIdClienteSelecionado();
        if (idcliente >= 0) {
            new ClienteForm(clientesDAO.getClienteById(idcliente)).setVisible(true);
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Selecione um cliente na tabela",
                    "Erro de identificação",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateClientesActionPerformed

    private void btnDeleteVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteVendasActionPerformed
        int idvenda = getIdVendaSelecionada();
        if (idvenda >= 0) {
            int continuar = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja deletar a venda selecionada?",
                    "Confirmar Deleção",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (continuar == JOptionPane.YES_OPTION) {
                vendasDAO.deleteVenda(idvenda);
                carregarVendasNaTabela();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma venda na tabela",
                    "Erro de identificação",
                    JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteVendasActionPerformed

    private void btnUpdateVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateVendasActionPerformed
        int idvenda = getIdVendaSelecionada();
        if (idvenda >= 0) {
            new VendaForm(vendasDAO.getVendaById(idvenda)).setVisible(true);
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Selecione um livro na tabela",
                    "Erro de identificação",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateVendasActionPerformed

    private void cmbClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbClienteActionPerformed

    private void cmbLivrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLivrosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbLivrosActionPerformed

    private void btnFiltrarVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarVendasActionPerformed
        String clienteSelecionado = (String) cmbCliente.getSelectedItem();
        String livroSelecionado = (String) cmbLivros.getSelectedItem();
        String cidadeSelecionada = (String) cmbCidades.getSelectedItem();

        List<Venda> vendas = vendasDAO.getAllVendas();

        // Filtro por cliente
        if (clienteSelecionado != null && !clienteSelecionado.isBlank()) {
            vendas = vendas.stream()
                    .filter(v -> v.getCliente() != null
                    && v.getCliente().getNome() != null
                    && v.getCliente().getNome().equalsIgnoreCase(clienteSelecionado))
                    .collect(Collectors.toList());
        }

        // Filtro por cidade
        if (cidadeSelecionada != null && !cidadeSelecionada.isBlank()) {
            vendas = vendas.stream()
                    .filter(v -> v.getCliente() != null
                    && v.getCliente().getCidade() != null
                    && v.getCliente().getCidade().equalsIgnoreCase(cidadeSelecionada))
                    .collect(Collectors.toList());
        }

        // Filtro por livro
        if (livroSelecionado != null && !livroSelecionado.isBlank()) {
            vendas = vendas.stream()
                    .filter(v -> v.getLivros() != null
                    && v.getLivros().stream()
                            .anyMatch(l -> l.getNome() != null
                            && l.getNome().equalsIgnoreCase(livroSelecionado)))
                    .collect(Collectors.toList());
        }

        // Atualiza tabela
        DefaultTableModel model = (DefaultTableModel) tblVendas.getModel();
        model.setRowCount(0);

        for (Venda venda : vendas) {
            String nomeCliente = venda.getCliente() != null ? venda.getCliente().getNome() : "—";

            Map<String, Integer> mapaQtd = venda.getLivros().stream()
                    .collect(Collectors.toMap(
                            Livro::getNome,
                            Livro::getQuantidade,
                            Integer::sum
                    ));

            String nomesLivrosFormatados = mapaQtd.entrySet().stream()
                    .map(e -> e.getValue() > 1
                    ? e.getKey() + " (x" + e.getValue() + ")"
                    : e.getKey())
                    .collect(Collectors.joining(", "));

            model.addRow(new Object[]{
                venda.getId(),
                nomeCliente,
                nomesLivrosFormatados,
                String.format("R$ %.2f", venda.getTotal())
            });
        }
    }//GEN-LAST:event_btnFiltrarVendasActionPerformed

    private void btnLimparVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparVendasActionPerformed
        cmbCliente.setSelectedIndex(-1);
        cmbLivros.setSelectedIndex(-1);
        cmbCidades.setSelectedIndex(-1);
        carregarVendasNaTabela();
    }//GEN-LAST:event_btnLimparVendasActionPerformed

    private void cmbCidadesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCidadesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCidadesActionPerformed

    private void cmbCidades2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCidades2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCidades2ActionPerformed

    private void btnLimpar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpar2ActionPerformed
        cmbClientes2.setSelectedIndex(-1);
        cmbCidades2.setSelectedIndex(-1);
        carregarClientesNaTabela();
    }//GEN-LAST:event_btnLimpar2ActionPerformed

    private void btnFiltrar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar2ActionPerformed
        String nomeSelecionado = (String) cmbClientes2.getSelectedItem();
        String cidadeSelecionada = (String) cmbCidades2.getSelectedItem();

        List<Cliente> clientes = clientesDAO.getAllClientes();

        // aplica filtros cumulativos
        if (nomeSelecionado != null && !nomeSelecionado.isBlank()) {
            clientes = clientes.stream()
                    .filter(c -> c.getNome() != null && c.getNome().equalsIgnoreCase(nomeSelecionado))
                    .collect(Collectors.toList());
        }

        if (cidadeSelecionada != null && !cidadeSelecionada.isBlank()) {
            clientes = clientes.stream()
                    .filter(c -> c.getCidade() != null && c.getCidade().equalsIgnoreCase(cidadeSelecionada))
                    .collect(Collectors.toList());
        }

        // atualiza tabela com os filtrados
        DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();
        model.setRowCount(0);

        for (Cliente c : clientes) {
            model.addRow(new Object[]{
                c.getId(),
                c.getNome(),
                c.getCidade(),
                c.getContato()
            });
        }
    }//GEN-LAST:event_btnFiltrar2ActionPerformed

    private void btnFiltrar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar3ActionPerformed
        String nomeSelecionado = (String) cmbNomeLivro.getSelectedItem();
        String codigoSelecionado = (String) cmbCodigoLivro.getSelectedItem();

        List<Livro> livros = livrosDAO.getAllLivros();

        // Filtro por nome
        if (nomeSelecionado != null && !nomeSelecionado.isBlank()) {
            livros = livros.stream()
                    .filter(l -> l.getNome() != null && l.getNome().equalsIgnoreCase(nomeSelecionado))
                    .collect(Collectors.toList());
        }

        // Filtro por código
        if (codigoSelecionado != null && !codigoSelecionado.isBlank()) {
            livros = livros.stream()
                    .filter(l -> l.getCodigo() != null && l.getCodigo().equalsIgnoreCase(codigoSelecionado))
                    .collect(Collectors.toList());
        }

        // Atualiza tabela com os filtrados
        DefaultTableModel model = (DefaultTableModel) tblLivros.getModel();
        model.setRowCount(0);

        for (Livro l : livros) {
            model.addRow(new Object[]{
                l.getId(),
                l.getCodigo(),
                l.getNome(),
                l.getValor()
            });
        }
    }//GEN-LAST:event_btnFiltrar3ActionPerformed

    private void btnLimpar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpar3ActionPerformed
        cmbNomeLivro.setSelectedIndex(-1);
        cmbCodigoLivro.setSelectedIndex(-1);
        carregarLivrosNaTabela();
    }//GEN-LAST:event_btnLimpar3ActionPerformed

    private void btnDetalhesLivrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetalhesLivrosActionPerformed
        int idlivro = getIdLivroSelecionado();
        if (idlivro >= 0) {
            new LivroDetail(livrosDAO.getLivroById(idlivro)).setVisible(true);
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Selecione um livro na tabela",
                    "Erro de identificação",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnDetalhesLivrosActionPerformed

    private void btnDetalhesClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetalhesClienteActionPerformed
        int idcliente = getIdClienteSelecionado();
        if (idcliente >= 0) {
            new ClienteDetail(clientesDAO.getClienteById(idcliente)).setVisible(true);
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Selecione um cliente na tabela",
                    "Erro de identificação",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnDetalhesClienteActionPerformed

    private void btnDetalhesVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetalhesVendaActionPerformed
        int idvenda = getIdVendaSelecionada();
        if (idvenda >= 0) {
            new VendaDetail(vendasDAO.getVendaById(idvenda)).setVisible(true);
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Selecione uma venda na tabela",
                    "Erro de identificação",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnDetalhesVendaActionPerformed

    private void btnExportDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportDatabaseActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar copia do banco");
        chooser.setFileFilter(new FileNameExtensionFilter("Banco SQLite (*.db)", "db"));
        chooser.setSelectedFile(new java.io.File("verdadesvivas-backup-" + timestampSuffix() + ".db"));

        int option = chooser.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path destinationPath = withExtension(chooser.getSelectedFile().toPath(), ".db");

        try {
            exportDatabaseCopy(destinationPath);
            JOptionPane.showMessageDialog(
                    this,
                    "Backup exportado com sucesso para:\n" + destinationPath,
                    "Exportar banco",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nao foi possivel exportar o banco.\n" + ex.getMessage(),
                    "Erro ao exportar",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_btnExportDatabaseActionPerformed

    private void btnExportExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportExcelActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar planilha do Excel");
        chooser.setFileFilter(new FileNameExtensionFilter("Planilha Excel 2003 XML (*.xls)", "xls"));
        chooser.setSelectedFile(new java.io.File("verdadesvivas-relatorio-" + timestampSuffix() + ".xls"));

        int option = chooser.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path destinationPath = withExtension(chooser.getSelectedFile().toPath(), ".xls");

        try {
            exportDatabaseToExcel(destinationPath);
            JOptionPane.showMessageDialog(
                    this,
                    "Planilha gerada com sucesso em:\n" + destinationPath,
                    "Exportar Excel",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nao foi possivel exportar para Excel.\n" + ex.getMessage(),
                    "Erro ao exportar",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_btnExportExcelActionPerformed

    private void btnUpdateSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateSystemActionPerformed
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "O sistema vai baixar o ultimo release e substituir o arquivo .jar atual.\nDeseja continuar?",
                "Atualizar sistema",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            PendingUpdate pendingUpdate = updateSystemFromGithub();
            ReleaseAsset release = pendingUpdate.releaseAsset();

            JOptionPane.showMessageDialog(
                    this,
                    "Atualizacao " + release.tag() + " (" + release.fileName() + ") baixada.\n"
                    + "Clique em OK para fechar e reabrir o sistema com a nova versão.",
                    "Atualizar sistema",
                    JOptionPane.INFORMATION_MESSAGE
            );
            runUpdateScript(pendingUpdate.scriptPath());
            dispose();
            System.exit(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nao foi possivel atualizar o sistema.\n" + ex.getMessage(),
                    "Erro ao atualizar",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_btnUpdateSystemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Menu().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteClientes;
    private javax.swing.JButton btnDeleteLivros;
    private javax.swing.JButton btnDeleteVendas;
    private javax.swing.JButton btnDetalhesCliente;
    private javax.swing.JButton btnDetalhesLivros;
    private javax.swing.JButton btnDetalhesVenda;
    private javax.swing.JButton btnExportDatabase;
    private javax.swing.JButton btnExportExcel;
    private javax.swing.JButton btnFiltrar2;
    private javax.swing.JButton btnFiltrar3;
    private javax.swing.JButton btnFiltrarVendas;
    private javax.swing.JButton btnLimpar2;
    private javax.swing.JButton btnLimpar3;
    private javax.swing.JButton btnLimparVendas;
    private javax.swing.JButton btnNewClientes;
    private javax.swing.JButton btnNewLivros;
    private javax.swing.JButton btnNewVendas;
    private javax.swing.JButton btnUpdateClientes;
    private javax.swing.JButton btnUpdateLivros;
    private javax.swing.JButton btnUpdateSystem;
    private javax.swing.JButton btnUpdateVendas;
    private javax.swing.JComboBox<String> cmbCidades;
    private javax.swing.JComboBox<String> cmbCidades2;
    private javax.swing.JComboBox<String> cmbCliente;
    private javax.swing.JComboBox<String> cmbClientes2;
    private javax.swing.JComboBox<String> cmbCodigoLivro;
    private javax.swing.JComboBox<String> cmbLivros;
    private javax.swing.JComboBox<String> cmbNomeLivro;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel lblCidade;
    private javax.swing.JLabel lblCidade2;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblCliente2;
    private javax.swing.JLabel lblClientes;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblExportar;
    private javax.swing.JLabel lblExportar1;
    private javax.swing.JLabel lblImportar;
    private javax.swing.JLabel lblLivro;
    private javax.swing.JLabel lblLivros;
    private javax.swing.JLabel lblNomeLivro;
    private javax.swing.JLabel lblVendas;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTable tblLivros;
    private javax.swing.JTable tblVendas;
    // End of variables declaration//GEN-END:variables
}
