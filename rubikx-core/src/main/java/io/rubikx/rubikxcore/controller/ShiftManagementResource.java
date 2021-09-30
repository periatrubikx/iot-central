package io.rubikx.rubikxcore.controller;

import io.rubikx.rubikxcore.domain.ShiftManagement;
import io.rubikx.rubikxcore.repository.ShiftManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rubikx-core")
public class ShiftManagementResource {

    @Autowired
    ShiftManagementRepository shiftManagementRepository;

    @GetMapping("/shift-management/")
    public List<ShiftManagement> getAllShifts() {
        return shiftManagementRepository.findAll();
    }
}
