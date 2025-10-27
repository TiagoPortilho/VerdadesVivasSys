package verdadesvivassys.model;

public class Livro {

    private int id;
    private String codigo;
    private String Nome;
    private float Valor;

    public Livro(){}

    public Livro(int id, String codigo, String nome, float valor) {
        this.id = id;
        this.codigo = codigo;
        Nome = nome;
        Valor = valor;
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
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public float getValor() {
        return Valor;
    }

    public void setValor(float valor) {
        Valor = valor;
    }
}
