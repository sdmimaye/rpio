package com.github.sdmimaye.rpio.server.database.models.system;

import com.github.sdmimaye.rpio.server.database.HibernateRevisionListener;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateConnectionConfigurator;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.ModifiedEntityNames;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "_Revision")
@RevisionEntity(HibernateRevisionListener.class)
public class RevisionEntry{
    @Id
    @GeneratedValue
    @RevisionNumber
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @RevisionTimestamp
    private Date timestamp;

    private Long userId;
    private String userName;

    @ElementCollection
    @JoinTable(name = HibernateConnectionConfigurator.TABLE_NAME_PREFIX + "_RevisionChanges", joinColumns = @JoinColumn(name = "rev"))
    @Column(name = "entityname")
    @Fetch(FetchMode.JOIN)
    @ModifiedEntityNames
    private Set<String> modifiedEntityNames = new HashSet<>();

    public Set<String> getModifiedEntityNames() {
        return modifiedEntityNames;
    }

    public void setModifiedEntityNames(Set<String> modifiedEntityNames) {
        this.modifiedEntityNames = modifiedEntityNames;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
