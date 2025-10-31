package verdadesvivassys.model;

public class Cliente {

    private int id;
    private String Nome;
    private String Cidade;
    private String Contato;

    public Cliente() {}

    public Cliente(int id, String nome, String cidade, String contato) {
        this.id = id;
        Nome = nome;
        Cidade = cidade;
        Contato = contato;
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

    public String getContato() { return Contato; }

    public void setContato(String contato) { Contato = contato; }
}
