package ru.practicum.ewm.repository.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public EventRepository(JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public List<Event> getEventsWithParams(Long[] userIds, String[] states, Long[] categoriesIds,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                           Integer from, Integer size) {

        from = from == null ? 0 : from;
        size = size == null ? 10 : size;

        StringBuilder sql = new StringBuilder("SELECT * FROM events AS ev WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (userIds != null && userIds.length > 0) {
            sql.append(" AND ev.initiator_id IN (");
            for (int i = 0; i < userIds.length; i++) {
                sql.append(i == 0 ? ":userIds" + i : ", :userIds" + i);
                params.put("userIds" + i, userIds[i]);
            }
            sql.append(")");
        }

        if (states != null && states.length > 0) {
            sql.append(" AND ev.state_ev IN (");
            for (int i = 0; i < states.length; i++) {
                sql.append(i == 0 ? ":states" + i : ", :states" + i);
                params.put("states" + i, states[i]);
            }
            sql.append(")");
        }

        if (categoriesIds != null && categoriesIds.length > 0) {
            sql.append(" AND ev.category_id IN (");
            for (int i = 0; i < categoriesIds.length; i++) {
                sql.append(i == 0 ? ":categoriesIds" + i : ", :categoriesIds" + i);
                params.put("categoriesIds" + i, categoriesIds[i]);
            }
            sql.append(")");
        }

        if (rangeStart != null) {
            sql.append(" AND ev.event_date >= :rangeStart");
            params.put("rangeStart", rangeStart);
        }

        if (rangeEnd != null) {
            sql.append(" AND ev.event_date <= :rangeEnd");
            params.put("rangeEnd", rangeEnd);
        }

        sql.append(" ORDER BY ev.event_date");
        sql.append(" LIMIT :size OFFSET :from");
        params.put("size", size);
        params.put("from", from);

        return namedParameterJdbcTemplate.query(sql.toString(), params, new EventRowMapper());
    }

    private static class EventRowMapper implements RowMapper<Event> {
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            User initiator = User.builder()
                    .id(rs.getLong("initiator_id"))
                    .build();

            Category category = Category.builder()
                    .id(rs.getLong("category_id"))
                    .build();

            return Event.builder()
                    .id(rs.getLong("event_id"))
                    .title(rs.getString("title"))
                    .annotation(rs.getString("annotation"))
                    .description(rs.getString("description_ev"))
                    .eventDate(rs.getTimestamp("event_date").toLocalDateTime())
                    .category(category)
                    .state(State.valueOf(rs.getString("state_ev")))
                    .createdOn(rs.getTimestamp("created_on").toLocalDateTime())
                    .publishedOn(rs.getTimestamp("published_on") != null ?
                            rs.getTimestamp("published_on").toLocalDateTime() : null)
                    .lat(rs.getDouble("lat"))
                    .lon(rs.getDouble("lon"))
                    .paid(rs.getBoolean("paid"))
                    .participantLimit(rs.getInt("participant_limit"))
                    .requestModeration(rs.getBoolean("request_moderation"))
                    .initiator(initiator)
                    .build();
        }
    }
}