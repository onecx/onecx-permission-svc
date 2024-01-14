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

import io.github.onecx.permission.domain.criteria.RoleSearchCriteria;
import io.github.onecx.permission.domain.models.Role;
import io.github.onecx.permission.domain.models.Role_;

@ApplicationScoped
public class RoleDAO extends AbstractDAO<Role> {

    // https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830
    @Override
    public Role findById(Object id) throws DAOException {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Role.class);
            var root = cq.from(Role.class);
            cq.where(cb.equal(root.get(TraceableEntity_.ID), id));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            throw new DAOException(ErrorKeys.FIND_ENTITY_BY_ID_FAILED, e, entityName, id);
        }
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public PageResult<Role> findByCriteria(RoleSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Role.class);
            var root = cq.from(Role.class);

            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                predicates.add(cb.like(root.get(Role_.name), QueryCriteriaUtil.wildcard(criteria.getName())));
            }
            if (criteria.getDescription() != null && !criteria.getDescription().isBlank()) {
                predicates.add(cb.like(root.get(Role_.description), QueryCriteriaUtil.wildcard(criteria.getDescription())));
            }

            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[] {}));
            }

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_ROLE_BY_CRITERIA, ex);
        }
    }

    public enum ErrorKeys {

        FIND_ENTITY_BY_ID_FAILED,
        ERROR_FIND_ROLE_BY_CRITERIA;
    }
}
