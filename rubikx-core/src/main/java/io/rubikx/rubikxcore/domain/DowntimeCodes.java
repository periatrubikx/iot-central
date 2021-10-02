package io.rubikx.rubikxcore.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A DowntimeCodes.
 */
@Entity
@Table(name = "downtime_codes")
public class DowntimeCodes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false)
    private Integer code;

    @NotNull
    @Column(name = "level", nullable = false)
    private String level;

    @Column(name = "parent_level")
    private String parent_level;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DowntimeCodes id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getCode() {
        return this.code;
    }

    public DowntimeCodes code(Integer code) {
        this.code = code;
        return this;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getLevel() {
        return this.level;
    }

    public DowntimeCodes level(String level) {
        this.level = level;
        return this;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getParent_level() {
        return this.parent_level;
    }

    public DowntimeCodes parent_level(String parent_level) {
        this.parent_level = parent_level;
        return this;
    }

    public void setParent_level(String parent_level) {
        this.parent_level = parent_level;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DowntimeCodes)) {
            return false;
        }
        return id != null && id.equals(((DowntimeCodes) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DowntimeCodes{" +
            "id=" + getId() +
            ", code=" + getCode() +
            ", level='" + getLevel() + "'" +
            ", parent_level='" + getParent_level() + "'" +
            "}";
    }
}
