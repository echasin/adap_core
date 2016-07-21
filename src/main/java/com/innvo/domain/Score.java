package com.innvo.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import java.time.ZonedDateTime;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import com.innvo.json.StringJsonUserType;
import com.innvo.domain.Status;

/**
 * A Score.
 */
@Entity
@Table(name = "score")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "score")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = StringJsonUserType.class)})
public class Score implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "value")
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private Double value;

    @Size(max = 50)
    @Column(name = "text", length = 50)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String text;

    @Size(max = 100)
    @Column(name = "rulefilename", length = 100)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String rulefilename;
    
    @Size(max = 100)
    @Column(name = "rulename", length = 100)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String rulename;

    @Column(name = "runid")
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String runid;
    
    @Column(name = "rundate", nullable = false)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private ZonedDateTime rundate;


    @Size(max = 25)
    @Column(name = "ruleversion", length = 25)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String ruleversion;

    @Column(name = "details")
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    @Type(type = "StringJsonObject")
    private String details;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private Status status;

    @Column(name = "lastmodifiedby")
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String lastmodifiedby;

    @Column(name = "lastmodifieddate", nullable = false)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private ZonedDateTime lastmodifieddate;

    @Column(name = "domain")
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String domain;


    @ManyToOne
    private Asset asset;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRulename() {
        return rulename;
    }

    public void setRulename(String rulename) {
        this.rulename = rulename;
    }

    public String getRuleversion() {
        return ruleversion;
    }

    public void setRuleversion(String ruleversion) {
        this.ruleversion = ruleversion;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getLastmodifiedby() {
        return lastmodifiedby;
    }

    public void setLastmodifiedby(String lastmodifiedby) {
        this.lastmodifiedby = lastmodifiedby;
    }

    public ZonedDateTime getLastmodifieddate() {
        return lastmodifieddate;
    }

    public void setLastmodifieddate(ZonedDateTime lastmodifieddate) {
        this.lastmodifieddate = lastmodifieddate;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Score score = (Score) o;

        if ( ! Objects.equals(id, score.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Score{" +
            "id=" + id +
            ", value='" + value + "'" +
            ", text='" + text + "'" +
            ", rulename='" + rulename + "'" +
            ", ruleversion='" + ruleversion + "'" +
            ", details='" + details + "'" +
            ", status='" + status + "'" +
            ", lastmodifiedby='" + lastmodifiedby + "'" +
            ", lastmodifieddate='" + lastmodifieddate + "'" +
            ", domain='" + domain + "'" +
            '}';
    }

    /**
     * @param runid the runid to set
     */
    public void setRunid(String runid) {
        this.runid = runid;
    }

    /**
     * @param rundate the rundate to set
     */
    public void setRundate(ZonedDateTime rundate) {
        this.rundate = rundate;
    }

    /**
     * @return the runid
     */
    public String getRunid() {
        return runid;
    }

    /**
     * @return the rundate
     */
    public ZonedDateTime getRundate() {
        return rundate;
    }


    /**
     * @return the rulefilename
     */
    public String getRulefilename() {
        return rulefilename;
    }

    /**
     * @param rulefilename the rulefilename to set
     */
    public void setRulefilename(String rulefilename) {
        this.rulefilename = rulefilename;
    }
}
