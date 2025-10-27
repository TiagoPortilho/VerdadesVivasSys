package verdadesvivassys.model;

public class Cliente {

    private int id;
    private String Nome;
    private String Cidade;

    public Cliente() {}

    public Cliente(int id, String nome, String cidade) {
        this.id = id;
        Nome = nome;
        Cidade = cidade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getCidade() {
        return Cidade;
    }

    public void setCidade(String cidade) {
        Cidade = cidade;
    }
}
