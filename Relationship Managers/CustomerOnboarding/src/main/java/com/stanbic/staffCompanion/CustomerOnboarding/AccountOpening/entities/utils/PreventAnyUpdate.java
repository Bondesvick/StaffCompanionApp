package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.utils;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class PreventAnyUpdate {
    /*
    * This ensures that a given table is read-only
    *
    * Source: https://stackoverflow.com/a/32207163
    * */

    @PrePersist
    void onPrePersist(Object o) {
        throw new IllegalStateException("JPA is trying to persist an entity of type " + (o == null ? "null" : o.getClass()));
    }

    @PreUpdate
    void onPreUpdate(Object o) {
        throw new IllegalStateException("JPA is trying to update an entity of type " + (o == null ? "null" : o.getClass()));
    }

    @PreRemove
    void onPreRemove(Object o) {
        throw new IllegalStateException("JPA is trying to remove an entity of type " + (o == null ? "null" : o.getClass()));
    }
}
