package io.rubikx.rubikxcore.repository;

import io.rubikx.rubikxcore.domain.DowntimeCodes;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the DowntimeCodes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DowntimeCodesRepository extends JpaRepository<DowntimeCodes, Long> {}
