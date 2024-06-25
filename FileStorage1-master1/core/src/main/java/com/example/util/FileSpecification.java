package com.example.util;

import com.example.dto.FileFilterDto;
import com.example.model.FileEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FileSpecification {

    public static Specification<FileEntity> getFilesByFilter(FileFilterDto filterDto) {
        return (Root<FileEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addPredicate(predicates, filterDto.getUploadDateFrom(), () -> cb.greaterThanOrEqualTo(root.get("uploadDate"), filterDto.getUploadDateFrom()));
            addPredicate(predicates, filterDto.getUploadDateTo(), () -> cb.lessThanOrEqualTo(root.get("uploadDate"), filterDto.getUploadDateTo()));
            addPredicate(predicates, filterDto.getFileIdMax(), () -> cb.lessThanOrEqualTo(root.get("id"), filterDto.getFileIdMax()));
            addPredicate(predicates, filterDto.getSizeMin(), () -> cb.greaterThanOrEqualTo(root.get("size"), filterDto.getSizeMin()));
            addPredicate(predicates, filterDto.getSizeMax(), () -> cb.lessThanOrEqualTo(root.get("size"), filterDto.getSizeMax()));

            if (filterDto.getSortBy() != null && filterDto.getSortDirection() != null) {
                if (filterDto.getSortDirection().equalsIgnoreCase("desc")) {
                    query.orderBy(cb.desc(root.get(filterDto.getSortBy())));
                } else {
                    query.orderBy(cb.asc(root.get(filterDto.getSortBy())));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static <T> void addPredicate(List<Predicate> predicates, T value, Supplier<Predicate> supplier) {
        if (value != null) {
            predicates.add(supplier.get());
        }
    }
}


