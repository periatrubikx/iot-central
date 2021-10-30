package org.thingsboard.server.dao.sql.shift;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.thingsboard.server.dao.model.sql.ShiftEntity;

import java.util.UUID;

public interface ShiftRepository extends PagingAndSortingRepository<ShiftEntity, UUID> {
}
