package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.utils;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class DateAudit implements Serializable {
    @CreatedDate
    @Column(name = "Date_Created", nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "Date_Last_Modified")
    private LocalDateTime updatedAt;

}