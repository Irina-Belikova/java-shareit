package ru.practicum.shareit.request.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.QItemRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ItemRequestRepositoryImpl implements ItemRequestRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QItemRequest request = QItemRequest.itemRequest;
    private final QItem item = QItem.item;

    @Override
    public List<ItemRequestResponse> findRequestsByRequestorId(long requestorId) {
        List<Tuple> results = queryFactory
                .select(
                        request.id,
                        request.description,
                        request.created,
                        item.id,
                        item.name,
                        item.owner.id
                )
                .from(request)
                .leftJoin(item).on(item.request.id.eq(request.id))
                .where(request.requestor.id.eq(requestorId))
                .orderBy(request.created.desc(), request.id.asc())
                .fetch();

        return groupTuplesByRequest(results);
    }

    @Override
    public List<ItemRequestResponse> findRequestsByOtherRequestors(long requestorId) {
        List<Tuple> results = queryFactory
                .select(
                        request.id,
                        request.description,
                        request.created,
                        item.id,
                        item.name,
                        item.owner.id
                )
                .from(request)
                .leftJoin(item).on(item.request.id.eq(request.id))
                .where(
                        request.requestor.id.ne(requestorId)
                                .and(item.owner.id.ne(requestorId)
                                        .or(item.owner.id.isNull())
                                )
                )
                .orderBy(request.created.desc(), request.id.asc())
                .fetch();

        return groupTuplesByRequest(results);
    }

    @Override
    public ItemRequestResponse findByRequestId(long requestId) {
        List<Tuple> results = queryFactory
                .select(
                        request.id,
                        request.description,
                        request.created,
                        item.id,
                        item.name,
                        item.owner.id
                )
                .from(request)
                .leftJoin(item).on(item.request.id.eq(request.id))
                .where(request.id.eq(requestId))
                .orderBy(item.id.asc())
                .fetch();

        return groupTuplesByRequest(results).getFirst();
    }

    private List<ItemRequestResponse> groupTuplesByRequest(List<Tuple> results) {
        Map<Long, ItemRequestResponse> resultMap = new LinkedHashMap<>();

        for (Tuple tuple : results) {
            Long requestId = tuple.get(request.id);

            ItemRequestResponse response = resultMap.computeIfAbsent(requestId, id ->
                    ItemRequestResponse.builder()
                            .id(tuple.get(request.id))
                            .description(tuple.get(request.description))
                            .created(tuple.get(request.created))
                            .items(new ArrayList<>())
                            .build()
            );

            Long itemId = tuple.get(item.id);
            if (itemId != null) {
                RequestItemDto requestItemDto = RequestItemDto.builder()
                        .id(itemId)
                        .name(tuple.get(item.name))
                        .ownerId(tuple.get(item.owner.id))
                        .build();
                response.getItems().add(requestItemDto);
            }
        }
        return new ArrayList<>(resultMap.values());
    }
}
