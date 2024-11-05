package ru.practicum.shareit;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.Status;
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
@ActiveProfiles("test")
@Transactional
public class BookingServiceImplTest {
    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user = userRepository.save(user);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        item = itemRepository.save(item);
    }

    @Test
    public void testCreateBooking() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingInputDto createdBooking = bookingService.createBooking(user.getId(), bookingDto);

        assertNotNull(createdBooking);
        assertEquals(Status.WAITING.name(), createdBooking.getStatus());
    }

    @Test
    public void testGetBookingById() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingInputDto createdBooking = bookingService.createBooking(user.getId(), bookingDto);

        BookingInputDto fetchedBooking = bookingService.getBookingById(user.getId(), createdBooking.getId());

        assertNotNull(fetchedBooking);
        assertEquals(createdBooking.getId(), fetchedBooking.getId());
    }

    @Test
    public void testGetBookings() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        bookingService.createBooking(user.getId(), bookingDto);

        List<BookingInputDto> bookings = bookingService.getBookings(user.getId(), "ALL");

        assertFalse(bookings.isEmpty());
    }

    @Test
    public void testGetBookingsOwner() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        bookingService.createBooking(user.getId(), bookingDto);

        List<BookingInputDto> bookings = bookingService.getBookingsOwner(user.getId(), "ALL");

        assertFalse(bookings.isEmpty());
    }

    @Test
    public void testUpdateBooking() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingInputDto createdBooking = bookingService.createBooking(user.getId(), bookingDto);

        BookingInputDto updatedBooking = bookingService.updateBooking(user.getId(), createdBooking.getId(), true);

        assertNotNull(updatedBooking);
        assertEquals(Status.APPROVED.name(), updatedBooking.getStatus());
    }
}
