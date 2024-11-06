package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE lower(i.name) like lower(concat('%', :text, '%')) " +
            "or lower(i.description) like lower(concat('%', :text, '%'))")
    List<Item> search(String text);

    List<Item> findByRequestId(Long requestId);

    @Query("SELECT i FROM Item i WHERE i.requestId IN :requestIds")
    List<Item> findItemsByRequestIds(@Param("requestIds") List<Long> requestIds);
}