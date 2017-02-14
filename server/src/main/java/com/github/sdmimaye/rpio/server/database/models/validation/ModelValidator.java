package com.github.sdmimaye.rpio.server.database.models.validation;

import com.github.sdmimaye.rpio.server.database.dao.base.HibernateDaoBase;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;
import com.github.sdmimaye.rpio.server.database.models.validation.readable.Readable;
import com.github.sdmimaye.rpio.server.database.models.validation.selectors.ModelSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public abstract class ModelValidator<TModel extends PersistedEntityBase, TFrom extends Readable, TDao extends HibernateDaoBase<TModel>> {
    private static final Logger logger = LoggerFactory.getLogger(ModelValidator.class);

    protected final TDao dao;
    protected final HibernateUtil util;

    protected ModelValidator(TDao dao, HibernateUtil util) {
        this.dao = dao;
        this.util = util;
    }

    private TModel execute(ValidatorMethod<TModel> method){
        return util.doWork((Callable<TModel>) () ->{
            ValidationErrorListBuilder builder = new ValidationErrorListBuilder();
            try {
                return method.validate(builder);
            }catch (ValidationException ve){
                throw ve;
            }catch (Exception ex) {
                builder.with(new ValidationError(ex.getMessage()));
                logger.warn("Uncaught Exception in Model Validator: ", ex);
            }finally {
                builder.validate();
            }

            return null;
        });
    }

    protected abstract TModel doInsert(TFrom model, ValidationErrorListBuilder builder);

    public final TModel insert(final TFrom model){
        return execute(builder -> doInsert(model, builder));
    }

    protected abstract TModel doUpdate(long id, TFrom from, ValidationErrorListBuilder builder);

    public final TModel update(final long id, final TFrom model){
        return execute(builder -> doUpdate(id, model, builder));
    }

    protected abstract TModel doDelete(long id, ValidationErrorListBuilder builder);

    public final TModel delete(final long id){
        return execute(builder -> doDelete(id, builder));
    }

    public TModel insertOrUpdate(ModelSelector<TModel, TDao> selector, TFrom model){
        if(model == null)
            return null;

        return execute(builder -> {
            TModel selected = selector.select(dao);
            return selected == null ? insert(model) : update(selected.getId(), model);
        });
    }

    private interface ValidatorMethod<T>{
        T validate(ValidationErrorListBuilder builder);
    }
}
