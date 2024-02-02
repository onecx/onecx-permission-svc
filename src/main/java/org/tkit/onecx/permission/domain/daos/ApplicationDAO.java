package org.tkit.onecx.permission.domain.daos;

import static org.tkit.quarkus.jpa.utils.QueryCriteriaUtil.addSearchStringPredicate;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

import org.tkit.onecx.permission.domain.criteria.ApplicationSearchCriteria;
import org.tkit.onecx.permission.domain.models.*;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;

@ApplicationScoped
public class ApplicationDAO extends AbstractDAO<Application> {

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public PageResult<Application> findByCriteria(ApplicationSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Application.class);
            var root = cq.from(Application.class);

            List<Predicate> predicates = new ArrayList<>();
            addSearchStringPredicate(predicates, cb, root.get(Application_.appId), criteria.getAppId());
            addSearchStringPredicate(predicates, cb, root.get(Application_.name), criteria.getName());

            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[] {}));
            }

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_APPLICATIONS_BY_CRITERIA, ex);
        }
    }

    public Application loadByAppId(String appId) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Application.class);
            var root = cq.from(Application.class);
            cq.where(cb.equal(root.get(Application_.APP_ID), appId));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException ne) {
            return null;
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_LOAD_BY_APP_ID, ex);
        }
    }

    public enum ErrorKeys {

        ERROR_FIND_APPLICATIONS_BY_CRITERIA,
        ERROR_LOAD_BY_APP_ID;
    }
}
