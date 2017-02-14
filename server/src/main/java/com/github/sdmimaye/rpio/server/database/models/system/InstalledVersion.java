package com.github.sdmimaye.rpio.server.database.models.system;

import com.github.sdmimaye.rpio.common.utils.version.Version;
import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Audited
public class InstalledVersion extends PersistedEntityBase {
    private Integer major;

    private Integer branch;

    private Integer hotfix;

    private Integer revision;

    @Temporal(TemporalType.TIMESTAMP)
    private Date installationDate;

    public InstalledVersion() {
    }

    public InstalledVersion(Integer major, Integer branch, Integer hotfix, Integer revision, Date installationDate) {
        this.major = major;
        this.branch = branch;
        this.hotfix = hotfix;
        this.revision = revision;
        this.installationDate = installationDate;
    }

    public static InstalledVersion byVersion(Version version, Date installed) {
        return new InstalledVersion(version.getMajor(), version.getMinor(), version.getBuild(), version.getRevision(), installed);
    }

    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public Date getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(Date installationDate) {
        this.installationDate = installationDate;
    }

    public Integer getBranch() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch = branch;
    }

    public Integer getHotfix() {
        return hotfix;
    }

    public void setHotfix(Integer hotfix) {
        this.hotfix = hotfix;
    }
}
