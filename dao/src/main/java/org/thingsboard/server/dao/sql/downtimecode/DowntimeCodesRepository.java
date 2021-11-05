package org.thingsboard.server.dao.sql.downtimecode;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sql.DowntimeCodeEntity;
import org.thingsboard.server.dao.model.sql.ShiftEntity;

import java.util.UUID;

public interface DowntimeCodesRepository extends PagingAndSortingRepository<DowntimeCodeEntity, UUID> {
    @Query("SELECT dc FROM DowntimeCodeEntity dc WHERE dc.tenantId = :tenantId " +
            "AND LOWER(dc.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<DowntimeCodeEntity> findByTenantId(@Param("tenantId") UUID tenantId, @Param("textSearch") String textSearch, Pageable pageable);


    @Query("SELECT dc FROM DowntimeCodeEntity dc WHERE dc.id = :downtimeCodeId ")
    DowntimeCodeEntity findDowntimeCodeInfoById(@Param("downtimeCodeId") UUID downtimeCodeId);
}
