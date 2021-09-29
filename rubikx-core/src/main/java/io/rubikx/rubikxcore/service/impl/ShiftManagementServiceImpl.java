package io.rubikx.rubikxcore.service.impl;

import io.rubikx.rubikxcore.domain.ShiftManagement;
import io.rubikx.rubikxcore.repository.ShiftManagementRepository;
import io.rubikx.rubikxcore.service.ShiftManagementService;
import io.rubikx.rubikxcore.service.dto.ShiftManagementDTO;
import io.rubikx.rubikxcore.service.mapper.ShiftManagementMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ShiftManagement}.
 */
@Service
@Transactional
public class ShiftManagementServiceImpl implements ShiftManagementService {

    private final Logger log = LoggerFactory.getLogger(ShiftManagementServiceImpl.class);

    private final ShiftManagementRepository shiftManagementRepository;

    private final ShiftManagementMapper shiftManagementMapper;

    public ShiftManagementServiceImpl(ShiftManagementRepository shiftManagementRepository, ShiftManagementMapper shiftManagementMapper) {
        this.shiftManagementRepository = shiftManagementRepository;
        this.shiftManagementMapper = shiftManagementMapper;
    }

    @Override
    public ShiftManagementDTO save(ShiftManagementDTO shiftManagementDTO) {
        log.debug("Request to save ShiftManagement : {}", shiftManagementDTO);
        ShiftManagement shiftManagement = shiftManagementMapper.toEntity(shiftManagementDTO);
        shiftManagement = shiftManagementRepository.save(shiftManagement);
        return shiftManagementMapper.toDto(shiftManagement);
    }

    @Override
    public Optional<ShiftManagementDTO> partialUpdate(ShiftManagementDTO shiftManagementDTO) {
        log.debug("Request to partially update ShiftManagement : {}", shiftManagementDTO);

        return shiftManagementRepository
            .findById(shiftManagementDTO.getId())
            .map(
                existingShiftManagement -> {
                    shiftManagementMapper.partialUpdate(existingShiftManagement, shiftManagementDTO);

                    return existingShiftManagement;
                }
            )
            .map(shiftManagementRepository::save)
            .map(shiftManagementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShiftManagementDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ShiftManagements");
        return shiftManagementRepository.findAll(pageable).map(shiftManagementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShiftManagementDTO> findOne(Long id) {
        log.debug("Request to get ShiftManagement : {}", id);
        return shiftManagementRepository.findById(id).map(shiftManagementMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ShiftManagement : {}", id);
        shiftManagementRepository.deleteById(id);
    }
}
