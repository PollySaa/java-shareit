package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Override
    public BookingInputDto createBooking(Long bookerId, BookingDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
        new NotFoundException("Предмет с id = " + bookingDto.getItemId() + " не найден!"));

        User user = userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + bookerId + " не был найден!"));

        if (!item.getAvailable()) {
            throw new ValidationException("Товар недоступен");
        }

        bookingDto.setStatus(Status.WAITING.name());
        Booking bookingApproved = BookingMapper.toBooking(bookingDto, item, user);
        Booking newBooking = bookingRepository.save(bookingApproved);
        return BookingMapper.toBookingInputDto(newBooking, UserMapper.toUserDto(user), ItemMapper.toItemDto(item));
    }

    @Override
    public BookingInputDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование с таким id = " + bookingId + " не найдено!"));

        Long itemId = booking.getItem().getOwner().getId();
        if (!itemId.equals(userId)) {
            throw new ValidationException("Не верный id владельца вещи!");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingInputDto(booking, UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem()));
    }

    @Override
    public BookingInputDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование с таким id = " + bookingId + " не найдено!"));

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (bookerId.equals(userId) || ownerId.equals(userId)) {
            return BookingMapper.toBookingInputDto(booking, UserMapper.toUserDto(booking.getBooker()),
                    ItemMapper.toItemDto(booking.getItem()));
        } else {
            throw new ValidationException("Некорректные данные!");
        }
    }

    @Override
    public List<BookingInputDto> getBookings(Long userId, String state) {
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Неизвестное состояние: " + state);
        }

        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");

        bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByBookerId(userId, sortByStartDesc);
            case CURRENT ->
                    bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                            LocalDateTime.now(), sortByStartDesc);
            case PAST ->
                    bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
            case FUTURE -> bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(),
                    sortByStartDesc);
            case WAITING -> bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, sortByStartDesc);
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, sortByStartDesc);
        };

        return BookingMapper.mapToBookingInputDtoList(bookings);
    }

    @Override
    public List<BookingInputDto> getBookingsOwner(Long userId, String state) {
        List<Item> items = itemRepository.findItemsByOwnerId(userId);
        if (items == null || items.isEmpty()) {
            throw new NotFoundException("Вещей нет!");
        }

        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Неизвестное состояние: " + state);
        }

        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");

        bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByItem_OwnerId(userId, sortByStartDesc);
            case CURRENT -> bookingRepository.findByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(userId,
                    LocalDateTime.now(), LocalDateTime.now(), sortByStartDesc);
            case PAST -> bookingRepository.findByItem_OwnerIdAndEndIsBefore(userId, LocalDateTime.now(),
                    sortByStartDesc);
            case FUTURE -> bookingRepository.findByItem_OwnerIdAndStartIsAfter(userId, LocalDateTime.now(),
                    sortByStartDesc);
            case WAITING -> bookingRepository.findByItem_OwnerIdAndStatus(userId, Status.WAITING, sortByStartDesc);
            case REJECTED -> bookingRepository.findByItem_OwnerIdAndStatus(userId, Status.REJECTED, sortByStartDesc);
        };
        return BookingMapper.mapToBookingInputDtoList(bookings);
    }
}