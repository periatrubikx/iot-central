package io.rubikx.rubikxcore.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.rubikx.rubikxcore.domain.ShiftManagement} entity.
 */
public class ShiftManagementDTO implements Serializable {

    private Long id;

    @NotNull
    private String shiftName;

    private String areaName;

    @NotNull
    private ZonedDateTime startTime;

    @NotNull
    private ZonedDateTime endTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShiftManagementDTO)) {
            return false;
        }

        ShiftManagementDTO shiftManagementDTO = (ShiftManagementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shiftManagementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShiftManagementDTO{" +
            "id=" + getId() +
            ", shiftName='" + getShiftName() + "'" +
            ", areaName='" + getAreaName() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }
}
