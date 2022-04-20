package io.github.hossensyedriadh.InvenTrackRESTfulService.pojo;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public final class PageInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1752666996396379364L;

    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final int number;

    public PageInfo(org.springframework.data.domain.Page<?> page) {
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.number = page.getNumber();
    }
}
