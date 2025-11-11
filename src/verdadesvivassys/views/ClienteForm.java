/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package verdadesvivassys.views;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import verdadesvivassys.model.Cliente;

/**
 *
 * @author ADM
 */
public class ClienteForm extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ClienteForm.class.getName());
    private int editId = -1;

    public ClienteForm() {
        initComponents();
        initCustom();
    }

    public ClienteForm(Cliente cliente) {
        initComponents();
        initCustom();

        lblTitulo.setText("Editar Cliente");

        // Preenche campos de texto
        txtNome.setText(cliente.getNome());
        txtContato.setText(cliente.getContato());

        // Limpa campo de cidade manual
        txtAddCidade.setText("");

        // Garante que o combo tenha opções antes de tentar selecionar
        if (cmbCidades.getItemCount() > 0) {
            String cidadeCliente = cliente.getCidade();
            if (cidadeCliente != null && !cidadeCliente.isBlank()) {
                // Se a cidade do cliente existir no combo, seleciona
                boolean found = false;
                for (int i = 0; i < cmbCidades.getItemCount(); i++) {
                    if (cidadeCliente.equalsIgnoreCase(cmbCidades.getItemAt(i))) {
                        cmbCidades.setSelectedIndex(i);
                        found = true;
                        break;
                    }
                }
                // Se não existir no combo, adiciona e seleciona
                if (!found) {
                    cmbCidades.addItem(cidadeCliente);
                    cmbCidades.setSelectedItem(cidadeCliente);
                }
            } else {
                cmbCidades.setSelectedIndex(-1); // Nenhuma cidade
            }
        }

        txtNome.requestFocus();
    }

    private void initCustom() {
        loadCidades();
        AutoCompleteDecorator.decorate(cmbCidades); // autocomplete ativado
        btnAddCidade.addActionListener(e -> addCidade());
    }

    private void loadCidades() {
        cmbCidades.removeAllItems();
        var cidades = verdadesvivassys.dao.DAOFactory.getClientesDAO().getAllCidades();

        if (cidades.isEmpty()) {
            cmbCidades.addItem("Nenhuma cidade cadastrada");
            cmbCidades.setEnabled(false);
        } else {
            for (String cidade : cidades) {
                cmbCidades.addItem(cidade);
            }
            cmbCidades.setEnabled(true);
        }
    }

    private void addCidade() {
        try {
            String nomeCidade = txtAddCidade.getText().trim();

            // Valida campo vazio
            if (nomeCidade.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Digite o nome da cidade antes de adicionar.",
                        "Campo vazio",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Valida padrão de caracteres (somente letras, espaços, acentos e hífen)
            if (!nomeCidade.matches("[A-Za-zÀ-ÖØ-öø-ÿ\\s\\-']+")) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "O nome da cidade contém caracteres inválidos.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            var dao = verdadesvivassys.dao.DAOFactory.getClientesDAO();
            var cidades = dao.getAllCidades();

            // Verifica duplicata
            for (String c : cidades) {
                if (c.equalsIgnoreCase(nomeCidade)) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Esta cidade já está cadastrada.",
                            "Duplicado",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Insere a cidade
            boolean sucesso = dao.insertCidade(nomeCidade);
            if (sucesso) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Cidade adicionada com sucesso!");
                txtAddCidade.setText("");
                loadCidades();
                cmbCidades.setSelectedItem(nomeCidade);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Erro ao adicionar cidade.",
                        "Erro",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Erro ao adicionar cidade", e);
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Erro inesperado ao adicionar cidade: " + e.getMessage(),
                    "Erro",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean verify() {
        try {
            if (txtNome.getText().trim().isEmpty()
                    || txtContato.getText().trim().isEmpty()
                    || cmbCidades.getSelectedItem() == null
                    || cmbCidades.getSelectedItem().toString().equals("Nenhuma cidade cadastrada")) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Todos os campos devem ser preenchidos.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }

            String nome = txtNome.getText().trim();
            if (!nome.matches("[A-Za-zÀ-ÖØ-öø-ÿ\\s\\-']+")) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "O nome contém caracteres inválidos. Use apenas letras e espaços.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }

            String cidade = cmbCidades.getSelectedItem().toString();
            if (!cidade.matches("[A-Za-zÀ-ÖØ-öø-ÿ\\s\\-']+")) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "A cidade contém caracteres inválidos.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }

            String contato = txtContato.getText().trim();
            if (!contato.matches("[0-9\\+\\-\\s\\(\\)]+")) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "O contato contém caracteres inválidos. Use apenas números e símbolos de telefone.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }

            return true;

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Erro na verificação de dados do cliente", e);
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Erro inesperado durante a verificação.",
                    "Erro",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void addCliente() {
        try {
            if (!verify()) {
                return;
            }

            String nome = txtNome.getText().trim();
            String cidade = cmbCidades.getSelectedItem().toString();
            String contato = txtContato.getText().trim();

            Cliente cliente = new Cliente();
            cliente.setNome(nome);
            cliente.setCidade(cidade);
            cliente.setContato(contato);

            boolean sucesso;
            if (editId == -1) {
                sucesso = verdadesvivassys.dao.DAOFactory.getClientesDAO().insertCliente(cliente);
            } else {
                cliente.setId(editId);
                sucesso = verdadesvivassys.dao.DAOFactory.getClientesDAO().updateCliente(cliente);
            }

            if (sucesso) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        (editId == -1 ? "Cliente cadastrado" : "Cliente atualizado") + " com sucesso!");
                clearFields();
                this.dispose();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Falha ao salvar o cliente. Verifique os dados e tente novamente.",
                        "Erro",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Erro ao salvar cliente", e);
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Erro inesperado ao salvar o cliente: " + e.getMessage(),
                    "Erro",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtNome.setText("");
        txtContato.setText("");
        txtAddCidade.setText("");
        if (cmbCidades.getItemCount() > 0) {
            cmbCidades.setSelectedIndex(0);
        }
        txtNome.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();
        lblCidade = new javax.swing.JLabel();
        lblContato = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        txtContato = new javax.swing.JTextField();
        btnConfirmar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        cmbCidades = new javax.swing.JComboBox<>();
        btnAddCidade = new javax.swing.JButton();
        txtAddCidade = new javax.swing.JTextField();
        lblCidade1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblTitulo.setText("Novo Cliente");

        lblNome.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblNome.setText("Nome:");

        lblCidade.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblCidade.setText("Cidade:");

        lblContato.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblContato.setText("Contato:");

        txtNome.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        txtContato.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        btnConfirmar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnConfirmar.setText("Confirmar");
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        btnLimpar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnLimpar.setText("Limpar");
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        cmbCidades.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbCidades.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnAddCidade.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnAddCidade.setText("+ Cidade");

        txtAddCidade.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        lblCidade1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblCidade1.setText("+ Cidade:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(219, 219, 219)
                        .addComponent(lblTitulo))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnLimpar)
                                .addGap(18, 18, 18)
                                .addComponent(btnConfirmar))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblCidade)
                                    .addComponent(lblContato)
                                    .addComponent(lblNome)
                                    .addComponent(lblCidade1))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtContato)
                                    .addComponent(txtNome)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(txtAddCidade, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnAddCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(cmbCidades, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addGap(69, 76, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lblTitulo)
                .addGap(58, 58, 58)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNome)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCidade)
                    .addComponent(cmbCidades, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddCidade)
                    .addComponent(txtAddCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCidade1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblContato)
                    .addComponent(txtContato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirmar)
                    .addComponent(btnLimpar))
                .addGap(33, 33, 33))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        addCliente();
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        clearFields();
    }//GEN-LAST:event_btnLimparActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new ClienteForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCidade;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JComboBox<String> cmbCidades;
    private javax.swing.JLabel lblCidade;
    private javax.swing.JLabel lblCidade1;
    private javax.swing.JLabel lblContato;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTextField txtAddCidade;
    private javax.swing.JTextField txtContato;
    private javax.swing.JTextField txtNome;
    // End of variables declaration//GEN-END:variables
}
