package optimal_route.contract;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IAccountPersistency extends Remote {
    List<Account> getAll() throws RemoteException;
    Account getById(int id) throws RemoteException;
    void insert(Account account) throws RemoteException;
    void update(Account account) throws RemoteException;
    void delete(int id) throws RemoteException;
}
