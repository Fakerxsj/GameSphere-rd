package com.xsj.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class PageResponse<T> {

    private List<T> records;

    private Long total;

    private Long pages;

    private Long current;

    private Long size;

    public static <T> PageResponse<T> of(List<T> records, Long total, Long current, Long size) {
        PageResponse<T> response = new PageResponse<>();
        response.setRecords(records);
        response.setTotal(total);
        response.setPages((total + size - 1) / size);
        response.setCurrent(current);
        response.setSize(size);
        return response;
    }
}
