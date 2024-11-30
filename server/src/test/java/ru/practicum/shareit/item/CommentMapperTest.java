package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    private User author;
    private Item item;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .id(1L)
                .name("Test Author")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .build();
    }

    @Test
    void testToComment() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Test comment")
                .created(LocalDateTime.now())
                .build();

        Comment comment = CommentMapper.toComment(commentDto, author, item);

        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertEquals(commentDto.getCreated(), comment.getCreatedDate());
    }

    @Test
    void testToCommentDto() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .item(item)
                .author(author)
                .createdDate(LocalDateTime.now())
                .build();

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getItem().getId(), commentDto.getItemId());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreatedDate(), commentDto.getCreated());
    }

    @Test
    void testToCommentDtoList() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Test comment 1")
                .item(item)
                .author(author)
                .createdDate(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .text("Test comment 2")
                .item(item)
                .author(author)
                .createdDate(LocalDateTime.now())
                .build();

        List<Comment> comments = List.of(comment1, comment2);

        List<CommentDto> commentDtos = CommentMapper.toCommentDtoList(comments);

        assertEquals(comments.size(), commentDtos.size());
        for (int i = 0; i < comments.size(); i++) {
            assertEquals(comments.get(i).getId(), commentDtos.get(i).getId());
            assertEquals(comments.get(i).getText(), commentDtos.get(i).getText());
            assertEquals(comments.get(i).getItem().getId(), commentDtos.get(i).getItemId());
            assertEquals(comments.get(i).getAuthor().getName(), commentDtos.get(i).getAuthorName());
            assertEquals(comments.get(i).getCreatedDate(), commentDtos.get(i).getCreated());
        }
    }
}
