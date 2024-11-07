package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    public void setUp() {
        user1 = User.builder().name("User1").email("user1@example.com").build();
        user2 = User.builder().name("User2").email("user2@example.com").build();
        userRepository.save(user1);
        userRepository.save(user2);

        item1 = Item.builder().name("Item1").description("Description1").available(true).owner(user1).build();
        item2 = Item.builder().name("Item2").description("Description2").available(true).owner(user2).build();
        itemRepository.save(item1);
        itemRepository.save(item2);

        booking1 = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item1)
                .booker(user2)
                .status(Status.APPROVED)
                .build();

        booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .item(item2)
                .booker(user1)
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
    }

    @Test
    public void testFindByBookerId() {
        List<Booking> bookings = bookingRepository.findByBookerId(user2.getId(), Sort.by("start"));
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.getFirst().getId());
    }

    @Test
    public void testFindByItem_OwnerId() {
        List<Booking> bookings = bookingRepository.findByItem_OwnerId(user1.getId(), Sort.by("start"));
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.getFirst().getId());
    }

    @Test
    public void testFindByBookerIdAndEndIsBefore() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBefore(user2.getId(),
                LocalDateTime.now().plusDays(3), Sort.by("start"));
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.getFirst().getId());
    }

    @Test
    public void testFindByBookerIdAndStatus() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(user2.getId(), Status.APPROVED,
                Sort.by("start"));
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.getFirst().getId());
    }

    @Test
    public void testFindByBookerIdAndStartIsAfter() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartIsAfter(user2.getId(), LocalDateTime.now(),
                Sort.by("start"));
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.getFirst().getId());
    }

    @Test
    public void testFindByItem_OwnerIdAndEndIsBefore() {
        List<Booking> bookings = bookingRepository.findByItem_OwnerIdAndEndIsBefore(user1.getId(),
                LocalDateTime.now().plusDays(3), Sort.by("start"));
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.getFirst().getId());
    }

    @Test
    public void testFindByItem_OwnerIdAndStartIsAfter() {
        List<Booking> bookings = bookingRepository.findByItem_OwnerIdAndStartIsAfter(user1.getId(), LocalDateTime.now(),
                Sort.by("start"));
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.getFirst().getId());
    }

    @Test
    public void testFindByItem_OwnerIdAndStatus() {
        List<Booking> bookings = bookingRepository.findByItem_OwnerIdAndStatus(user1.getId(), Status.APPROVED,
                Sort.by("start"));
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.getFirst().getId());
    }

    @Test
    public void testFindFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus() {
        Booking booking = bookingRepository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(item1.getId(),
                user2.getId(), LocalDateTime.now().plusDays(3), Status.APPROVED);
        assertNotNull(booking);
        assertEquals(booking1.getId(), booking.getId());
    }
}