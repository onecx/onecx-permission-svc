package io.github.onecx.permission.domain.daos;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.utils.QueryCriteriaUtil;

import io.github.onecx.permission.domain.criteria.PermissionSearchCriteria;
import io.github.onecx.permission.domain.models.Permission;
import io.github.onecx.permission.domain.models.Permission_;

@ApplicationScoped
public class PermissionDAO extends AbstractDAO<Permission> {

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public PageResult<Permission> findByCriteria(PermissionSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Permission.class);
            var root = cq.from(Permission.class);

            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                predicates.add(cb.like(root.get(Permission_.name), QueryCriteriaUtil.wildcard(criteria.getName())));
            }
            if (criteria.getAction() != null && !criteria.getAction().isBlank()) {
                predicates.add(cb.like(root.get(Permission_.action), QueryCriteriaUtil.wildcard(criteria.getAction())));
            }
            if (criteria.getAppId() != null && !criteria.getAppId().isBlank()) {
                predicates.add(cb.like(root.get(Permission_.appId), QueryCriteriaUtil.wildcard(criteria.getAppId())));
            }
            if (criteria.getObject() != null && !criteria.getObject().isBlank()) {
                predicates.add(cb.like(root.get(Permission_.object), QueryCriteriaUtil.wildcard(criteria.getObject())));
            }

            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[] {}));
            }

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_PERMISSION_BY_CRITERIA, ex);
        }
    }

    public List<Permission> loadByAppId(String appId) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Permission.class);
            var root = cq.from(Permission.class);
            cq.where(cb.equal(root.get(Permission_.APP_ID), appId));
            return this.getEntityManager().createQuery(cq).getResultList();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_LOAD_BY_APP_ID, ex);
        }
    }

    public enum ErrorKeys {

        ERROR_LOAD_BY_APP_ID,
        ERROR_FIND_PERMISSION_BY_CRITERIA;
    }
}
