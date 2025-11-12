package verdadesvivassys.views.venda;

import verdadesvivassys.views.cliente.ClienteForm;
import java.lang.System.Logger.Level;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import verdadesvivassys.dao.ClientesDAO;
import verdadesvivassys.dao.DAOFactory;
import verdadesvivassys.dao.LivrosDAO;
import verdadesvivassys.model.Cliente;
import verdadesvivassys.model.Livro;
import verdadesvivassys.model.Venda;

public class VendaForm extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VendaForm.class.getName());

    private LivrosDAO livrosDAO = DAOFactory.getLivrosDAO();
    private ClientesDAO clientesDAO = DAOFactory.getClientesDAO();
    private int editid = -1;

    public VendaForm() {
        initComponents();
        loadClientes();
        loadLivros();
        setResizable(false);

        clearFields();
        AutoCompleteDecorator.decorate(cmbCliente);
        AutoCompleteDecorator.decorate(cmbLivro);

        this.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                loadClientes();
                loadLivros();
            }

            @Override
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                // nada a fazer
            }
        });
    }

    public VendaForm(Venda venda) {
        initComponents();
        loadClientes();
        loadLivros();
        setResizable(false);
        
        this.editid = venda.getId();

        AutoCompleteDecorator.decorate(cmbCliente);
        AutoCompleteDecorator.decorate(cmbLivro);

        // título adaptado
        lblTitulo.setText("Editar Venda #" + venda.getId());

        // seleciona o cliente correspondente
        cmbCliente.setSelectedItem(venda.getCliente().getNome());

        // limpa tabela e preenche com os livros da venda (agora incluindo quantidade)
        DefaultTableModel model = (DefaultTableModel) tblLivros.getModel();
        model.setRowCount(0);
        for (Livro l : venda.getLivros()) {
            model.addRow(new Object[]{
                l.getId(),
                l.getCodigo(), // pode ser null se não estiver carregado no DAO; opcional
                l.getNome(),
                l.getValor(), // pode ser 0 se não foi carregado pelo DAO
                l.getQuantidade() // <<< aqui
            });
        }

        // atualiza o total
        lblTotal.setText("Valor Total: R$ " + String.format("%.2f", venda.getTotal()));

        // adiciona o mesmo listener de foco da janela
        this.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                loadClientes();
                loadLivros();
            }

            @Override
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                // nada a fazer
            }
        });
    }

    private void loadClientes() {
        cmbCliente.removeAllItems();
        for (Cliente c : clientesDAO.getAllClientes()) {
            cmbCliente.addItem(c.getNome());
        }
    }

    private void loadLivros() {
        cmbLivro.removeAllItems();
        for (Livro l : livrosDAO.getAllLivros()) {
            cmbLivro.addItem(l.getNome());
        }
    }

    private void updateTotal() {
        DefaultTableModel model = (DefaultTableModel) tblLivros.getModel();
        float total = 0f;

        for (int i = 0; i < model.getRowCount(); i++) {
            Object valorObj = model.getValueAt(i, 3);
            Object qtdObj = model.getValueAt(i, 4);

            if (valorObj == null) {
                continue;
            }
            String valorStr = valorObj.toString().trim();
            if (valorStr.isEmpty() || valorStr.equalsIgnoreCase("null")) {
                continue;
            }

            int qtd = 1;
            if (qtdObj != null) {
                try {
                    qtd = Integer.parseInt(qtdObj.toString());
                    if (qtd < 0) {
                        qtd = 1;
                    }
                } catch (NumberFormatException ex) {
                    qtd = 1;
                }
            }

            try {
                float valor = Float.parseFloat(valorStr);
                total += valor * qtd;
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido na linha " + i + ": " + valorStr);
            }
        }

        lblTotal.setText(String.format("Valor Total: R$ %.2f", total));
    }

    /**
     * Verifica se os campos obrigatórios da venda estão preenchidos
     * corretamente. Retorna true se estiver tudo certo, senão mostra mensagens
     * e retorna false.
     */
    private boolean verifyFields() {
        // Verifica se o cliente foi selecionado
        if (cmbCliente.getSelectedItem() == null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Selecione um cliente para a venda.",
                    "Erro de validação",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Verifica se há livros na tabela
        DefaultTableModel model = (DefaultTableModel) tblLivros.getModel();
        if (model.getRowCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Adicione pelo menos um livro à venda.",
                    "Erro de validação",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Verifica se os valores e quantidades estão válidos
        for (int i = 0; i < model.getRowCount(); i++) {
            Object valorObj = model.getValueAt(i, 3);
            Object qtdObj = model.getValueAt(i, 4);

            if (valorObj == null || valorObj.toString().isBlank()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Um ou mais livros possuem valor inválido.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }

            try {
                Float.parseFloat(valorObj.toString());
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Valor inválido encontrado em um dos livros.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (qtdObj == null || qtdObj.toString().isBlank()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Informe a quantidade para todos os livros.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }

            try {
                int qtd = Integer.parseInt(qtdObj.toString());
                if (qtd <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Quantidade inválida encontrada em um dos livros.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        return true;
    }

    private void addVenda() {
    try {
        // 1️⃣ Validar campos
        if (!verifyFields()) {
            return;
        }

        // 2️⃣ Pegar o cliente selecionado
        String nomeCliente = cmbCliente.getSelectedItem().toString();
        java.util.List<Cliente> clientes = clientesDAO.getByNome(nomeCliente);

        if (clientes == null || clientes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Cliente inválido. Atualize a lista e tente novamente.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cliente cliente = clientes.get(0);

        // 3️⃣ Montar lista de livros com quantidade
        DefaultTableModel model = (DefaultTableModel) tblLivros.getModel();
        java.util.List<Livro> livros = new java.util.ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            int id = Integer.parseInt(model.getValueAt(i, 0).toString());
            String nome = model.getValueAt(i, 2).toString();
            float valor = Float.parseFloat(model.getValueAt(i, 3).toString());
            int qtd = Integer.parseInt(model.getValueAt(i, 4).toString());

            Livro l = new Livro();
            l.setId(id);
            l.setNome(nome);
            l.setValor(valor);
            l.setQuantidade(qtd);

            livros.add(l);
        }

        // 4️⃣ Calcular total
        float total = 0;
        for (Livro l : livros) {
            total += l.getValor() * l.getQuantidade();
        }

        // 5️⃣ Montar objeto Venda
        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setLivros(livros);
        venda.setTotal(total);

        boolean sucesso;

        // 6️⃣ Verificar INSERÇÃO ou EDIÇÃO
        if (editid == -1) {
            // nova venda
            sucesso = DAOFactory.getVendasDAO().addVenda(venda);
        } else {
            // edição
            venda.setId(editid);
            sucesso = DAOFactory.getVendasDAO().updateVenda(venda);
        }

        // 7️⃣ Feedback
        if (sucesso) {
            if (editid == -1) {
                JOptionPane.showMessageDialog(this,
                    "Venda cadastrada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Venda atualizada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }

            clearFields();
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this,
                "Falha ao salvar a venda.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }

    } catch (Exception e) {
        logger.log(java.util.logging.Level.SEVERE, "Erro ao salvar venda", e);
        JOptionPane.showMessageDialog(this,
                "Erro inesperado ao salvar a venda: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
    }
}


    private void clearFields() {
        cmbCliente.setSelectedIndex(-1);
        cmbLivro.setSelectedIndex(-1);
        DefaultTableModel model = (DefaultTableModel) tblLivros.getModel();
        model.setRowCount(0);
        lblTotal.setText("Valor Total: R$ 0,00");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblCliente = new javax.swing.JLabel();
        cmbCliente = new javax.swing.JComboBox<>();
        lblLivros = new javax.swing.JLabel();
        btnAddLivro = new javax.swing.JButton();
        cmbLivro = new javax.swing.JComboBox<>();
        scrollpane = new javax.swing.JScrollPane();
        tblLivros = new javax.swing.JTable();
        btnRemoverLivro = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        btnConfirmar = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();
        btnAddCliente = new javax.swing.JButton();
        txtQuantidade = new javax.swing.JTextField();
        lblQuantidade = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblTitulo.setText("Nova Venda");

        lblCliente.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblCliente.setText("Cliente:");

        cmbCliente.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbCliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbClienteActionPerformed(evt);
            }
        });

        lblLivros.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblLivros.setText("Livros:");

        btnAddLivro.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnAddLivro.setText("+ Adicionar Livro");
        btnAddLivro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddLivroActionPerformed(evt);
            }
        });

        cmbLivro.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbLivro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbLivro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLivroActionPerformed(evt);
            }
        });

        tblLivros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id", "Cod", "Nome", "Valor", "Qtd"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollpane.setViewportView(tblLivros);

        btnRemoverLivro.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnRemoverLivro.setText("- Remover Livro");
        btnRemoverLivro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverLivroActionPerformed(evt);
            }
        });

        btnLimpar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnLimpar.setText("Limpar");
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        btnConfirmar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnConfirmar.setText("Confirmar");
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        lblTotal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTotal.setText("Valor Total:");

        btnAddCliente.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnAddCliente.setText("+ Novo Cliente");
        btnAddCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddClienteActionPerformed(evt);
            }
        });

        txtQuantidade.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        lblQuantidade.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblQuantidade.setText("Quantidade:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(299, 299, 299)
                        .addComponent(lblTitulo))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(31, 31, 31)
                            .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(49, 49, 49)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblCliente)
                                .addComponent(lblLivros)
                                .addComponent(lblQuantidade))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnRemoverLivro, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(scrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(btnAddLivro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(cmbCliente, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGap(18, 18, 18)
                                    .addComponent(btnAddCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(cmbLivro, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(76, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblTitulo)
                .addGap(51, 51, 51)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCliente)
                    .addComponent(cmbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddCliente))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLivros)
                    .addComponent(cmbLivro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQuantidade)
                    .addComponent(btnAddLivro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addComponent(scrollpane, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal)
                    .addComponent(btnRemoverLivro))
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLimpar)
                    .addComponent(btnConfirmar))
                .addGap(27, 27, 27))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbLivroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLivroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbLivroActionPerformed

    private void btnAddLivroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLivroActionPerformed
        try {
            if (cmbLivro.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                        "Selecione um livro antes de adicionar.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sel = cmbLivro.getSelectedItem().toString();
            Livro livro = livrosDAO.getByNome(sel);
            if (livro == null) {
                JOptionPane.showMessageDialog(this,
                        "Livro não encontrado!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantidade = 1;
            String qtxt = txtQuantidade.getText().trim();

            if (!qtxt.isEmpty()) {
                try {
                    quantidade = Integer.parseInt(qtxt);
                    if (quantidade <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Quantidade inválida!",
                            "Erro",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            DefaultTableModel model = (DefaultTableModel) tblLivros.getModel();

            boolean found = false;
            for (int i = 0; i < model.getRowCount(); i++) {
                if (Integer.parseInt(model.getValueAt(i, 0).toString()) == livro.getId()) {
                    int atual = Integer.parseInt(model.getValueAt(i, 4).toString());
                    model.setValueAt(atual + quantidade, i, 4);
                    found = true;
                    break;
                }
            }

            if (!found) {
                model.addRow(new Object[]{
                    livro.getId(),
                    livro.getCodigo(),
                    livro.getNome(),
                    livro.getValor(),
                    quantidade
                });
            }

            txtQuantidade.setText("");
            updateTotal();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao adicionar livro: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAddLivroActionPerformed

    private void cmbClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbClienteActionPerformed

    private void btnRemoverLivroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverLivroActionPerformed
        int selectedRow = tblLivros.getSelectedRow();
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Selecione um livro para remover.",
                    "Aviso",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblLivros.getModel();
        model.removeRow(selectedRow);
        updateTotal();
    }//GEN-LAST:event_btnRemoverLivroActionPerformed

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        cmbCliente.setSelectedIndex(-1);
        cmbLivro.setSelectedIndex(-1);
        DefaultTableModel model = (DefaultTableModel) tblLivros.getModel();
        model.setRowCount(0);
        lblTotal.setText("Valor Total: R$ 0,00");
    }//GEN-LAST:event_btnLimparActionPerformed

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        addVenda();
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnAddClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddClienteActionPerformed
        new ClienteForm().setVisible(true);
    }//GEN-LAST:event_btnAddClienteActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new VendaForm().setVisible(true));
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCliente;
    private javax.swing.JButton btnAddLivro;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnRemoverLivro;
    private javax.swing.JComboBox<String> cmbCliente;
    private javax.swing.JComboBox<String> cmbLivro;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblLivros;
    private javax.swing.JLabel lblQuantidade;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JScrollPane scrollpane;
    private javax.swing.JTable tblLivros;
    private javax.swing.JTextField txtQuantidade;
    // End of variables declaration//GEN-END:variables
}
