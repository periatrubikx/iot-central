package io.rubikx.rubikxcore;

import io.rubikx.rubikxcore.domain.ShiftManagement;
import io.rubikx.rubikxcore.repository.ShiftManagementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZonedDateTime;
import java.util.List;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ShiftManagementServiceTest extends RubikxCoreApplicationTests {

    @Autowired
    ShiftManagementRepository shiftManagementRepository;

    @Test
    public void findAllShifts() throws Exception {
        List<ShiftManagement> shifts = shiftManagementRepository.findAll();
        assertTrue(shifts.isEmpty());
    }

    @Test
    public void saveShift() throws Exception {
        List<ShiftManagement> shifts = shiftManagementRepository.findAll();
        assertTrue(shifts.isEmpty());
        ShiftManagement shiftManagement = new ShiftManagement();
        shiftManagement.setShiftName("A");
        shiftManagement.setAreaName("Test Area");
        shiftManagement.setStartTime(ZonedDateTime.now());
        shiftManagement.setEndTime(ZonedDateTime.now().plusHours(8));
        ShiftManagement savedEntity = shiftManagementRepository.save(shiftManagement);
        assertNotNull(savedEntity.getId());
    }
}
