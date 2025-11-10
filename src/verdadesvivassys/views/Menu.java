package verdadesvivassys.views;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import verdadesvivassys.connection.DatabaseConfig;
import verdadesvivassys.dao.ClientesDAO;
import verdadesvivassys.dao.DAOFactory;
import verdadesvivassys.dao.LivrosDAO;
import verdadesvivassys.dao.VendasDAO;
import verdadesvivassys.model.Cliente;
import verdadesvivassys.model.Livro;
import verdadesvivassys.model.Venda;

public class Menu extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Menu.class.getName());

    private LivrosDAO livrosDAO = DAOFactory.getLivrosDAO();
    private ClientesDAO clientesDAO = DAOFactory.getClientesDAO();
    private VendasDAO vendasDAO = DAOFactory.getVendasDAO();

    public Menu() {
        new DatabaseConfig().initialize();
        initComponents();
        carregarLivrosNaTabela();
        carregarClientesNaTabela();

        this.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                int abaSelecionada = jTabbedPane2.getSelectedIndex();

                switch (abaSelecionada) {
                    case 0 ->
                        carregarVendasNaTabela();
                    case 1 ->
                        carregarClientesNaTabela();
                    case 2 ->
                        carregarLivrosNaTabela();
                    default -> {
                        /* nenhuma ação */ }
                }
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
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        lblClientes = new javax.swing.JLabel();
        btnDeleteClientes = new javax.swing.JButton();
        btnNewClientes = new javax.swing.JButton();
        btnUpdateClientes = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblLivros = new javax.swing.JTable();
        lblLivros = new javax.swing.JLabel();
        btnDeleteLivros = new javax.swing.JButton();
        btnNewLivros = new javax.swing.JButton();
        btnUpdateLivros = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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

        lblVendas.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblVendas.setText("Vendas");

        btnDeleteVendas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnDeleteVendas.setText("Deletar");
        btnDeleteVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteVendasActionPerformed(evt);
            }
        });

        btnNewVendas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnNewVendas.setText("Nova Venda");
        btnNewVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewVendasActionPerformed(evt);
            }
        });

        btnUpdateVendas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnUpdateVendas.setText("Atualizar");
        btnUpdateVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateVendasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnNewVendas)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdateVendas)
                        .addGap(18, 18, 18)
                        .addComponent(btnDeleteVendas))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblVendas)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 689, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lblVendas)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeleteVendas, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNewVendas, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateVendas, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(56, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Vendas", jPanel1);

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Id", "Nome", "Cidade"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblClientes);

        lblClientes.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblClientes.setText("Clientes");

        btnDeleteClientes.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnDeleteClientes.setText("Deletar");
        btnDeleteClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteClientesActionPerformed(evt);
            }
        });

        btnNewClientes.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnNewClientes.setText("Novo Cliente");
        btnNewClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewClientesActionPerformed(evt);
            }
        });

        btnUpdateClientes.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnUpdateClientes.setText("Atualizar");
        btnUpdateClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateClientesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnNewClientes)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdateClientes)
                        .addGap(18, 18, 18)
                        .addComponent(btnDeleteClientes))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblClientes)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 689, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lblClientes)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeleteClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNewClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(56, Short.MAX_VALUE))
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

        lblLivros.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblLivros.setText("Livros");

        btnDeleteLivros.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnDeleteLivros.setText("Deletar");
        btnDeleteLivros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteLivrosActionPerformed(evt);
            }
        });

        btnNewLivros.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnNewLivros.setText("Novo Livro");
        btnNewLivros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewLivrosActionPerformed(evt);
            }
        });

        btnUpdateLivros.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnUpdateLivros.setText("Atualizar");
        btnUpdateLivros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateLivrosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnNewLivros)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdateLivros)
                        .addGap(18, 18, 18)
                        .addComponent(btnDeleteLivros))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblLivros)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 689, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lblLivros)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeleteLivros, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNewLivros, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateLivros, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(56, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Livros", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 756, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 535, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    private javax.swing.JButton btnNewClientes;
    private javax.swing.JButton btnNewLivros;
    private javax.swing.JButton btnNewVendas;
    private javax.swing.JButton btnUpdateClientes;
    private javax.swing.JButton btnUpdateLivros;
    private javax.swing.JButton btnUpdateVendas;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel lblClientes;
    private javax.swing.JLabel lblLivros;
    private javax.swing.JLabel lblVendas;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTable tblLivros;
    private javax.swing.JTable tblVendas;
    // End of variables declaration//GEN-END:variables
}
