package br.com.techchallenge.restaurant_cleanarch.core.domain.pagination;

import java.util.List;
import java.util.function.Function;

public record Page<T>(int pageNumber, int pageSize, long totalElements, List<T> content) {

    public int totalPages() {
        if (pageSize <= 0) return 0;
        return (int) Math.ceil((double) totalElements / (double) pageSize);
    }

    public <R> Page<R> mapItems(Function<T, R> mapper) {
        return new Page<>(
                pageNumber,
                pageSize,
                totalElements,
                content.stream().map(mapper).toList()
        );
    }
}
