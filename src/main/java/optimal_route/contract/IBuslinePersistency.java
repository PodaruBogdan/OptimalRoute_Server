package optimal_route.contract;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IBuslinePersistency extends Remote {
    List<Busline> getAll() throws RemoteException;
    Busline getById(int id) throws RemoteException;
    void insert(Busline busline) throws RemoteException;
    void update(Busline busline) throws RemoteException;
    void delete(int id) throws RemoteException;
}
