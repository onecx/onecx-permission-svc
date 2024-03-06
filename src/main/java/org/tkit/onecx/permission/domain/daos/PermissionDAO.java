package org.tkit.onecx.permission.domain.daos;

import static org.tkit.quarkus.jpa.utils.QueryCriteriaUtil.addSearchStringPredicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;

import org.tkit.onecx.permission.domain.criteria.PermissionSearchCriteria;
import org.tkit.onecx.permission.domain.models.*;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.TraceableEntity_;

@ApplicationScoped
public class PermissionDAO extends AbstractDAO<Permission> {

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public PageResult<Permission> findByCriteria(PermissionSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Permission.class);
            var root = cq.from(Permission.class);

            List<Predicate> predicates = new ArrayList<>();
            addSearchStringPredicate(predicates, cb, root.get(Permission_.appId), criteria.getAppId());
            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[] {}));
            }

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_PERMISSION_BY_CRITERIA, ex);
        }
    }

    public List<Permission> findByProductAndAppId(String productName, String appId) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Permission.class);
            var root = cq.from(Permission.class);
            cq.where(cb.and(
                    cb.equal(root.get(Permission_.PRODUCT_NAME), productName),
                    cb.equal(root.get(Permission_.APP_ID), appId)));
            return this.getEntityManager().createQuery(cq).getResultList();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_BY_PRODUCT_AND_APP_ID, ex);
        }
    }

    public List<Permission> findByProductNames(Collection<String> productNames) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Permission.class);
            var root = cq.from(Permission.class);
            cq.where(root.get(Permission_.PRODUCT_NAME).in(productNames));
            return this.getEntityManager().createQuery(cq).getResultList();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_BY_PRODUCT_NAMES, ex);
        }
    }

    public List<Permission> findPermissionForUser(String appId, List<String> roles) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Permission.class);
            var root = cq.from(Permission.class);

            Subquery<String> sq = cq.subquery(String.class);
            var subRoot = sq.from(Assignment.class);
            sq.select(subRoot.get(Assignment_.PERMISSION_ID));
            sq.where(
                    subRoot.get(Assignment_.role).get(Role_.name).in(roles),
                    cb.equal(subRoot.get(Assignment_.permission).get(Permission_.appId), appId));

            cq.where(root.get(TraceableEntity_.id).in(sq));

            return this.getEntityManager().createQuery(cq).getResultList();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_PERMISSION_FOR_USER, ex);
        }
    }

    public enum ErrorKeys {

        ERROR_FIND_PERMISSION_FOR_USER,
        ERROR_FIND_BY_PRODUCT_AND_APP_ID,
        ERROR_FIND_PERMISSION_BY_CRITERIA,
        ERROR_FIND_BY_PRODUCT_NAMES;
    }
}
