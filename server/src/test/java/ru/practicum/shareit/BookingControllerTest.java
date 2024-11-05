package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    public void testCreateBooking() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .id(1L)
                .status("WAITING")
                .build();

        when(bookingService.createBooking(any(Long.class), any(BookingDto.class)))
                .thenReturn(bookingInputDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    public void testGetBookingById() throws Exception {
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .id(1L)
                .status("WAITING")
                .build();

        when(bookingService.getBookingById(any(Long.class), any(Long.class)))
                .thenReturn(bookingInputDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    public void testGetBookings() throws Exception {
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .id(1L)
                .status("WAITING")
                .build();

        when(bookingService.getBookings(any(Long.class), any(String.class)))
                .thenReturn(Collections.singletonList(bookingInputDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    public void testGetBookingsOwner() throws Exception {
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .id(1L)
                .status("WAITING")
                .build();

        when(bookingService.getBookingsOwner(any(Long.class), any(String.class)))
                .thenReturn(Collections.singletonList(bookingInputDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    public void testUpdateBooking() throws Exception {
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .id(1L)
                .status("APPROVED")
                .build();

        when(bookingService.updateBooking(any(Long.class), any(Long.class), any(Boolean.class)))
                .thenReturn(bookingInputDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
