package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testFindItemsByOwnerId() {
        User owner1 = User.builder().name("Owner1").email("owner1@example.com").build();
        User owner2 = User.builder().name("Owner2").email("owner2@example.com").build();

        entityManager.persist(owner1);
        entityManager.persist(owner2);

        Item item1 = Item.builder().name("Item1").description("Description1").available(true).owner(owner1).build();
        Item item2 = Item.builder().name("Item2").description("Description2").available(true).owner(owner1).build();
        Item item3 = Item.builder().name("Item3").description("Description3").available(true).owner(owner2).build();

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        entityManager.flush();

        List<Item> foundItems = itemRepository.findItemsByOwnerId(owner1.getId());

        assertThat(foundItems).hasSize(2);
        assertThat(foundItems).extracting(Item::getName).containsExactlyInAnyOrder("Item1", "Item2");
    }

    @Test
    public void testSearch() {
        User owner = User.builder().name("Owner").email("owner@example.com").build();
        entityManager.persist(owner);

        Item item1 = Item.builder().name("Book").description("A great book").available(true).owner(owner).build();
        Item item2 = Item.builder().name("Notebook").description("A notebook with paper").available(true).owner(owner).build();
        Item item3 = Item.builder().name("Pen").description("A pen for writing").available(true).owner(owner).build();

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        entityManager.flush();

        List<Item> foundItems = itemRepository.search("book");

        assertThat(foundItems).hasSize(2);
        assertThat(foundItems).extracting(Item::getName).containsExactlyInAnyOrder("Book", "Notebook");
    }

    @Test
    public void testFindByRequestId() {
        User owner = User.builder().name("Owner").email("owner@example.com").build();
        entityManager.persist(owner);

        Item item1 = Item.builder().name("Item1").description("Description1").available(true).owner(owner).requestId(101L).build();
        Item item2 = Item.builder().name("Item2").description("Description2").available(true).owner(owner).requestId(102L).build();
        Item item3 = Item.builder().name("Item3").description("Description3").available(true).owner(owner).requestId(101L).build();

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        entityManager.flush();

        List<Item> foundItems = itemRepository.findByRequestId(101L);

        assertThat(foundItems).hasSize(2);
        assertThat(foundItems).extracting(Item::getName).containsExactlyInAnyOrder("Item1", "Item3");
    }

    @Test
    public void testFindItemsByRequestIds() {
        User owner = User.builder().name("Owner").email("owner@example.com").build();
        entityManager.persist(owner);

        Item item1 = Item.builder().name("Item1").description("Description1").available(true).owner(owner).requestId(101L).build();
        Item item2 = Item.builder().name("Item2").description("Description2").available(true).owner(owner).requestId(102L).build();
        Item item3 = Item.builder().name("Item3").description("Description3").available(true).owner(owner).requestId(101L).build();
        Item item4 = Item.builder().name("Item4").description("Description4").available(true).owner(owner).requestId(103L).build();

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        entityManager.persist(item4);
        entityManager.flush();

        List<Item> foundItems = itemRepository.findItemsByRequestIds(List.of(101L, 103L));

        assertThat(foundItems).hasSize(3);
        assertThat(foundItems).extracting(Item::getName).containsExactlyInAnyOrder("Item1", "Item3", "Item4");
    }
}