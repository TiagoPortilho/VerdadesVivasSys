package verdadesvivassys.model;

public class Livro {

    private int id;
    private String codigo;
    private String nome;
    private float valor;

    public Livro(){}

    public Livro(int id, String codigo, String nome, float valor) {
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.valor = valor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }
}
