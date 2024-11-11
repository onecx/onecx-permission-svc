package org.tkit.onecx.permission.domain.daos;

import static org.tkit.quarkus.jpa.utils.QueryCriteriaUtil.addSearchStringPredicate;

import java.util.ArrayList;
import java.util.Collection;
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
            if (!criteria.getAppIds().isEmpty()) {
                List<String> filteredAppIds = criteria.getAppIds().stream().filter(s -> !s.isBlank()).toList();
                if (!filteredAppIds.isEmpty()) {
                    predicates.add(root.get(Assignment_.permission).get(Permission_.APP_ID).in(filteredAppIds));
                }
            }
            addSearchStringPredicate(predicates, cb, root.get(Assignment_.roleId), criteria.getRoleId());
            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[] {}));
            }
            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.CREATION_DATE)));
            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_ASSIGNMENT_BY_CRITERIA, ex);
        }
    }

    @Transactional
    public void deleteByRoleId(String roleId) {
        try {
            var cb = getEntityManager().getCriteriaBuilder();
            var dq = this.deleteQuery();
            var root = dq.from(Assignment.class);
            dq.where(cb.and(cb.equal(root.get(Assignment_.ROLE_ID), roleId),
                    cb.or(cb.equal(root.get(Assignment_.MANDATORY), false), root.get(Assignment_.MANDATORY).isNull())));
            this.getEntityManager().createQuery(dq).executeUpdate();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_DELETE_BY_ROLE_ID, ex);
        }
    }

    public List<String> selectMandatoryByRoleId(String roleId) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(String.class);
            var root = cq.from(Assignment.class);
            cq.select(root.get(Assignment_.PERMISSION_ID)).where(cb.and(cb.equal(root.get(Assignment_.ROLE_ID), roleId),
                    cb.equal(root.get(Assignment_.MANDATORY), true)));
            return this.getEntityManager().createQuery(cq).getResultList();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_SELECT_MANDATORY_BY_ROLE_ID, ex);
        }
    }

    @Transactional
    public void deleteByPermissionId(String permissionId) {
        try {
            var cb = getEntityManager().getCriteriaBuilder();
            var dq = this.deleteQuery();
            var root = dq.from(Assignment.class);
            dq.where(cb.and(cb.equal(root.get(Assignment_.PERMISSION).get(TraceableEntity_.ID), permissionId),
                    cb.or(cb.equal(root.get(Assignment_.MANDATORY), false), root.get(Assignment_.MANDATORY).isNull())));
            this.getEntityManager().createQuery(dq).executeUpdate();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_DELETE_BY_PERMISSION_ID, ex);
        }
    }

    @Transactional
    public void deleteByRoleProductNameAppId(String roleId, String productName, String appId) {
        try {
            var cb = getEntityManager().getCriteriaBuilder();
            var dq = this.deleteQuery();
            var root = dq.from(Assignment.class);

            dq.where(cb.and(
                    cb.equal(root.get(Assignment_.ROLE_ID), roleId),
                    cb.equal(root.get(Assignment_.PERMISSION).get(Permission_.PRODUCT_NAME), productName),
                    cb.equal(root.get(Assignment_.PERMISSION).get(Permission_.APP_ID), appId)));
            this.getEntityManager().createQuery(dq).executeUpdate();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_DELETE_BY_ROLE_PRODUCT_NAME_APP_ID, ex);
        }
    }

    @Transactional
    public void deleteByProductNameAppIds(String productName, Collection<String> appId) {
        try {
            var cb = getEntityManager().getCriteriaBuilder();
            var dq = this.deleteQuery();
            var root = dq.from(Assignment.class);

            dq.where(cb.and(
                    cb.equal(root.get(Assignment_.PERMISSION).get(Permission_.PRODUCT_NAME), productName),
                    root.get(Assignment_.PERMISSION).get(Permission_.APP_ID).in(appId)));
            this.getEntityManager().createQuery(dq).executeUpdate();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_DELETE_BY_PRODUCT_NAME_APP_IDS, ex);
        }
    }

    @Transactional
    public void deleteByProducts(String roleId, List<String> productNames) {
        try {
            var cb = getEntityManager().getCriteriaBuilder();
            var dq = this.deleteQuery();
            var root = dq.from(Assignment.class);

            dq.where(cb.and(
                    cb.equal(root.get(Assignment_.ROLE_ID), roleId),
                    root.get(Assignment_.PERMISSION).get(Permission_.PRODUCT_NAME).in(productNames)));
            this.getEntityManager().createQuery(dq).executeUpdate();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_DELETE_BY_PRODUCTS, ex);
        }
    }

    @Transactional
    public void deleteByPermissionIds(List<String> ids) {
        try {
            var dq = this.deleteQuery();
            var root = dq.from(Assignment.class);
            dq.where(root.get(Assignment_.PERMISSION).get(TraceableEntity_.ID).in(ids));
            this.getEntityManager().createQuery(dq).executeUpdate();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_DELETE_BY_PERMISSION_IDS, ex);
        }
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

    public PageResult<Assignment> findUserAssignments(List<String> roles, int pageNumber, int pageSize) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Assignment.class);
            var root = cq.from(Assignment.class);

            cq.where(root.get(Assignment_.ROLE).get(Role_.NAME).in(roles));
            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.CREATION_DATE)));
            return createPageQueryCustom(cq, Page.of(pageNumber, pageSize)).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_USER_ASSIGNMENTS, ex);
        }
    }

    public List<Assignment> loadAssignments(List<String> assignmentIds) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Assignment.class);
            var root = cq.from(Assignment.class);
            cq.where(root.get(TraceableEntity_.ID).in(assignmentIds));

            return this.getEntityManager().createQuery(cq).setHint(HINT_LOAD_GRAPH,
                    this.getEntityManager().getEntityGraph(Assignment.ASSIGNMENT_FULL)).getResultList();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_LOAD_ASSIGNMENTS, ex);
        }
    }

    public enum ErrorKeys {

        ERROR_DELETE_BY_PRODUCT_NAME_APP_IDS,
        ERROR_DELETE_BY_PRODUCTS,
        ERROR_DELETE_BY_ROLE_PRODUCT_NAME_APP_ID,
        ERROR_DELETE_BY_PERMISSION_ID,
        ERROR_DELETE_BY_ROLE_ID,
        ERROR_FIND_PERMISSION_ACTION_FOR_PRODUCTS,
        ERROR_FIND_USER_ASSIGNMENTS,
        ERROR_LOAD_ASSIGNMENTS,
        FIND_ENTITY_BY_ID_FAILED,
        ERROR_FIND_ASSIGNMENT_BY_CRITERIA,
        ERROR_SELECT_MANDATORY_BY_ROLE_ID,
        ERROR_DELETE_BY_PERMISSION_IDS;
    }
}
