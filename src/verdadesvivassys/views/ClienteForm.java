/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package verdadesvivassys.views;

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
    }

    public ClienteForm(Cliente cliente) {
        initComponents();
        lblTitulo.setText("Editar Cliente");
        txtCidade.setText(cliente.getCidade());
        txtNome.setText(cliente.getNome());
        txtContato.setText(cliente.getContato());
        this.editId = cliente.getId();

    }

    private boolean verify() {
        try {
            // Verifica campos vazios
            if (txtNome.getText().trim().isEmpty()
                    || txtCidade.getText().trim().isEmpty()
                    || txtContato.getText().trim().isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Todos os campos devem ser preenchidos.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Valida nome (somente letras, espaços e acentuação)
            String nome = txtNome.getText().trim();
            if (!nome.matches("[A-Za-zÀ-ÖØ-öø-ÿ\\s\\-']+")) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "O nome contém caracteres inválidos. Use apenas letras e espaços.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Valida cidade (aceita letras, espaços e acentuação)
            String cidade = txtCidade.getText().trim();
            if (!cidade.matches("[A-Za-zÀ-ÖØ-öø-ÿ\\s\\-']+")) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "A cidade contém caracteres inválidos.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Valida contato (aceita números, +, -, espaços, parênteses)
            String contato = txtContato.getText().trim();
            if (!contato.matches("[0-9\\+\\-\\s\\(\\)]+")) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "O contato contém caracteres inválidos. Use apenas números e símbolos de telefone.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Tudo certo
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
            // 1️⃣ Valida os campos
            if (!verify()) {
                return;
            }

            // 2️⃣ Pega os valores validados
            String nome = txtNome.getText().trim();
            String cidade = txtCidade.getText().trim();
            String contato = txtContato.getText().trim();

            // 3️⃣ Cria o objeto Cliente
            Cliente cliente = new Cliente();
            cliente.setNome(nome);
            cliente.setCidade(cidade);
            cliente.setContato(contato);

            boolean sucesso;

            // 4️⃣ Verifica se é inserção ou edição
            if (editId == -1) {
                // Novo cadastro
                sucesso = verdadesvivassys.dao.DAOFactory.getClientesDAO().insertCliente(cliente);
            } else {
                // Atualização de cliente existente
                cliente.setId(editId);
                sucesso = verdadesvivassys.dao.DAOFactory.getClientesDAO().updateCliente(cliente);
            }

            // 5️⃣ Feedback ao usuário
            if (sucesso) {
                if (editId == -1) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
                    clearFields();
                    this.dispose();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
                    this.dispose(); // fecha a janela de edição
                }
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
        txtCidade.setText("");
        txtContato.setText("");
        txtNome.requestFocus(); // volta o foco pro primeiro campo
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();
        lblCidade = new javax.swing.JLabel();
        lblContato = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        txtCidade = new javax.swing.JTextField();
        txtContato = new javax.swing.JTextField();
        btnConfirmar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblTitulo.setText("Novo Cliente");

        lblNome.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblNome.setText("Nome");

        lblCidade.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblCidade.setText("Cidade");

        lblContato.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblContato.setText("Contato");

        txtNome.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        txtCidade.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(158, 158, 158)
                        .addComponent(lblTitulo))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnLimpar)
                                .addGap(18, 18, 18)
                                .addComponent(btnConfirmar))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblCidade, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblContato, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblNome, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtContato, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblTitulo)
                .addGap(57, 57, 57)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNome)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCidade)
                    .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblContato)
                    .addComponent(txtContato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
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
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JLabel lblCidade;
    private javax.swing.JLabel lblContato;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTextField txtCidade;
    private javax.swing.JTextField txtContato;
    private javax.swing.JTextField txtNome;
    // End of variables declaration//GEN-END:variables
}
