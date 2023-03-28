package com.ubn.hairsalon.admin.entity;

import com.ubn.hairsalon.admin.dto.TypeFormDto;
import com.ubn.hairsalon.config.entity.BaseEntity;
import com.ubn.hairsalon.member.constant.Gender;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "type")
@Data @EqualsAndHashCode(callSuper=true)
public class Type extends BaseEntity {

    @Id
    @Column(name = "type_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String typeName;

    @JoinColumn(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender typeGender;

    @Column(nullable = false)
    private int takeMinutes;

    public static Type createType(TypeFormDto typeFormDto) {
        Type type = new Type();
        type.setTypeName(typeFormDto.getTypeName());
        type.setTypeGender(typeFormDto.getTypeGender());
        type.setTakeMinutes(typeFormDto.getTakeMinutes());
        return type;
    }

    public void updateType(TypeFormDto typeFormDto) {
        this.typeName = typeFormDto.getTypeName();
        this.typeGender = typeFormDto.getTypeGender();
        this.takeMinutes = typeFormDto.getTakeMinutes();
    }
}
