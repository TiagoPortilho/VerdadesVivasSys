package verdadesvivassys.model;

import java.util.List;
import java.util.ArrayList;

public class Venda {

    private int id;
    private Cliente cliente;
    private List<Livro> livros = new ArrayList<>();
    
    public Venda(){}

    public Venda(int id, Cliente cliente, List<Livro> livros) {
        this.id = id;
        this.cliente = cliente;
        this.livros = livros;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }
}
