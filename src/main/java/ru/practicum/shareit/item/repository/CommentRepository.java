package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItemId(long itemId);

    List<Comment> findByItemIdInOrderByCreatedDesc(List<Long> itemIds);

    default Map<Long, List<Comment>> getAllCommentsByItemId(List<Long> itemIds) {
        return findByItemIdInOrderByCreatedDesc(itemIds).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
    }
}
