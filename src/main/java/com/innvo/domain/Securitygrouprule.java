package com.innvo.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

import com.innvo.domain.enumeration.Ruletype;

import com.innvo.domain.enumeration.Protocol;

/**
 * A Securitygrouprule.
 */
@Entity
@Table(name = "securitygrouprule")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "securitygrouprule")
public class Securitygrouprule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ruletype", nullable = false)
    private Ruletype ruletype;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "protocol", nullable = false)
    private Protocol protocol;

    @Column(name = "iprange")
    private String iprange;

    @Column(name = "fromport")
    private Integer fromport;

    @Column(name = "toport")
    private Integer toport;

    @ManyToOne
    @NotNull
    private Securitygroup securitygroup;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ruletype getRuletype() {
        return ruletype;
    }

    public void setRuletype(Ruletype ruletype) {
        this.ruletype = ruletype;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getIprange() {
        return iprange;
    }

    public void setIprange(String iprange) {
        this.iprange = iprange;
    }

    public Integer getFromport() {
        return fromport;
    }

    public void setFromport(Integer fromport) {
        this.fromport = fromport;
    }

    public Integer getToport() {
        return toport;
    }

    public void setToport(Integer toport) {
        this.toport = toport;
    }

    public Securitygroup getSecuritygroup() {
        return securitygroup;
    }

    public void setSecuritygroup(Securitygroup securitygroup) {
        this.securitygroup = securitygroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Securitygrouprule securitygrouprule = (Securitygrouprule) o;
        if(securitygrouprule.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, securitygrouprule.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Securitygrouprule{" +
            "id=" + id +
            ", ruletype='" + ruletype + "'" +
            ", protocol='" + protocol + "'" +
            ", iprange='" + iprange + "'" +
            ", fromport='" + fromport + "'" +
            ", toport='" + toport + "'" +
            '}';
    }
}
