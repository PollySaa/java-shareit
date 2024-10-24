package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @FutureOrPresent
    @Column(name = "start_date")
    LocalDateTime start;
    @Future
    @Column(name = "end_date")
    LocalDateTime end;
    @ManyToOne()
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    Item item;
    @ManyToOne()
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    User booker;
    @Enumerated(EnumType.STRING)
    Status status;
}
