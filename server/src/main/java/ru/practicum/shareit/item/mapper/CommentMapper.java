package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    default Comment mapToCommentForCreate(CommentRequest commentRequest, Item item, User author) {
        return Comment.builder()
                .text(commentRequest.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    @Mapping(source = "author.name", target = "authorName")
    CommentResponse mapToCommentResponse(Comment comment);

    List<CommentResponse> mapToCommentResponseList(List<Comment> comments);
}
