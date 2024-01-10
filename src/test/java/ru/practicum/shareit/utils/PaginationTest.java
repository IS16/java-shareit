package ru.practicum.shareit.utils;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.error.baseExceptions.ValidationError;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaginationTest {

    @Test
    public void shouldWorkCorrectWhenSizeLessThanFrom() {
        Integer from = 5;
        Integer size = 2;

        Pagination pager = new Pagination(from, size);
        assertThat(pager.getPageStart()).isEqualTo(1);
        assertThat(pager.getPageSize()).isEqualTo(5);
        assertThat(pager.getPagesAmount()).isEqualTo(2);
    }

    @Test
    public void shouldWorkCorrectWhenFromLessThanSize() {
        Integer from = 3;
        Integer size = 7;

        Pagination pager = new Pagination(from,size);
        assertThat(pager.getPageStart()).isEqualTo(1);
        assertThat(pager.getPageSize()).isEqualTo(3);
        assertThat(pager.getPagesAmount()).isEqualTo(4);
    }

    @Test
    public void shouldWorkCorrectWhenParamsEquals() {
        Integer from = 4;
        Integer size = 4;

        Pagination pager = new Pagination(from, size);
        assertThat(pager.getPageStart()).isEqualTo(1);
        assertThat(pager.getPageSize()).isEqualTo(4);
        assertThat(pager.getPagesAmount()).isEqualTo(2);
    }

    @Test
    public void shouldWorkCorrectWhenFromIsZeroSizeNotNull() {
        Integer from = 0;
        Integer size = 4;

        Pagination pager = new Pagination(from, size);
        assertThat(pager.getPageStart()).isEqualTo(0);
        assertThat(pager.getPageSize()).isEqualTo(4);
        assertThat(pager.getPagesAmount()).isEqualTo(1);
    }

    @Test
    public void shouldWorkCorrectWhenFromIsZeroSizeNull() {
        Integer from = 0;
        Integer size = null;

        Pagination pager = new Pagination(from, size);
        assertThat(pager.getPageStart()).isEqualTo(0);
        assertThat(pager.getPageSize()).isEqualTo(1000);
        assertThat(pager.getPagesAmount()).isEqualTo(0);
    }
}
