package com.ubn.hairsalon.config.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(value = {AuditingEntityListener.class})
@MappedSuperclass
@Data
public class BaseDateEntity {

    @CreatedDate
    @Column(updatable = false)
    public LocalDateTime createdDate;

    @LastModifiedDate
    public LocalDateTime modifiedDate;

}
