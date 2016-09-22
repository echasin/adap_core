package com.innvo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Securitygroup.
 */
@Entity
@Table(name = "securitygroup")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "securitygroup")
public class Securitygroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Size(max = 255)
    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Size(max = 25)
    @Column(name = "groupid", length = 25)
    private String groupid;

    @Size(max = 25)
    @Column(name = "vpcid", length = 25)
    private String vpcid;

    @OneToMany(mappedBy = "securitygroup")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Securitygrouprule> securitygrouprules = new HashSet<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getVpcid() {
        return vpcid;
    }

    public void setVpcid(String vpcid) {
        this.vpcid = vpcid;
    }

    public Set<Securitygrouprule> getSecuritygrouprules() {
        return securitygrouprules;
    }

    public void setSecuritygrouprules(Set<Securitygrouprule> securitygrouprules) {
        this.securitygrouprules = securitygrouprules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Securitygroup securitygroup = (Securitygroup) o;
        if(securitygroup.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, securitygroup.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Securitygroup{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            ", groupid='" + groupid + "'" +
            ", vpcid='" + vpcid + "'" +
            '}';
    }
}
