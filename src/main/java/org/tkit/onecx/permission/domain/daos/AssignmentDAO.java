package org.tkit.onecx.permission.domain.daos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

import org.tkit.onecx.permission.domain.criteria.AssignmentSearchCriteria;
import org.tkit.onecx.permission.domain.models.Assignment;
import org.tkit.onecx.permission.domain.models.Assignment_;
import org.tkit.onecx.permission.domain.models.Permission_;
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

            if (criteria.getAppId() != null) {
                List<String> filteredAppIds = Arrays.stream(criteria.getAppId()).filter(s -> !s.isBlank()).toList();
                if (!filteredAppIds.isEmpty()) {
                    cq.where(root.get(Assignment_.permission).get(Permission_.APP_ID).in(filteredAppIds));
                }
            }

            cq.orderBy(cb.asc(root.get(AbstractTraceableEntity_.creationDate)));

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_ASSIGNMENT_BY_CRITERIA, ex);
        }
    }

    @Transactional
    public void deleteByCriteria(String roleId, List<String> productNames, String permissionId) {
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

        dq.where(cb.and(predicates.toArray(new Predicate[0])));

        this.getEntityManager().createQuery(dq).executeUpdate();
    }

    public enum ErrorKeys {

        FIND_ENTITY_BY_ID_FAILED,
        ERROR_FIND_ASSIGNMENT_BY_CRITERIA;
    }
}
