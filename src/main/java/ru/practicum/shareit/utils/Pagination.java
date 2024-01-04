package ru.practicum.shareit.utils;

import lombok.Getter;
import ru.practicum.shareit.error.baseExceptions.ValidationError;

@Getter
public class Pagination {
    private Integer pageSize;
    private Integer pageStart;
    private Integer pagesAmount;

    public Pagination(Integer offset, Integer size) {
        if (size != null) {
            if (offset < 0 || size < 0) {
                throw new ValidationError("Значения не могут быть меньше нуля");
            }

            if (size == 0) {
                throw new ValidationError("Количество не может быть равным нулю");
            }
        }

        pageSize = offset;
        pageStart = 1;
        pagesAmount = 0;
        if (size == null) {
            if (offset == 0) {
                pageSize = 100;
                pageStart = 0;
            }
        } else {
            if (offset == 0) {
                pageSize = size;
                pageStart = 0;
            }

            pagesAmount = pageStart + 1;
            if ((offset < size) && (offset != 0)) {
                pagesAmount = size / offset + pageStart;
                if (size % offset != 0) {
                    pagesAmount++;
                }
            }
        }
    }
}
