/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package verdadesvivassys.views.livro;

import verdadesvivassys.model.Livro;

/**
 *
 * @author ADM
 */
public class LivroForm extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LivroForm.class.getName());
    private int editId = -1;

    public LivroForm() {
        initComponents();
        setResizable(false);
    }

    public LivroForm(Livro livro) {
        initComponents();
        setResizable(false);
        lblTitulo.setText("Editar Livro");
        txtNome.setText(livro.getNome());
        txtCodigo.setText(livro.getCodigo());
        txtValor.setText(String.valueOf(livro.getValor()));
        this.editId = livro.getId();

    }

    private boolean verify() {
        try {
            // Verifica campos vazios
            if (txtCodigo.getText().trim().isEmpty()
                    || txtNome.getText().trim().isEmpty()
                    || txtValor.getText().trim().isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Todos os campos devem ser preenchidos.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Verifica se o valor é numérico e positivo
            float valor;
            try {
                valor = Float.parseFloat(txtValor.getText().replace(",", "."));
                if (valor < 0) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "O valor não pode ser negativo.",
                            "Erro de validação",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "O campo Valor deve conter apenas números válidos.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Verifica caracteres inválidos no código e nome
            String codigo = txtCodigo.getText().trim();
            String nome = txtNome.getText().trim();
            if (!codigo.matches("[A-Za-z0-9\\-]+")) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "O código só pode conter letras, números e traços.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (!nome.matches("[A-Za-zÀ-ÖØ-öø-ÿ0-9\\s\\-\\,\\.\\(\\)/:!]+")) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "O nome contém caracteres inválidos.",
                        "Erro de validação",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Tudo certo
            return true;

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Erro na verificação de dados", e);
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Erro inesperado durante a verificação.",
                    "Erro",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void addLivro() {
        try {
            // 1️⃣ Primeiro, valida os campos
            if (!verify()) {
                return;
            }

            // 2️⃣ Pega os valores validados
            String codigo = txtCodigo.getText().trim();
            String nome = txtNome.getText().trim();
            float valor = Float.parseFloat(txtValor.getText().replace(",", "."));

            // 3️⃣ Cria o objeto Livro
            Livro livro = new Livro();
            livro.setCodigo(codigo);
            livro.setNome(nome);
            livro.setValor(valor);

            boolean sucesso;

            // 4️⃣ Verifica se é inserção ou edição
            if (editId == -1) {
                // Novo cadastro
                sucesso = verdadesvivassys.dao.DAOFactory.getLivrosDAO().insertLivro(livro);
            } else {
                // Atualização de livro existente
                livro.setId(editId);
                sucesso = verdadesvivassys.dao.DAOFactory.getLivrosDAO().updateLivro(livro);
            }

            // 5️⃣ Feedback ao usuário
            if (sucesso) {
                if (editId == -1) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Livro cadastrado com sucesso!");
                    clearFields();
                    this.dispose();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Livro atualizado com sucesso!");
                    this.dispose(); // fecha a janela de edição
                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Falha ao salvar o livro. Verifique os dados e tente novamente.",
                        "Erro",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Erro ao salvar livro", e);
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Erro inesperado ao salvar o livro: " + e.getMessage(),
                    "Erro",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtCodigo.setText("");
        txtNome.setText("");
        txtValor.setText("");
        txtCodigo.requestFocus(); // volta o foco pro primeiro campo
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblCodigo = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();
        lblValor = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        txtNome = new javax.swing.JTextField();
        txtValor = new javax.swing.JTextField();
        btnConfirmar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblTitulo.setText("Novo Livro");

        lblCodigo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblCodigo.setText("Código:");

        lblNome.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblNome.setText("Nome:");

        lblValor.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblValor.setText("Valor:");

        txtCodigo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        txtNome.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        txtValor.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

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
                                    .addComponent(lblCodigo)
                                    .addComponent(lblNome, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblValor, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(79, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblTitulo)
                .addGap(57, 57, 57)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCodigo)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNome)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblValor)
                    .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(59, 59, 59)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirmar)
                    .addComponent(btnLimpar))
                .addContainerGap(62, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        addLivro();
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
        java.awt.EventQueue.invokeLater(() -> new LivroForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblValor;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtValor;
    // End of variables declaration//GEN-END:variables
}
