package io.rubikx.rubikxcore.repository;

import io.rubikx.rubikxcore.domain.ShiftManagement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ShiftManagement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShiftManagementRepository extends JpaRepository<ShiftManagement, Long> {}
