package vn.unigap.common.utils;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import vn.unigap.common.errorcode.ErrorCode;
import vn.unigap.common.exception.ApiException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtils {

    public static void validateIdsExist(String idsString, JpaRepository<?, Long> repository, String entityName) {

        if (idsString == null || idsString.trim().isEmpty()) {
            return;
        }

        List<Long> requestedIds = Arrays.stream(idsString.split("-"))
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toList());

        List<?> existingEntities = repository.findAllById(requestedIds);

        Set<Long> existingIds = existingEntities.stream()
                .map(entity -> getEntityId(entity))
                .collect(Collectors.toSet());

        List<Long> missingIds = requestedIds.stream()
                .filter(id -> !existingIds.contains(id))
                .distinct()
                .collect(Collectors.toList());

        if (!missingIds.isEmpty()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST,
                    "The following " + entityName + "(s) do not exist: " + missingIds);
    }

    }

    public static void validateIdExists(Long id, JpaRepository<?, Long> repository, String entityName) {

        if (id == null) {
            return;
        }
        validateIdsExist(id.toString(), repository, entityName);
    }

    private static Long getEntityId(Object entity) {
        try {
            return (Long) entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException("Enity must have getId method" , e);
        }
    }

}
