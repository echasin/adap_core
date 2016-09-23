package com.innvo.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import com.innvo.domain.enumeration.Ruledirectiontype;

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
    @Column(name = "ruledirectiontype", nullable = false)
    private Ruledirectiontype ruledirectiontype;

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

    @NotNull
    @Size(max = 25)
    @Column(name = "status", length = 25, nullable = false)
    private String status;

    @NotNull
    @Size(max = 50)
    @Column(name = "lastmodifiedby", length = 50, nullable = false)
    private String lastmodifiedby;

    @NotNull
    @Column(name = "lastmodifieddatetime", nullable = false)
    private ZonedDateTime lastmodifieddatetime;

    @NotNull
    @Size(max = 25)
    @Column(name = "domain", length = 25, nullable = false)
    private String domain;

    @ManyToOne
    @NotNull
    private Securitygroup securitygroup;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ruledirectiontype getRuledirectiontype() {
        return ruledirectiontype;
    }

    public void setRuledirectiontype(Ruledirectiontype ruledirectiontype) {
        this.ruledirectiontype = ruledirectiontype;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastmodifiedby() {
        return lastmodifiedby;
    }

    public void setLastmodifiedby(String lastmodifiedby) {
        this.lastmodifiedby = lastmodifiedby;
    }

    public ZonedDateTime getLastmodifieddatetime() {
        return lastmodifieddatetime;
    }

    public void setLastmodifieddatetime(ZonedDateTime lastmodifieddatetime) {
        this.lastmodifieddatetime = lastmodifieddatetime;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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
            ", ruledirectiontype='" + ruledirectiontype + "'" +
            ", ruletype='" + ruletype + "'" +
            ", protocol='" + protocol + "'" +
            ", iprange='" + iprange + "'" +
            ", fromport='" + fromport + "'" +
            ", toport='" + toport + "'" +
            ", status='" + status + "'" +
            ", lastmodifiedby='" + lastmodifiedby + "'" +
            ", lastmodifieddatetime='" + lastmodifieddatetime + "'" +
            ", domain='" + domain + "'" +
            '}';
    }
}
