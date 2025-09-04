package com.dompet.api.shared.web;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * Stable DTO for paginated responses. Mirrors common frontend contract.
 */
public record PageResponse<T>(
    List<T> content,
    int number,
    int size,
    long totalElements,
    int totalPages,
    boolean last,
    boolean first
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast(),
            page.isFirst()
        );
    }
}
