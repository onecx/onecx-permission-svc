package io.github.onecx.permission.domain.daos;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.AbstractTraceableEntity_;
import org.tkit.quarkus.jpa.models.TraceableEntity_;

import io.github.onecx.permission.domain.criteria.WorkspaceAssignmentSearchCriteria;
import io.github.onecx.permission.domain.models.*;

@ApplicationScoped
public class WorkspaceAssignmentDAO extends AbstractDAO<WorkspaceAssignment> {

    // https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830
    @Override
    public WorkspaceAssignment findById(Object id) throws DAOException {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(WorkspaceAssignment.class);
            var root = cq.from(WorkspaceAssignment.class);
            cq.where(cb.equal(root.get(TraceableEntity_.ID), id));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            throw new DAOException(ErrorKeys.FIND_ENTITY_BY_ID_FAILED, e, entityName, id);
        }
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public PageResult<WorkspaceAssignment> findByCriteria(WorkspaceAssignmentSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(WorkspaceAssignment.class);
            var root = cq.from(WorkspaceAssignment.class);

            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getWorkspaceId() != null && !criteria.getWorkspaceId().isBlank()) {
                predicates.add(cb.equal(root.get(WorkspaceAssignment_.permission).get(WorkspacePermission_.WORKSPACE_ID),
                        criteria.getWorkspaceId()));
            }

            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[] {}));
            }

            cq.orderBy(cb.asc(root.get(AbstractTraceableEntity_.creationDate)));

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_ASSIGNMENT_BY_CRITERIA, ex);
        }
    }

    public enum ErrorKeys {

        ERROR_FIND_ASSIGNMENT_BY_CRITERIA,

        FIND_ENTITY_BY_ID_FAILED;
    }
}
