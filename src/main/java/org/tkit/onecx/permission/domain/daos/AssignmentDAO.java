package org.tkit.onecx.permission.domain.daos;

import static org.tkit.quarkus.jpa.utils.QueryCriteriaUtil.addSearchStringPredicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

import org.tkit.onecx.permission.domain.criteria.AssignmentSearchCriteria;
import org.tkit.onecx.permission.domain.models.*;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.AbstractTraceableEntity_;
import org.tkit.quarkus.jpa.models.TraceableEntity_;

@ApplicationScoped
public class AssignmentDAO extends AbstractDAO<Assignment> {

    // https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830
    @Override
    public Assignment findById(Object id) throws DAOException {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Assignment.class);
            var root = cq.from(Assignment.class);
            cq.where(cb.equal(root.get(TraceableEntity_.ID), id));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            throw new DAOException(ErrorKeys.FIND_ENTITY_BY_ID_FAILED, e, entityName, id);
        }
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public PageResult<Assignment> findByCriteria(AssignmentSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Assignment.class);
            var root = cq.from(Assignment.class);

            List<Predicate> predicates = new ArrayList<>();
            if (criteria.getAppIds() != null) {
                List<String> filteredAppIds = Arrays.stream(criteria.getAppIds()).filter(s -> !s.isBlank()).toList();
                if (!filteredAppIds.isEmpty()) {
                    predicates.add(root.get(Assignment_.permission).get(Permission_.APP_ID).in(filteredAppIds));
                }
            }
            addSearchStringPredicate(predicates, cb, root.get(Assignment_.roleId), criteria.getRoleId());
            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[] {}));
            }
            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.modificationDate)));

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_ASSIGNMENT_BY_CRITERIA, ex);
        }
    }

    @Transactional
    public void deleteByCriteria(String roleId, List<String> productNames, String permissionId, String appId) {
        var cb = getEntityManager().getCriteriaBuilder();
        var dq = this.deleteQuery();
        var root = dq.from(Assignment.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get(Assignment_.ROLE).get(TraceableEntity_.ID), roleId));

        if (productNames != null) {
            predicates.add(root.get(Assignment_.PERMISSION).get(Permission_.PRODUCT_NAME).in(productNames));
        }

        if (permissionId != null) {
            predicates.add(cb.equal(root.get(Assignment_.PERMISSION).get(TraceableEntity_.ID), permissionId));
        }
        if (appId != null) {
            predicates.add(cb.equal(root.get(Assignment_.PERMISSION).get(Permission_.APP_ID), appId));
        }

        dq.where(cb.and(predicates.toArray(new Predicate[0])));

        this.getEntityManager().createQuery(dq).executeUpdate();
    }

    public List<PermissionAction> findPermissionActionForProducts(Set<String> productNames) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(PermissionAction.class);
            var root = cq.from(Assignment.class);

            cq.select(cb.construct(PermissionAction.class,
                    root.get(Assignment_.ROLE).get(Role_.NAME),
                    root.get(Assignment_.permission).get(Permission_.PRODUCT_NAME),
                    root.get(Assignment_.permission).get(Permission_.APP_ID),
                    root.get(Assignment_.permission).get(Permission_.RESOURCE),
                    root.get(Assignment_.permission).get(Permission_.ACTION)));
            cq.where(root.get(Assignment_.permission).get(Permission_.PRODUCT_NAME).in(productNames));

            return this.getEntityManager().createQuery(cq).getResultList();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_PERMISSION_ACTION_FOR_PRODUCTS, ex);
        }
    }

    public enum ErrorKeys {

        ERROR_FIND_PERMISSION_ACTION_FOR_PRODUCTS,

        FIND_ENTITY_BY_ID_FAILED,
        ERROR_FIND_ASSIGNMENT_BY_CRITERIA;
    }
}
