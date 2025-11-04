package verdadesvivassys.dao;

public class DAOFactory {
    private static final LivrosDAO livrosDAO = new LivrosDAO();
    private static final ClientesDAO clientesDAO = new ClientesDAO();
    
    public static LivrosDAO getLivrosDAO(){
        return livrosDAO;
    }
    
    public static ClientesDAO getClientesDAO(){
        return clientesDAO;
    }
}
