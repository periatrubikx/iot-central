package io.rubikx.rubikxcore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import io.rubikx.rubikxcore.service.dto.*;

import java.util.Optional;

/**
 * Service Interface for managing {@link io.rubikx.rubikxcore.domain.ShiftManagement}.
 */
public interface ShiftManagementService {
    /**
     * Save a shiftManagement.
     *
     * @param ShiftManagementDTO the entity to save.
     * @return the persisted entity.
     */
    ShiftManagementDTO save(ShiftManagementDTO ShiftManagementDTO);

    /**
     * Partially updates a shiftManagement.
     *
     * @param ShiftManagementDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ShiftManagementDTO> partialUpdate(ShiftManagementDTO ShiftManagementDTO);

    /**
     * Get all the shiftManagements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ShiftManagementDTO> findAll(Pageable pageable);

    /**
     * Get the "id" shiftManagement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ShiftManagementDTO> findOne(Long id);

    /**
     * Delete the "id" shiftManagement.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
