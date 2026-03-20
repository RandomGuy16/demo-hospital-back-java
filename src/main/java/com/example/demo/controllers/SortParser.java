package com.example.demo.controllers;

import org.springframework.data.domain.Sort;

import java.util.List;

final class SortParser {

    private SortParser() {
    }

    static Sort parse(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.unsorted();
        }

        Sort sort = Sort.unsorted();
        for (int i = 0; i < sortParams.size(); i++) {
            String sortParam = sortParams.get(i);
            String[] tokens = sortParam.split(",");
            String property = tokens[0].trim();
            Sort.Direction direction = Sort.Direction.ASC;

            if (tokens.length > 1) {
                direction = Sort.Direction.fromOptionalString(tokens[1].trim()).orElse(Sort.Direction.ASC);
            } else if (i + 1 < sortParams.size()) {
                Sort.Direction nextDirection = Sort.Direction.fromOptionalString(sortParams.get(i + 1).trim()).orElse(null);
                if (nextDirection != null) {
                    direction = nextDirection;
                    i++;
                }
            }

            sort = sort.and(Sort.by(direction, property));
        }
        return sort;
    }
}
