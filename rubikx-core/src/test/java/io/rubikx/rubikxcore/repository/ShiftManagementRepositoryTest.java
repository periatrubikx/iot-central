package io.rubikx.rubikxcore.repository;

import io.rubikx.rubikxcore.domain.ShiftManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShiftManagementRepositoryTest extends BaseTest {

    @Autowired
    ShiftManagementRepository shiftManagementRepository;

    @Test
    public void testGetAllShifts() {
        List<ShiftManagement> shifts = shiftManagementRepository.findAll();
        assertTrue(shifts.isEmpty());
    }
}