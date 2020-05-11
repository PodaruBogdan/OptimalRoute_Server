package optimal_route.main;

import optimal_route.contract.IAccountPersistency;
import optimal_route.contract.IStationNodePersistency;
import optimal_route.contract.StationNode;
import optimal_route.util.BuslineUtil;
import org.apache.commons.lang3.tuple.Pair;
import java.io.*;
import java.net.Socket;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class ClientWorker implements Runnable{

    private boolean stop;
    private IStationNodePersistency stubForStations;
    private IAccountPersistency stub;
    private Socket client;
    public ClientWorker(Socket client,IAccountPersistency stub,IStationNodePersistency stubForStations) {
        this.client = client;//who?
        stop = false;
        this.stub=stub;
        this.stubForStations=stubForStations;
    }

    @Override
    public void run() {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try{
            ois = new ObjectInputStream(client.getInputStream());
            oos = new ObjectOutputStream(client.getOutputStream());
        }catch (IOException e){
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
            client.close();
        } catch (NoSuchObjectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
