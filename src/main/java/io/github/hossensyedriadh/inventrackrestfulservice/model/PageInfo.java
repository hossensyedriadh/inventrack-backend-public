package io.github.hossensyedriadh.inventrackrestfulservice.model;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.io.Serial;
import java.io.Serializable;

@Getter
public final class PageInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 3313964966461789803L;

    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final int number;

    public PageInfo(Page<?> page) {
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.number = page.getNumber();
    }
}
