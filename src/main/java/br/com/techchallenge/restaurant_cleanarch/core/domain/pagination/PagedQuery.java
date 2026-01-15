package br.com.techchallenge.restaurant_cleanarch.core.domain.pagination;

public record PagedQuery<T>(T filter, int pageNumber, int pageSize) {

    public PagedQuery {
        if (pageNumber < 0) throw new IllegalArgumentException("pageNumber must be >= 0");
        if (pageSize <= 0) throw new IllegalArgumentException("pageSize must be > 0");
        // filter pode ser null (ex: PagedQuery<Void> no findAll)
    }
}