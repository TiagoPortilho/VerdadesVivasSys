package verdadesvivassys.dao;

public class DAOFactory {
    private static final LivrosDAO livrosDAO = new LivrosDAO();
    private static final ClientesDAO clientesDAO = new ClientesDAO();
    private static final VendasDAO vendasDAO = new VendasDAO();
    
    public static LivrosDAO getLivrosDAO(){
        return livrosDAO;
    }
    
    public static ClientesDAO getClientesDAO(){
        return clientesDAO;
    }
    
    public static VendasDAO getVendasDAO(){
        return vendasDAO;
    }
}
