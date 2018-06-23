package gabtemp.vanhackathon.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;

@Entity
@Table(name = "RESTAURANT")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RESTAURANT_SEQ")
    @SequenceGenerator(name = "RESTAURANT_SEQ", sequenceName = "RESTAURANT_SEQ")
    @Column(name = "RESTAURANT_ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @ElementCollection(targetClass = DayOfWeek.class, fetch = FetchType.EAGER)
    @Column(name = "PICK_UP_DAYS", nullable = false)
    private Set<DayOfWeek> availablePickUpDays;

    @Column(name = "PICK_UP_TIME", nullable = false)
    private LocalTime pickUpTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<DayOfWeek> getAvailablePickUpDays() {
        return availablePickUpDays;
    }

    public void setAvailablePickUpDays(Set<DayOfWeek> availablePickUpDays) {
        this.availablePickUpDays = availablePickUpDays;
    }

    public LocalTime getPickUpTime() {
        return pickUpTime;
    }

    public void setPickUpTime(LocalTime pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Restaurant that = (Restaurant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("id", id)
                          .add("name", name)
                          .add("address", address)
                          .add("availablePickUpDays", availablePickUpDays)
                          .add("pickUpTime", pickUpTime)
                          .toString();
    }
}
