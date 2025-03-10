package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setItemId(comment.getItem().getId());
        dto.setAuthorId(comment.getAuthor().getId());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }

    public static Comment toCommentFromCreate(CommentCreateDto dto, User commentator, Item item) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setCreated(dto.getCreated());
        comment.setAuthor(commentator);
        comment.setItem(item);
        return comment;
    }
}