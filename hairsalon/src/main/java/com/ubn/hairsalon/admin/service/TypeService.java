package com.ubn.hairsalon.admin.service;

import com.ubn.hairsalon.admin.dto.TypeFormDto;
import com.ubn.hairsalon.admin.entity.Type;
import com.ubn.hairsalon.admin.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TypeService {

    private final TypeRepository typeRepository;

    public Type saveType(Type type) {
        return typeRepository.save(type);
    }

    public List<Type> listType() {
        return typeRepository.findAllByOrderByIdDesc();
    }

    public TypeFormDto getTypeDetail(Long typeId) {
        Type type = typeRepository.findById(typeId).orElseThrow(EntityNotFoundException::new);
        TypeFormDto typeFormDto = TypeFormDto.of(type);
        return typeFormDto;
    }

    public Long updateType(TypeFormDto typeFormDto) throws Exception {
        Type type = typeRepository.findById(typeFormDto.getTypeId()).orElseThrow(EntityNotFoundException::new);
        type.updateType(typeFormDto);
        return type.getId();
    }

    public void deleteType(Long typeId) {
        Type type = typeRepository.findById(typeId).orElseThrow(EntityNotFoundException::new);
        typeRepository.delete(type);
    }
}
