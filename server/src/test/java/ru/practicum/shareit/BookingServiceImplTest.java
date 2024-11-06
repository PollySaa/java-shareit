package ru.practicum.shareit;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        user = User.builder()
                .name("Test User")
                .email("test@test.com")
                .build();
        user = userRepository.save(user);

        item = Item.builder()
                .owner(user)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
        item = itemRepository.save(item);
    }

    @Test
    void createBookingTest() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .bookerId(user.getId())
                .status(State.ALL.name())
                .build();

        BookingInputDto bookingResponseDto = bookingService.createBooking(user.getId(), bookingDto);

        assertNotNull(bookingResponseDto.getId());
        assertEquals(item.getId(), bookingResponseDto.getItem().getId());
        assertEquals(user.getId(), bookingResponseDto.getBooker().getId());
        assertEquals(Status.WAITING.name(), bookingResponseDto.getStatus());
    }

    @Test
    void getBookingTest() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Booking booking = Booking.builder()
                .status(Status.APPROVED)
                .item(item)
                .booker(user)
                .start(start)
                .end(end)
                .build();
        booking = bookingRepository.save(booking);

        BookingInputDto bookingResponseDto = bookingService.getBookingById(user.getId(), booking.getId());

        assertNotNull(bookingResponseDto.getId());
        assertEquals(user.getId(), bookingResponseDto.getBooker().getId());
        assertEquals(Status.APPROVED.name(), bookingResponseDto.getStatus());
        assertEquals(item.getId(), bookingResponseDto.getItem().getId());
    }

    @Test
    void updateBookingTest() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Booking booking = Booking.builder()
                .status(Status.WAITING)
                .item(item)
                .booker(user)
                .start(start)
                .end(end)
                .build();
        booking = bookingRepository.save(booking);

        BookingInputDto bookingResponseDto = bookingService.updateBooking(user.getId(), booking.getId(), true);

        assertNotNull(bookingResponseDto.getId());
        assertEquals(user.getId(), bookingResponseDto.getBooker().getId());
        assertEquals(item.getId(), bookingResponseDto.getItem().getId());
        assertEquals(Status.APPROVED.name(), bookingResponseDto.getStatus());
    }

    @Test
    void getBookingsTest() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Booking booking = Booking.builder()
                .status(Status.APPROVED)
                .item(item)
                .booker(user)
                .start(start)
                .end(end)
                .build();
        booking = bookingRepository.save(booking);

        List<BookingInputDto> bookingResponseDtoList = bookingService.getBookings(user.getId(), State.ALL.name());

        assertFalse(bookingResponseDtoList.isEmpty());
        assertEquals(1, bookingResponseDtoList.size());
        BookingInputDto bookingResponseDto = bookingResponseDtoList.getFirst();
        assertEquals(Status.APPROVED.name(), bookingResponseDto.getStatus());
        assertEquals(item.getId(), bookingResponseDto.getItem().getId());
        assertEquals(user.getId(), bookingResponseDto.getBooker().getId());
    }

    @Test
    void getBookingsOwnerTest() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Booking booking = Booking.builder()
                .status(Status.APPROVED)
                .item(item)
                .booker(user)
                .start(start)
                .end(end)
                .build();
        booking = bookingRepository.save(booking);

        List<BookingInputDto> bookingResponseDtoList = bookingService.getBookingsOwner(user.getId(), State.ALL.name());

        assertFalse(bookingResponseDtoList.isEmpty());
        assertEquals(1, bookingResponseDtoList.size());
        BookingInputDto bookingResponseDto = bookingResponseDtoList.getFirst();
        assertEquals(Status.APPROVED.name(), bookingResponseDto.getStatus());
        assertEquals(item.getId(), bookingResponseDto.getItem().getId());
        assertEquals(user.getId(), bookingResponseDto.getBooker().getId());
    }
}