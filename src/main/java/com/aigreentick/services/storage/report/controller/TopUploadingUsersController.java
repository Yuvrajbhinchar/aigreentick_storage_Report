package com.aigreentick.services.storage.report.controller;

import com.aigreentick.services.storage.report.dto.TopUploadingUserDto;
import com.aigreentick.services.storage.report.service.TopUploadingUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/v1/reports/top-uploading-users")
@RequiredArgsConstructor
public class TopUploadingUsersController {

    private final TopUploadingUsersService service;


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
