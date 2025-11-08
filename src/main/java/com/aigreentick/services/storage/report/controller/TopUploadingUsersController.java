package com.aigreentick.services.storage.report.controller;

import com.aigreentick.services.storage.report.dto.TopUploadingUserDto;
import com.aigreentick.services.storage.report.service.TopUploadingUsersService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/v1/reports/top-uploading-users")
public class TopUploadingUsersController {

    private final TopUploadingUsersService service;

    public TopUploadingUsersController(TopUploadingUsersService service) {
        this.service = service;
    }

    @GetMapping
    public List<TopUploadingUserDto> topUploaders(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(required = false) Long orgId,
            @RequestParam(required = false) Integer limit
    ) {
        return service.getTopUploadingUsers(from, to, orgId, limit);
    }
}
