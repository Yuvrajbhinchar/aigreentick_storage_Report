package com.aigreentick.services.storage.report.controller;

import com.aigreentick.services.storage.report.dto.OrganisationGrowthDto;
import com.aigreentick.services.storage.report.service.OrganisationGrowthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

public class OrganisationGrowthController {

    @RestController
    @RequestMapping("/api/v1/reports/organisation-growth")
    public class OrganisationGrowthController {

        private final OrganisationGrowthService service;

        public OrganisationGrowthController(OrganisationGrowthService service) {
            this.service = service;
        }

        @GetMapping
        public List<OrganisationGrowthDto> getGrowthLeaderboard(
                @RequestParam LocalDate from,
                @RequestParam LocalDate to,
                @RequestParam(required = false) Integer limit
        ) {
            return service.getOrganisationGrowth(from, to, limit);
        }
    }
}
