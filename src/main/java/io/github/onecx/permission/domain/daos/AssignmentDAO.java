package io.github.onecx.permission.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.TraceableEntity_;

import io.github.onecx.permission.domain.criteria.AssignmentSearchCriteria;
import io.github.onecx.permission.domain.models.Assignment;

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

            cq.orderBy(cb.asc(root.get(TraceableEntity_.CREATION_DATE)));

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
