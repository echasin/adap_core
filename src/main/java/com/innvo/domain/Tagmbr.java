package com.innvo.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Tagmbr.
 */
@Entity
@Table(name = "tagmbr")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "tagmbr")
public class Tagmbr implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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
    private Strategy strategytagmbrlhs;

    @ManyToOne
    @NotNull
    private Tag tagrhs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Strategy getStrategytagmbrlhs() {
        return strategytagmbrlhs;
    }

    public void setStrategytagmbrlhs(Strategy strategy) {
        this.strategytagmbrlhs = strategy;
    }

    public Tag getTagrhs() {
        return tagrhs;
    }

    public void setTagrhs(Tag tag) {
        this.tagrhs = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tagmbr tagmbr = (Tagmbr) o;
        if(tagmbr.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, tagmbr.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Tagmbr{" +
            "id=" + id +
            ", status='" + status + "'" +
            ", lastmodifiedby='" + lastmodifiedby + "'" +
            ", lastmodifieddatetime='" + lastmodifieddatetime + "'" +
            ", domain='" + domain + "'" +
            '}';
    }
}
