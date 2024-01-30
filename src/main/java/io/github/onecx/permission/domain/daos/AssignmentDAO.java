package io.github.onecx.permission.domain.daos;

import static org.tkit.quarkus.jpa.utils.QueryCriteriaUtil.addSearchStringPredicate;

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

import io.github.onecx.permission.domain.criteria.AssignmentSearchCriteria;
import io.github.onecx.permission.domain.models.Assignment;
import io.github.onecx.permission.domain.models.Assignment_;
import io.github.onecx.permission.domain.models.Permission_;

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

            addSearchStringPredicate(predicates, cb, root.get(Assignment_.permission).get(Permission_.APP_ID),
                    criteria.getAppId());

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

        FIND_ENTITY_BY_ID_FAILED,
        ERROR_FIND_ASSIGNMENT_BY_CRITERIA;
    }
}
