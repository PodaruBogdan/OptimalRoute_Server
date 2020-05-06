package optimal_route.contract;

import org.hibernate.FetchMode;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;

import javax.persistence.Table;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

@Entity
@Embeddable
@Table(name = "stations",uniqueConstraints = @UniqueConstraint(name="uniqueStationCons" , columnNames = {"stationName"}))
public class StationNode implements Serializable {
    @Id
    @GeneratedValue(generator = "incrementor")
    @GenericGenerator(name="incrementor",strategy = "increment")
    @Column(name = "station_id")
    @Cascade(value = CascadeType.ALL)
    private int id;
    @Column(name = "stationName")
    private String stationName;
    @Column(name = "realCoordinate")
    private Point realCoordinates;
    @Column(name = "apparentCoordinate")
    private Point apparentCoordinate;
    @ElementCollection
    @CollectionTable(name="blpt", joinColumns=@JoinColumn(name="station_id"))
    @JoinColumn
    @Cascade(value = CascadeType.ALL)
    private List<String> buslinesPassingThrough;
    @CollectionId(columns = @Column(name="id"), type=@Type(type="int"), generator = "incrementor")
    @CollectionTable(name="neighbors",joinColumns = @JoinColumn(name = "neighbor_id"), uniqueConstraints = @UniqueConstraint(name="UK_lgwe1i4ac40dt5r45a91p64my",columnNames = {"neighbor_id","neighbors_station_id"}))
    //@JoinColumn
    @ElementCollection
    @Cascade(value = CascadeType.ALL)
    // @Cascade(CascadeType.ALL)
    private List<StationNode> neighbors;



    public StationNode(){}

    public StationNode(String stationName, Point apparentCoordinate) {
        this.stationName = stationName;
        this.apparentCoordinate = apparentCoordinate;
        neighbors=new ArrayList<StationNode>();
        buslinesPassingThrough=new ArrayList<String>();
    }
    public StationNode(String stationName, Point apparentCoordinate, List<StationNode> neighbors) {
        this.stationName = stationName;
        this.apparentCoordinate = apparentCoordinate;
        this.neighbors = neighbors;
        buslinesPassingThrough=new ArrayList<String>();
    }
    public void addBusline(String busline){
        if(!buslinesPassingThrough.contains(busline))
            buslinesPassingThrough.add(busline);

    }

    public void addNeighbor(StationNode neighbor){
        boolean found = false;
        for(StationNode n : neighbors){
            if(n.getStationName().equals(neighbor.stationName)){
                found=true;
            }
        }
        if(found==false){
            neighbors.add(neighbor);
        }
        found=false;
        for(StationNode n:neighbor.getNeighbors()){
            if(n.getStationName().equals(stationName)){
                found=true;
            }
        }
        if(found==false){
            neighbor.addNeighbor(this);
        }
    }

    public void removeNeighbors(){
        List<StationNode> neighbors = getNeighbors();

        for(StationNode neighbor: neighbors){
            List<StationNode> recNeighbors = new ArrayList<>(neighbor.getNeighbors());
            recNeighbors.remove(this);
            neighbor.setNeighbors(recNeighbors);
        }
        this.neighbors = new ArrayList<>();

    }
    public List<StationNode> getNeighbors(){
        return neighbors;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Point2D getRealCoordinates() {
        return realCoordinates;
    }

    public void setRealCoordinates(Point realCoordinates) {
        this.realCoordinates = realCoordinates;
    }

    public Point getApparentCoordinate() {
        return apparentCoordinate;
    }

    public void setApparentCoordinate(Point apparentCoordinate) {
        this.apparentCoordinate = apparentCoordinate;
    }

    public List<String> getBuslinesPassingThrough() {
        return buslinesPassingThrough;
    }

    public void setBuslinesPassingThrough(List<String> busLines) {
        this.buslinesPassingThrough = busLines;
    }

    public void setNeighbors(List<StationNode> neighbors) {
        this.neighbors = neighbors;
    }

    public String toString(){
        String neighbors = "";
        for(StationNode s:this.getNeighbors()){
            neighbors+= s.getStationName()+";";
        }
        String buses="";
        for(String s:buslinesPassingThrough){
            buses+=s+";";
        }
        return id+","+stationName+","+apparentCoordinate+","+neighbors+","+buses;
    }



}

