package optimal_route.util;

import optimal_route.contract.StationNode;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.util.*;

public class BuslineUtil {

     static StationNode getMin(HashSet<StationNode> visited, HashMap<StationNode,Integer> dist){
        int min = Integer.MAX_VALUE;
        StationNode u = null;
        for(StationNode n:visited){
            if(dist.get(n)<min){
                min=dist.get(n);
                u = n;
            }
        }
        return u;
    }

    private static double euclidianDistance(int x1,int x2,int y1,int y2){
        return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    }
    public static double distBetweenNodes(StationNode n1,StationNode n2){
        int x1=(int)n1.getApparentCoordinate().getX();
        int y1=(int)n1.getApparentCoordinate().getY();
        int x2=(int)n2.getApparentCoordinate().getX();
        int y2=(int)n2.getApparentCoordinate().getY();
        return euclidianDistance(x1,x2,y1,y2);
    }

    public static String getMax(HashMap<String,Integer>map, List<String> list){
        int max=-1;
        String maxString=null;
        for(String s:list){
            if(map.get(s)>max){
                max=map.get(s);
                maxString=s;
            }
        }
        return maxString;
    }

    public static List<String> intersection(StationNode s1,StationNode s2){
        List<String> list=new ArrayList<>();
        List<String> bus1=s1.getBuslinesPassingThrough();
        List<String> bus2=s2.getBuslinesPassingThrough();
        Collections.sort(bus1);
        Collections.sort(bus2);
        List<String> visited=new ArrayList<>();
        for(String s:bus1){
            if(bus2.contains(s) && !visited.contains(s)){
                list.add(s);
                visited.add(s);
            }
        }
        return list;
    }


    public static Object Dijkstra(List<StationNode> nodes, String source, String destination, boolean verbose) {
         Object returnValue = null;
        HashMap<StationNode, Integer> dist = new HashMap<>();
        HashMap<StationNode, StationNode> prev = new HashMap<>();
        HashSet<StationNode> visited = new HashSet<>();
        StationNode start = null;
        StationNode end = null;
        Stack<StationNode> stack = new Stack<>();
        Stack<StationNode> result = new Stack<>();
        for (StationNode v : nodes) {
            dist.put(v, Integer.MAX_VALUE);
            prev.put(v, null);
            visited.add(v);
            if (v.getStationName().equals(source))
                start = v;
            if (v.getStationName().equals(destination))
                end = v;
        }
        dist.replace(start, 0);

        boolean foundSolution = false;
        while (!visited.isEmpty()) {
            StationNode u = getMin(visited, dist);
            if (u == null)
                break;
            visited.remove(u);
            if (u.getStationName().equals(destination)) {
                foundSolution = true;
                break;
            }
            for (StationNode v : u.getNeighbors()) {
                double val = dist.get(u) + distBetweenNodes(u, v);
                if (val < dist.get(v)) {
                    dist.replace(v, (int) val);
                    prev.replace(v, u);

                }
            }
        }
        if (foundSolution) {
            StationNode tmp = end;
            HashMap<String, Integer> aps = new HashMap<>();
            while (tmp != start) {
                stack.push(tmp);
                result.push(tmp);
                for (String s : tmp.getBuslinesPassingThrough()) {
                    if (aps.containsKey(s)) {
                        aps.replace(s, aps.get(s) + 1);
                    } else
                        aps.put(s, 1);
                }
                tmp = prev.get(tmp);
            }
            result.push(start);
            for (String s : start.getBuslinesPassingThrough()) {
                if (aps.containsKey(s)) {
                    aps.replace(s, aps.get(s) + 1);
                } else
                    aps.put(s, 1);
            }
            String message = start.getStationName();
            StationNode s2 = start;
            while (!stack.empty()) {
                StationNode s = stack.pop();
                String currentSelected = getMax(aps, intersection(s, s2));
                message += " --(" + currentSelected + ")-- " + s.getStationName();
                s2 = s;
            }
            if(verbose==true){
                returnValue = new ImmutablePair<>(result, dist);
            }else{
                returnValue = message;
            }
            //JOptionPane.showMessageDialog(null, message);
            //return new ImmutablePair<>(result, dist);
        } else {
            //JOptionPane.showMessageDialog(null, "No route found");
            if(verbose==true){
                returnValue=null;
            }else
                returnValue="No route found";
        }
        return returnValue;
    }
}
