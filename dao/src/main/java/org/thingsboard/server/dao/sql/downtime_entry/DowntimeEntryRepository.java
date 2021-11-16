package org.thingsboard.server.dao.sql.downtime_entry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.dao.model.sql.DowntimeCodeEntity;
import org.thingsboard.server.dao.model.sql.DowntimeEntryEntity;
import org.thingsboard.server.dao.model.sql.DowntimeEntryInfoEntity;

import java.util.UUID;

public interface DowntimeEntryRepository extends PagingAndSortingRepository<DowntimeEntryEntity, UUID>{

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DowntimeEntryInfoEntity(dt, c.title, c.additionalInfo, a.name, d.name) " +
            "FROM DowntimeEntryEntity dt " +
            "LEFT JOIN CustomerEntity c on c.id = dt.customerId " +
            "LEFT JOIN AssetEntity a on a.id = dt.assetId " +
            "LEFT JOIN DeviceEntity d on d.id = dt.deviceId " +
            "WHERE dt.tenantId = :tenantId " +
            "AND LOWER(dt.searchText) LIKE LOWER(CONCAT('%', :textSearch, '%'))")
    Page<DowntimeEntryInfoEntity> findDowntimeEntryInfosByTenantId(@Param("tenantId") UUID tenantId,
                                                                   @Param("textSearch") String textSearch,
                                                                   Pageable pageable);
    @Query("SELECT dc FROM DowntimeEntryEntity dc WHERE dc.id = :downtimeEntryId ")
    DowntimeEntryEntity findDowntimeEntryById(@Param("downtimeEntryId") UUID downtimeEntryId);
}
