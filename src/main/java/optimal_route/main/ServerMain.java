package optimal_route.main;

import optimal_route.contract.Account;
import optimal_route.contract.IAccountPersistency;
import optimal_route.contract.IStationNodePersistency;
import optimal_route.contract.StationNode;
import optimal_route.persistency.AccountPersistency;
import optimal_route.persistency.StationNodePersistency;
import optimal_route.util.BuslineUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NoSuchObjectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.List;

public class ServerMain implements Runnable {

    private static final int RMI_PORT = 2020;
    private static final int RMI_PORT2 = 2021;
    private static ServerSocket serverSocket;
    private static int port = 9876;
    private Registry registry;
    private boolean stop;
    private IStationNodePersistency stubForStations;
    private IAccountPersistency stub;
    ServerMain(Registry registry,IAccountPersistency stub,IStationNodePersistency stubForStations) {
        this.registry = registry;
        stop = false;
        this.stub=stub;
        this.stubForStations=stubForStations;
    }

    public static void main(String args[]) {
        try {

            IAccountPersistency accountPersistency = new AccountPersistency();
            IStationNodePersistency stationNodePersistency = new StationNodePersistency();

           /* StationNode stationNode1 = new StationNode("S1", new Point(12, 12));
            StationNode stationNode2 = new StationNode("S2", new Point(22, 22));
            stationNode1.getBuslinesPassingThrough().add("35");
            stationNode1.getBuslinesPassingThrough().add("32");
            stationNode2.getBuslinesPassingThrough().add("35");
            stationNode2.getBuslinesPassingThrough().add("32");
            stationNodePersistency.insert(stationNode1);
            stationNodePersistency.insert(stationNode2);
            stationNode1=stationNodePersistency.getByName("S1");
            stationNode2=stationNodePersistency.getByName("S2");

            stationNode1.addNeighbor(stationNode2);
            stationNode2.addNeighbor(stationNode1);
            List<StationNode> list=new ArrayList<>();
            list.add(stationNode1);
            list.add(stationNode2);

            stationNodePersistency.writeAll(list);


            StationNode st = stationNodePersistency.getByName("s235");
            if(st==null) {
                System.out.println("NULL in server");
            }else {
                System.out.println("NOT NULL in server");
            }*/



            accountPersistency.insert(new Account.AccountBuilder().username("bogdan").pswd("1234").role("admin").build());

            //for tests
            ///StationNode s1 = stationNodePersistency.getById(1);
            // for(String s:s1.getBuslinesPassingThrough()){
            //System.out.println(s);
            //}

            //stationNodePersistency.delete(1);


            IAccountPersistency stub = (IAccountPersistency) UnicastRemoteObject.exportObject(accountPersistency, 0);
            IStationNodePersistency stubForStations = (IStationNodePersistency) UnicastRemoteObject.exportObject(stationNodePersistency, 1);
            ServerMain server = new ServerMain(LocateRegistry.createRegistry(RMI_PORT),stub,stubForStations);
            server.registry.bind("optimal_route.contract.IAccountPersistency", stub);
            server.registry.bind("optimal_route.contract.IStationNodePersistency", stubForStations);
            Thread thread = new Thread(server);
            serverSocket = new ServerSocket(port);
            thread.start();
            System.err.println("Server ready");
            Scanner s = new Scanner(System.in);
            while (!s.next().equals("stop")) ;
            server.stop();
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }


    }

    public void run() {
        Socket socket= null;
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {
            socket = serverSocket.accept();
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (stop == false) {
            try {
                System.out.println("ACCEPTING MESSAGE...");
                //socket = serverSocket.accept();
                //oos=socket.getOutputStream();
                Object[] read = (Object[]) ois.readObject();
                System.err.println("SERVER.1");
                String type = (String) read[0];
                int nrArgs = (int)read[1];
                List<Object> args = new ArrayList<>();
                for(int i=2;i<nrArgs+2;i++){
                    args.add(read[i]);
                }
                switch (type){
                    case "dijkstra":
                        System.out.println("Calling Dijkstra()...");
                        List<StationNode> stationNodes = stubForStations.getAll();
                        boolean verbose = (boolean)read[2];
                        if(verbose==false) {
                           String res = (String) BuslineUtil.Dijkstra(stationNodes, (String) read[3], (String) read[4], verbose);
                           oos.writeObject(new Object[]{res});
                            System.err.println("SERVER.2");
                        }else{
                            Pair<Stack<StationNode>, HashMap<StationNode, Integer>> res = (Pair<Stack<StationNode>, HashMap<StationNode, Integer>>) BuslineUtil.Dijkstra(stationNodes, (String) read[3], (String) read[4], verbose);
                            oos.writeObject(res);
                            System.err.println("SERVER.2");
                        }
                        break;
                    default:break;
                }
            } catch (Exception e) {
                //System.out.println("EXCEPTION!!!!");
            }
        }
        try {
            ois.close();
            oos.close();
            socket.close();
            UnicastRemoteObject.unexportObject(registry, true);
        } catch (NoSuchObjectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() throws NoSuchObjectException {
        System.out.println("Shutting down server");
        UnicastRemoteObject.unexportObject(registry, true);
        stop = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }


}
