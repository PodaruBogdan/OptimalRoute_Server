import optimal_route.contract.Busline;
import optimal_route.contract.IBuslinePersistency;

import java.rmi.RemoteException;
import java.util.List;

public class BuslinePersistency implements IBuslinePersistency {
    @Override
    public List<Busline> getAll() throws RemoteException {
        return null;
    }

    @Override
    public Busline getById(int id) throws RemoteException {
        return null;
    }

    @Override
    public void insert(Busline busline) throws RemoteException {

    }

    @Override
    public void update(Busline busline) throws RemoteException {

    }

    @Override
    public void delete(int id) throws RemoteException {

    }
}
