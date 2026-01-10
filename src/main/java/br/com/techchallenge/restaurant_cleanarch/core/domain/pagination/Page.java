package br.com.techchallenge.restaurant_cleanarch.core.domain.pagination;

import java.util.List;

public record Page<T>(int currentPage, int pageSize, long totalElements, int totalPages, List<T> content) {}
