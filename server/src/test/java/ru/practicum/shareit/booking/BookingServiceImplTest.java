package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
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

    @Test
    void createBookingShouldThrowNotFoundExceptionWhenItemNotFound() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(999L)
                .bookerId(user.getId())
                .status(State.ALL.name())
                .build();

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(user.getId(), bookingDto);
        });
    }

    @Test
    void createBookingShouldThrowNotFoundExceptionWhenUserNotFound() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .bookerId(999L)
                .status(State.ALL.name())
                .build();

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(999L, bookingDto);
        });
    }

    @Test
    void createBookingShouldThrowValidationExceptionWhenItemNotAvailable() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        item.setAvailable(false);
        itemRepository.save(item);

        BookingDto bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .bookerId(user.getId())
                .status(State.ALL.name())
                .build();

        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(user.getId(), bookingDto);
        });
    }

    @Test
    void updateBookingShouldThrowNotFoundExceptionWhenBookingNotFound() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.updateBooking(user.getId(), 999L, true);
        });
    }

    @Test
    void updateBookingShouldThrowValidationExceptionWhenUserIsNotOwner() {
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

        User anotherUser = User.builder()
                .name("Another User")
                .email("another@test.com")
                .build();
        anotherUser = userRepository.save(anotherUser);

        User finalAnotherUser = anotherUser;
        Booking finalBooking = booking;
        assertThrows(ValidationException.class, () -> {
            bookingService.updateBooking(finalAnotherUser.getId(), finalBooking.getId(), true);
        });
    }

    @Test
    void getBookingByIdShouldThrowNotFoundExceptionWhenBookingNotFound() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(user.getId(), 999L);
        });
    }

    @Test
    void getBookingByIdShouldThrowValidationExceptionWhenUserIsNotBookerOrOwner() {
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

        User anotherUser = User.builder()
                .name("Another User")
                .email("another@test.com")
                .build();
        anotherUser = userRepository.save(anotherUser);

        User finalAnotherUser = anotherUser;
        Booking finalBooking = booking;
        assertThrows(ValidationException.class, () -> {
            bookingService.getBookingById(finalAnotherUser.getId(), finalBooking.getId());
        });
    }

    @Test
    void getBookingsShouldThrowValidationExceptionWhenUnknownState() {
        assertThrows(ValidationException.class, () -> {
            bookingService.getBookings(user.getId(), "UNKNOWN_STATE");
        });
    }

    @Test
    void getBookingsOwnerShouldThrowNotFoundExceptionWhenNoItems() {
        User anotherUser = User.builder()
                .name("Another User")
                .email("another@test.com")
                .build();
        anotherUser = userRepository.save(anotherUser);

        User finalAnotherUser = anotherUser;
        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingsOwner(finalAnotherUser.getId(), State.ALL.name());
        });
    }

    @Test
    void getBookingsOwnerShouldThrowValidationExceptionWhenUnknownState() {
        assertThrows(ValidationException.class, () -> {
            bookingService.getBookingsOwner(user.getId(), "UNKNOWN_STATE");
        });
    }

    @Test
    void updateBookingShouldSetStatusToApprovedWhenApprovedIsTrue() {
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
        assertEquals(Status.APPROVED.name(), bookingResponseDto.getStatus());
    }

    @Test
    void updateBookingShouldSetStatusToRejectedWhenApprovedIsFalse() {
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

        BookingInputDto bookingResponseDto = bookingService.updateBooking(user.getId(), booking.getId(), false);

        assertNotNull(bookingResponseDto.getId());
        assertEquals(Status.REJECTED.name(), bookingResponseDto.getStatus());
    }

    @Test
    void getBookingsShouldReturnAllBookingsWhenStateIsAll() {
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
    }

    @Test
    void getBookingsShouldReturnFutureBookingsWhenStateIsFuture() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        Booking booking = Booking.builder()
                .status(Status.APPROVED)
                .item(item)
                .booker(user)
                .start(start)
                .end(end)
                .build();
        booking = bookingRepository.save(booking);

        List<BookingInputDto> bookingResponseDtoList = bookingService.getBookings(user.getId(), State.FUTURE.name());

        assertFalse(bookingResponseDtoList.isEmpty());
        assertEquals(1, bookingResponseDtoList.size());
    }

    @Test
    void getBookingsShouldReturnWaitingBookingsWhenStateIsWaiting() {
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

        List<BookingInputDto> bookingResponseDtoList = bookingService.getBookings(user.getId(), State.WAITING.name());

        assertFalse(bookingResponseDtoList.isEmpty());
        assertEquals(1, bookingResponseDtoList.size());
    }

    @Test
    void getBookingsShouldReturnRejectedBookingsWhenStateIsRejected() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Booking booking = Booking.builder()
                .status(Status.REJECTED)
                .item(item)
                .booker(user)
                .start(start)
                .end(end)
                .build();
        booking = bookingRepository.save(booking);

        List<BookingInputDto> bookingResponseDtoList = bookingService.getBookings(user.getId(), State.REJECTED.name());

        assertFalse(bookingResponseDtoList.isEmpty());
        assertEquals(1, bookingResponseDtoList.size());
    }

    @Test
    void getBookingsShouldThrowValidationExceptionWhenStateIsUnknown() {
        assertThrows(ValidationException.class, () -> {
            bookingService.getBookings(user.getId(), "UNKNOWN_STATE");
        });
    }

    @Test
    void getBookingsOwnerShouldReturnAllBookingsWhenStateIsAll() {
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

        List<BookingInputDto> bookingResponseDtoList = bookingService.getBookingsOwner(item.getOwner().getId(), State.ALL.name());

        assertFalse(bookingResponseDtoList.isEmpty());
        assertEquals(1, bookingResponseDtoList.size());
    }

    @Test
    void getBookingsOwnerShouldReturnFutureBookingsWhenStateIsFuture() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        Booking booking = Booking.builder()
                .status(Status.APPROVED)
                .item(item)
                .booker(user)
                .start(start)
                .end(end)
                .build();
        booking = bookingRepository.save(booking);

        List<BookingInputDto> bookingResponseDtoList = bookingService.getBookingsOwner(item.getOwner().getId(), State.FUTURE.name());

        assertFalse(bookingResponseDtoList.isEmpty());
        assertEquals(1, bookingResponseDtoList.size());
    }

    @Test
    void getBookingsOwnerShouldReturnWaitingBookingsWhenStateIsWaiting() {
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

        List<BookingInputDto> bookingResponseDtoList = bookingService.getBookingsOwner(item.getOwner().getId(), State.WAITING.name());

        assertFalse(bookingResponseDtoList.isEmpty());
        assertEquals(1, bookingResponseDtoList.size());
    }

    @Test
    void getBookingsOwnerShouldReturnRejectedBookingsWhenStateIsRejected() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Booking booking = Booking.builder()
                .status(Status.REJECTED)
                .item(item)
                .booker(user)
                .start(start)
                .end(end)
                .build();
        booking = bookingRepository.save(booking);

        List<BookingInputDto> bookingResponseDtoList = bookingService.getBookingsOwner(item.getOwner().getId(), State.REJECTED.name());

        assertFalse(bookingResponseDtoList.isEmpty());
        assertEquals(1, bookingResponseDtoList.size());
    }

    @Test
    void getBookingsOwnerShouldThrowValidationExceptionWhenStateIsUnknown() {
        assertThrows(ValidationException.class, () -> {
            bookingService.getBookingsOwner(item.getOwner().getId(), "UNKNOWN_STATE");
        });
    }


}