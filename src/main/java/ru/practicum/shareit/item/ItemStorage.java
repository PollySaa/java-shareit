package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(Integer id);

    List<Item> getItemsByOwnerId(Integer ownerId);

    List<Item> searchItem(String text);
}
