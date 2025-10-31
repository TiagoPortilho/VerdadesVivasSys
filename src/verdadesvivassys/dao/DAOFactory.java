package verdadesvivassys.dao;

public class DAOFactory {
    private static final LivrosDAO livrosDAO = new LivrosDAO();
    
    public static LivrosDAO getLivrosDAO(){
        return livrosDAO;
    }
}
