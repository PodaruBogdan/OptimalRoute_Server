package optimal_route.contract;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
@Entity
@Embeddable
@Table(name = "buslines",uniqueConstraints = @UniqueConstraint(name="uniqueStationCons" , columnNames = {"busline_Name"}))

public class Busline {
    @Id
    @GeneratedValue(generator = "incrementor")
    @GenericGenerator(name="incrementor",strategy = "increment")
    @Column(name = "busline_id")
    @Cascade(value = CascadeType.ALL)
    private int id;
    @Column(name = "busline_Name")
    private String buslineName;
    @CollectionId(columns = @Column(name="id"), type=@Type(type="int"), generator = "incrementor")
    @CollectionTable(name="busline_list",joinColumns = @JoinColumn(name = "station_id"))
    //@JoinColumn
    @ElementCollection
    @Cascade(value = CascadeType.ALL)
    private List<StationNode> stations;

    public Busline(int id, String buslineName, List<StationNode> stations) {
        this.id = id;
        this.buslineName = buslineName;
        this.stations = stations;
    }

    public Busline(int id, String buslineName) {
        this.id = id;
        this.buslineName = buslineName;
        stations=new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBuslineName() {
        return buslineName;
    }

    public void setBuslineName(String buslineName) {
        this.buslineName = buslineName;
    }

    public List<StationNode> getStations() {
        return stations;
    }

    public void setStations(List<StationNode> stations) {
        this.stations = stations;
    }
}
