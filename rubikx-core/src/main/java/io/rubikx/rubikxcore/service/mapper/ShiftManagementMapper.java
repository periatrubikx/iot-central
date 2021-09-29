package io.rubikx.rubikxcore.service.mapper;

import io.rubikx.rubikxcore.domain.*;
import io.rubikx.rubikxcore.service.dto.ShiftManagementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ShiftManagement} and its DTO {@link ShiftManagementDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ShiftManagementMapper extends EntityMapper<ShiftManagementDTO, ShiftManagement> {}
