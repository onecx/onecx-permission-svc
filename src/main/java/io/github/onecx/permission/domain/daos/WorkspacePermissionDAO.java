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
import org.tkit.quarkus.jpa.models.TraceableEntity_;
import org.tkit.quarkus.jpa.utils.QueryCriteriaUtil;

import io.github.onecx.permission.domain.criteria.WorkspacePermissionSearchCriteria;
import io.github.onecx.permission.domain.models.WorkspacePermission;
import io.github.onecx.permission.domain.models.WorkspacePermission_;

@ApplicationScoped
public class WorkspacePermissionDAO extends AbstractDAO<WorkspacePermission> {

    // https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830
    @Override
    public WorkspacePermission findById(Object id) throws DAOException {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(WorkspacePermission.class);
            var root = cq.from(WorkspacePermission.class);
            cq.where(cb.equal(root.get(TraceableEntity_.ID), id));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            throw new DAOException(ErrorKeys.FIND_ENTITY_BY_ID_FAILED, e, entityName, id);
        }
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public PageResult<WorkspacePermission> findByCriteria(WorkspacePermissionSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(WorkspacePermission.class);
            var root = cq.from(WorkspacePermission.class);

            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getAction() != null && !criteria.getAction().isBlank()) {
                predicates
                        .add(cb.like(root.get(WorkspacePermission_.action), QueryCriteriaUtil.wildcard(criteria.getAction())));
            }
            if (criteria.getWorkspaceId() != null && !criteria.getWorkspaceId().isBlank()) {
                predicates.add(cb.like(root.get(WorkspacePermission_.workspaceId),
                        QueryCriteriaUtil.wildcard(criteria.getWorkspaceId())));
            }
            if (criteria.getResource() != null && !criteria.getResource().isBlank()) {
                predicates.add(
                        cb.like(root.get(WorkspacePermission_.resource), QueryCriteriaUtil.wildcard(criteria.getResource())));
            }

            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[] {}));
            }

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_PERMISSION_BY_CRITERIA, ex);
        }
    }

    public enum ErrorKeys {

        ERROR_FIND_PERMISSION_BY_CRITERIA,
        FIND_ENTITY_BY_ID_FAILED;
    }
}
