import optimal_route.contract.IStationNodePersistency;
import optimal_route.contract.StationNode;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.rmi.RemoteException;
import java.util.*;

public class StationNodePersistency implements IStationNodePersistency {
    @Override
    public List<StationNode> getAll() throws RemoteException {
        List<StationNode> stations=new ArrayList<>();
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<StationNode> criteria = builder.createQuery(StationNode.class);
        criteria.from(StationNode.class);
        stations = entityManager.createQuery(criteria).getResultList();
        for(StationNode stationNode:stations){
            Hibernate.initialize(stationNode.getBuslinesPassingThrough());
            Hibernate.initialize(stationNode.getNeighbors());
        }
        return stations;
    }

    @Override
    public StationNode getById(int id) throws RemoteException {
        EntityManager entityManager = null;
        StationNode stationNode=null;
        try{
            entityManager = EntityManagerUtil.getEntityManager();
            stationNode = entityManager.find(StationNode.class, id);
            Hibernate.initialize(stationNode.getBuslinesPassingThrough());
            Hibernate.initialize(stationNode.getNeighbors());
        }catch(HibernateException e){
            System.out.println("Cannot read...");
        }
        return stationNode;
    }
    public StationNode getByName(String name) throws RemoteException {
        EntityManager entityManager = null;
        StationNode stationNode=null;
        try {
            entityManager = EntityManagerUtil.getEntityManager();
            stationNode = (StationNode) entityManager.createQuery("SELECT s from StationNode s where s.stationName = :name")
                    .setParameter("name", name)
                    .getSingleResult();
            Hibernate.initialize(stationNode.getBuslinesPassingThrough());
            Hibernate.initialize(stationNode.getNeighbors());
        }catch (NoResultException e){
            System.out.println("No result...");
        }catch(RollbackException e){
            System.out.println("Cannot read...");
        }
        return stationNode;
    }

    @Override
    public void insert(StationNode stationNode) throws RemoteException {
        EntityManager entityManager = null;
        try {
            entityManager= EntityManagerUtil.getEntityManager();
            entityManager.getTransaction().begin();
            Hibernate.initialize(stationNode.getBuslinesPassingThrough());
            Hibernate.initialize(stationNode.getNeighbors());
            entityManager.persist(stationNode);
            entityManager.getTransaction().commit();

        }catch (RollbackException e){
            System.out.println("Cannot insert");
        }
    }

    @Override
    public void update(StationNode stationNode) throws RemoteException {
        EntityManager entityManager = null;
        try {
            entityManager= EntityManagerUtil.getEntityManager();
            entityManager.getTransaction().begin();
            Hibernate.initialize(stationNode.getBuslinesPassingThrough());
            Hibernate.initialize(stationNode.getNeighbors());
            entityManager.merge(stationNode);
            entityManager.getTransaction().commit();

        }catch (HibernateException e){
            System.out.println("Cannot update...");
        }
    }

    @Override
    public void delete(int id) throws RemoteException {
        EntityManager entityManager = null;
        try {
            entityManager= EntityManagerUtil.getEntityManager();
            entityManager.getTransaction().begin();
            StationNode stationNode = entityManager.find(StationNode.class, id);
            List<StationNode> stationList = stationNode.getNeighbors();
            for(Iterator<StationNode> it=stationList.iterator();it.hasNext();){
                StationNode s = it.next();
                for(Iterator<StationNode> st=s.getNeighbors().iterator();st.hasNext();){
                    StationNode t= st.next();
                    if(t.getStationName().equals(stationNode.getStationName()))
                        st.remove();
                }
            }
            stationNode.setBuslinesPassingThrough(new ArrayList<>());
            update(stationNode);
            Hibernate.initialize(stationNode.getBuslinesPassingThrough());
            Hibernate.initialize(stationNode.getNeighbors());
            entityManager.remove(stationNode);
            entityManager.getTransaction().commit();

        }catch (HibernateException e){
            System.out.println("Cannot delete...");
        }
    }

    @Override
    public void writeAll(List<StationNode> list) throws RemoteException {
        if(list!=null) {
            List<StationNode> all = getAll();
            for(StationNode s:all){
                boolean found=false;
                for(StationNode n:list){
                    if(s.getStationName().equals(n.getStationName())){
                        found=true;
                    }
                }
                if(found==false){
                    delete(s.getId());
                }
            }
            Map<String,StationNode> map = new HashMap<>();
            for(StationNode stationNode:list){
                StationNode newStationNode=new StationNode(stationNode.getStationName(),stationNode.getApparentCoordinate());
                StationNode tmp = getByName(newStationNode.getStationName());
                if(tmp==null) {
                    insert(newStationNode);
                }
                map.put(newStationNode.getStationName(),stationNode);
            }
            List<StationNode> allData = getAll();
            for(StationNode stationNode:allData){
                for(StationNode neighbor:map.get(stationNode.getStationName()).getNeighbors()){
                    StationNode stationNode1 = getByName(neighbor.getStationName());
                    System.out.println("Trying to insert pair ("+stationNode.getId()+","+stationNode1.getId()+")");
                    stationNode.addNeighbor(stationNode1);
                }
                for(String blpt :map.get(stationNode.getStationName()).getBuslinesPassingThrough()){
                    stationNode.addBusline(blpt);
                }
                update(stationNode);
            }

        }
         /*
        for(StationNode stationNode:list){
            System.out.println("NAME: "+stationNode.getStationName());
            System.out.print("Buses: ");
            for(String s:stationNode.getBuslinesPassingThrough()){
                System.out.print(s+" ");
            }
            System.out.print("Neighbors: ");
            for(StationNode s :stationNode.getNeighbors()){
                System.out.print(s.getStationName()+" ");
            }
            System.out.println();
        }*/
    }
}
