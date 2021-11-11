package org.thingsboard.server.dao.sql.shift;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.shift.ShiftInfo;
import org.thingsboard.server.dao.model.sql.AssetInfoEntity;
import org.thingsboard.server.dao.model.sql.ShiftEntity;
import org.thingsboard.server.dao.model.sql.ShiftInfoEntity;

import java.util.UUID;

public interface ShiftRepository extends PagingAndSortingRepository<ShiftEntity, UUID> {
    @Query("SELECT s FROM ShiftEntity s WHERE s.tenantId = :tenantId " +
            "AND LOWER(s.searchText) LIKE LOWER(CONCAT(:textSearch, '%'))")
    Page<ShiftEntity> findByTenantId(@Param("tenantId") UUID tenantId, @Param("textSearch") String textSearch, Pageable pageable);


    @Query("SELECT s FROM ShiftEntity s WHERE s.id = :shiftId ")
    ShiftEntity findAssetInfoById(@Param("shiftId") UUID shiftId);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.ShiftInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM ShiftEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "WHERE a.tenantId = :tenantId " +
            "AND LOWER(a.searchText) LIKE LOWER(CONCAT('%', :textSearch, '%'))")
    Page<ShiftInfoEntity> findShiftInfosByTenantId(@Param("tenantId") UUID tenantId,
                                                   @Param("textSearch") String textSearch,
                                                   Pageable pageable);
}
