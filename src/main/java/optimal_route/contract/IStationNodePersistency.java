package optimal_route.contract;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IStationNodePersistency extends Remote {
    List<StationNode> getAll() throws RemoteException;
    StationNode getById(int id) throws RemoteException;
    StationNode getByName(String name) throws RemoteException;
    void insert(StationNode stationNode) throws RemoteException;
    void update(StationNode stationNode) throws RemoteException;
    void delete(int id) throws RemoteException;
    void writeAll(List<StationNode> list) throws RemoteException;
}
