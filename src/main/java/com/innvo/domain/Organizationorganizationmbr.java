package com.innvo.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Organizationorganizationmbr.
 */
@Entity
@Table(name = "organizationorganizationmbr")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "organizationorganizationmbr")
public class Organizationorganizationmbr implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 255)
    @Column(name = "comment", length = 255)
    private String comment;

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
    private Organization organizationlhs;

    @ManyToOne
    @NotNull
    private Organization organizationrhs;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "organizationorganizationmbr_category",
               joinColumns = @JoinColumn(name="organizationorganizationmbrs_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="categories_id", referencedColumnName="ID"))
    private Set<Category> categories = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public Organization getOrganizationlhs() {
        return organizationlhs;
    }

    public void setOrganizationlhs(Organization organization) {
        this.organizationlhs = organization;
    }

    public Organization getOrganizationrhs() {
        return organizationrhs;
    }

    public void setOrganizationrhs(Organization organization) {
        this.organizationrhs = organization;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Organizationorganizationmbr organizationorganizationmbr = (Organizationorganizationmbr) o;
        if(organizationorganizationmbr.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, organizationorganizationmbr.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Organizationorganizationmbr{" +
            "id=" + id +
            ", comment='" + comment + "'" +
            ", status='" + status + "'" +
            ", lastmodifiedby='" + lastmodifiedby + "'" +
            ", lastmodifieddatetime='" + lastmodifieddatetime + "'" +
            ", domain='" + domain + "'" +
            '}';
    }
}
