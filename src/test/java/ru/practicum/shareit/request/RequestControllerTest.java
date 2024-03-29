package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto = new UserDto(1L, "Admin", "admin@shareit.ru");

    private ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "Описание",
            userDto,
            LocalDateTime.of(2023, 1, 1, 0, 0),
            null
    );

    private List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.create(any(), any(Long.class))).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(itemRequestDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestor.id", is(itemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(itemRequestDto.getRequestor().getName()), String.class))
                .andExpect(jsonPath("$.requestor.email", is(itemRequestDto.getRequestor().getEmail()), String.class))
                .andExpect(jsonPath(
                        "$.created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        ));
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(any(Long.class), any(Long.class))).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestor.id", is(itemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(itemRequestDto.getRequestor().getName()), String.class))
                .andExpect(jsonPath("$.requestor.email", is(itemRequestDto.getRequestor().getEmail()), String.class))
                .andExpect(jsonPath(
                        "$.created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                ));
    }

    @Test
    void getItemRequests() throws Exception {
        when(itemRequestService.getAllItemRequests(
                any(Long.class), any(Integer.class), nullable(Integer.class))
        ).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.[0].requestor.id", is(itemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.[0].requestor.name", is(itemRequestDto.getRequestor().getName()), String.class))
                .andExpect(jsonPath("$.[0].requestor.email", is(itemRequestDto.getRequestor().getEmail()), String.class))
                .andExpect(jsonPath(
                        "$.[0].created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                ));
    }

    @Test
    void getOwnItemRequests() throws Exception {
        when(itemRequestService.getOwnItemRequests(any(Long.class))).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.[0].requestor.id", is(itemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.[0].requestor.name", is(itemRequestDto.getRequestor().getName()), String.class))
                .andExpect(jsonPath("$.[0].requestor.email", is(itemRequestDto.getRequestor().getEmail()), String.class))
                .andExpect(jsonPath(
                        "$.[0].created",
                        is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                ));
    }
}
