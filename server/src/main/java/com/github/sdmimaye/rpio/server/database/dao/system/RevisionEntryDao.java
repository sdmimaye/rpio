package com.github.sdmimaye.rpio.server.database.dao.system;

import com.github.sdmimaye.rpio.server.database.dao.base.HibernateDaoBase;
import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;
import com.github.sdmimaye.rpio.server.database.models.system.RevisionEntry;
import com.github.sdmimaye.rpio.server.http.rest.models.json.logs.JsonActivityLogEntry;
import com.github.sdmimaye.rpio.server.http.rest.queries.QueryParameters;
import com.github.sdmimaye.rpio.server.http.rest.util.SimplifiedSerialization;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.CrossTypeRevisionChangesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class RevisionEntryDao extends HibernateDaoBase<RevisionEntry> {
    private static final Logger logger = LoggerFactory.getLogger(RevisionEntryDao.class);
    @Inject
    public RevisionEntryDao(Provider<Session> session) {
        super(session);
    }

    @Override
    protected Class<RevisionEntry> getModelClass() {
        return RevisionEntry.class;
    }

    public List<JsonActivityLogEntry> getPaged(QueryParameters query) {
        List<JsonActivityLogEntry> result = new ArrayList<>();
        Criteria criteria = buildCriteria(query)
                .setFirstResult(query.getOffset())
                .setMaxResults(query.getCount())
                .setFetchSize(query.getCount());
        List<RevisionEntry> entries = (List<RevisionEntry>)criteria.list();
        AuditReader reader = AuditReaderFactory.get(session.get());
        CrossTypeRevisionChangesReader changesReader = reader.getCrossTypeRevisionChangesReader();

        boolean error = false;
        for (RevisionEntry entry : entries) {
            try {
                List<Object> changes = changesReader.findEntities(entry.getId());
                result.add(createEntry(entry, changes));
            } catch (Exception ex) {//only log first error
                if(error == false)
                    logger.warn("Error while creating database log. Did the schema change?", ex);
                error = true;
            }
        }

        return result;
    }

    private JsonActivityLogEntry createEntry(RevisionEntry entry, List<Object> changes) {
        List<Long> primaryKeys = new ArrayList<Long>();
        List<String> typeNames = new ArrayList<String>();
        for (Object obj : changes) {
            PersistedEntityBase e = (PersistedEntityBase) obj;
            primaryKeys.add(e.getId());
            typeNames.add(obj.getClass().getSimpleName() + " (" + e.getId() + ")");
        }

        JsonActivityLogEntry result = new JsonActivityLogEntry();
        result.setId(entry.getId());
        result.setPrimaryKeys(primaryKeys);
        result.setTimestamp(entry.getTimestamp());
        result.setTypeNames(typeNames);
        result.setUser(entry.getUserName());

        return result;
    }

    public JsonActivityLogEntry getByRevisionId(String id) throws IllegalAccessException, InvocationTargetException {
        RevisionEntry selected = getById(id);
        if (selected == null)
            return null;

        AuditReader reader = AuditReaderFactory.get(session.get());
        CrossTypeRevisionChangesReader changesReader = reader.getCrossTypeRevisionChangesReader();
        List<Object> entities = changesReader.findEntities(selected.getId());

        JsonActivityLogEntry result = createEntry(selected, entities);
        for (Object entry : entities) {
            PersistedEntityBase current = (PersistedEntityBase) entry;
            List<Number> revisions = reader.getRevisions(current.getClass(), current.getId());
            Number previous = getPreviousRevision(revisions, selected.getId());
            result.appendAfter(SimplifiedSerialization.simplify(current));
            PersistedEntityBase old = (PersistedEntityBase) (previous == null ? null : reader.find(entry.getClass(), current.getId(), previous));
            result.appendBefore(old == null ? "" : SimplifiedSerialization.simplify(old));
        }

        return result;
    }

    private Number getPreviousRevision(List<Number> revisions, long current) {
        for (int i = 0; i < revisions.size(); i++) {
            long rev = revisions.get(i).longValue();
            if (current != rev)
                continue;

            int index = i - 1;
            if (index >= 0)
                return revisions.get(index);
        }

        return null;
    }
}
