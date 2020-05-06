import optimal_route.contract.Account;
import optimal_route.contract.IAccountPersistency;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.rmi.RemoteException;
import java.util.List;

public class AccountPersistency implements IAccountPersistency {

    public List<Account> getAll() throws RemoteException {
        List<Account> accounts=null;
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
        criteria.from(Account.class);
        accounts = entityManager.createQuery(criteria).getResultList();
        return accounts;

    }

    public Account getById(int id){
        EntityManager entityManager = null;
        Account account=null;
        try{
            entityManager = EntityManagerUtil.getEntityManager();
            account = entityManager.find(Account.class, id);
        }catch(HibernateException e){
            System.out.println("Cannot read...");
        }
        return account;
    }

    public void insert(Account account){
        EntityManager entityManager = null;
        try {
            entityManager= EntityManagerUtil.getEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(account);
            entityManager.getTransaction().commit();

        }catch (RollbackException e){
            System.out.println("Cannot insert...");
        }
    }


    public void update(Account account){
        EntityManager entityManager = null;
        try {
            entityManager= EntityManagerUtil.getEntityManager();
            entityManager.getTransaction().begin();
            entityManager.merge(account);
            entityManager.getTransaction().commit();

        }catch (HibernateException e){
            System.out.println("Cannot update...");
        }
    }


    public void delete(int id){
        EntityManager entityManager = null;
        try {
            entityManager= EntityManagerUtil.getEntityManager();
            entityManager.getTransaction().begin();
            Account account = entityManager.find(Account.class, id);
            if(account!=null)
                entityManager.remove(account);
            entityManager.getTransaction().commit();

        }catch (HibernateException e){
            System.out.println("Cannot delete...");
        }
    }
}
