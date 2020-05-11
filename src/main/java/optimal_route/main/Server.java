package optimal_route.main;

import optimal_route.contract.Account;
import optimal_route.contract.IAccountPersistency;
import optimal_route.contract.IStationNodePersistency;
import optimal_route.persistency.AccountPersistency;
import optimal_route.persistency.StationNodePersistency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.ServerSocket;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class Server {

    private static int normal_port = 9876;
    private static final int RMI_PORT = 2020;

    public static void main(String[] args) {
         /*if(args.length!=1){
             System.err.println("Please provide port number");
         }
         int portNumber = Integer.parseInt(args[0]);*/
        IAccountPersistency accountPersistency = new AccountPersistency();
        IStationNodePersistency stationNodePersistency = new StationNodePersistency();
        IAccountPersistency stub = null;
        IStationNodePersistency stubForStations=null;
        Registry registry=null;
        try {
            //accountPersistency.insert(new Account.AccountBuilder().username("admin").pswd("1234").role("admin").build());
            //accountPersistency.insert(new Account.AccountBuilder().username("emp").pswd("1234").role("employee").build());

            stub = (IAccountPersistency) UnicastRemoteObject.exportObject(accountPersistency, 0);
            stubForStations = (IStationNodePersistency) UnicastRemoteObject.exportObject(stationNodePersistency, 1);
            registry=LocateRegistry.createRegistry(RMI_PORT);
            registry.bind("optimal_route.contract.IAccountPersistency", stub);
            registry.bind("optimal_route.contract.IStationNodePersistency", stubForStations);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(normal_port);
        }catch (IOException e){
            System.err.println("Can't listen on port...");
        }
        Scanner s=new Scanner(System.in);
        while (true){
            if(s.equals("stop")){
                try {
                    UnicastRemoteObject.unexportObject(registry, true);
                } catch (NoSuchObjectException e) {
                    e.printStackTrace();
                }
                break;
            }
            ClientWorker worker;
            try {
                worker=new ClientWorker(serverSocket.accept(),stub,stubForStations);
                Thread t = new Thread(worker);
                t.start();
            }catch (IOException e){
                System.err.println("Accept failed");
            }
        }


    }
}
