package io.rubikx.rubikxcore.controller;

import io.rubikx.rubikxcore.domain.ShiftManagement;
import io.rubikx.rubikxcore.repository.ShiftManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rubikx-core")
public class ShiftManagementResource {

    @Autowired
    ShiftManagementRepository shiftManagementRepository;

    @GetMapping("/shift-management/all")
    public List<ShiftManagement> getAllShifts() {
        return shiftManagementRepository.findAll();
    }

    @PostMapping("/shift-management")
    public ShiftManagement createShift(@RequestBody ShiftManagement shiftManagement) {
        return shiftManagementRepository.save(shiftManagement);
    }

    @DeleteMapping("/shift-management/{shiftId}")
    public void deleteShift(@PathVariable Long shiftId) {
        shiftManagementRepository.deleteById(shiftId);
    }
}
