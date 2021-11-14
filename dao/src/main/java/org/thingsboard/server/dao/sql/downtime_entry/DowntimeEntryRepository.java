package org.thingsboard.server.dao.sql.downtime_entry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sql.DowntimeEntryEntity;
import org.thingsboard.server.dao.model.sql.DowntimeEntryInfoEntity;

import java.util.UUID;

public interface DowntimeEntryRespository extends PagingAndSortingRepository<DowntimeEntryEntity, UUID>{

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DowntimeEntryInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM DowntimeEntryEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "WHERE a.tenantId = :tenantId " +
            "AND LOWER(a.searchText) LIKE LOWER(CONCAT('%', :textSearch, '%'))")
    Page<DowntimeEntryInfoEntity> findDowntimeEntryInfosByTenantId(@Param("tenantId") UUID tenantId,
                                                                   @Param("textSearch") String textSearch,
                                                                   Pageable pageable);
}
