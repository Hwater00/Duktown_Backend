package com.duktown.domain.roommate.controller;

import com.duktown.domain.cleaningUnit.entity.CleaningUnitInitDB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("unit")
public class RoommateController {


    private final CleaningUnitInitDB unitService;

    @GetMapping("/{userId}")
    public void unitAllocation(@PathVariable Long userId){
        unitService.allocationCleaning(userId);
    }

}
