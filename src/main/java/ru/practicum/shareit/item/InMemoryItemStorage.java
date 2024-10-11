package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {
    final Map<Integer, Item> items = new HashMap<>();
    Integer generateId = 1;

    @Override
    public Item addItem(Item item) {
        validation(item);
        item.setId(generateId++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        validationId(item.getId());
        if (item.getName() == null) {
            item.setName(items.get(item.getId()).getName());
        } else if (item.getDescription() == null) {
            item.setDescription(items.get(item.getId()).getDescription());
        } else if (item.getAvailable() == null) {
            item.setAvailable(items.get(item.getId()).getAvailable());
        } else {
            validation(item);
            items.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public Item getItemById(Integer id) {
        validationId(id);
        return items.get(id);
    }

    @Override
    public List<Item> getItemsByOwnerId(Integer ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> searchItems = new ArrayList<>();
        if (!text.isBlank()) {
            searchItems = items.values().stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(text) ||
                            item.getDescription().toLowerCase().contains(text))
                    .collect(toList());
        }
        return searchItems;
    }

    private void validation(Item item) {
        if (item.getName().isEmpty() || item.getDescription().isEmpty() || item.getAvailable() == null) {
            throw new ValidationException("Некорректные данные!");
        }
    }

    private void validationId(Integer id) {
        if (id == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }

        if (!items.containsKey(id)) {
            throw new NotFoundException("Вещь с таким id = " + id + " не была найдена!");
        }
    }
}

