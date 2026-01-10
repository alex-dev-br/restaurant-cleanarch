package br.com.techchallenge.restaurant_cleanarch.core.domain.pagination;

public record PagedQuery<T>(T filter, int pageNumber, int pageSize) {}