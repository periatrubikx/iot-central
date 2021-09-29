package io.rubikx.rubikxcore.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A ShiftManagement.
 */
@Entity
@Table(name = "shift_management")
public class ShiftManagement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "shift_name", nullable = false)
    private String shiftName;

    @Column(name = "area_name")
    private String areaName;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private ZonedDateTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private ZonedDateTime endTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ShiftManagement id(Long id) {
        this.id = id;
        return this;
    }

    public String getShiftName() {
        return this.shiftName;
    }

    public ShiftManagement shiftName(String shiftName) {
        this.shiftName = shiftName;
        return this;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getAreaName() {
        return this.areaName;
    }

    public ShiftManagement areaName(String areaName) {
        this.areaName = areaName;
        return this;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public ZonedDateTime getStartTime() {
        return this.startTime;
    }

    public ShiftManagement startTime(ZonedDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return this.endTime;
    }

    public ShiftManagement endTime(ZonedDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShiftManagement)) {
            return false;
        }
        return id != null && id.equals(((ShiftManagement) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShiftManagement{" +
            "id=" + getId() +
            ", shiftName='" + getShiftName() + "'" +
            ", areaName='" + getAreaName() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }
}
