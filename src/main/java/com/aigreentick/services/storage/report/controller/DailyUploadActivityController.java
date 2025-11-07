package com.aigreentick.services.storage.report.controller;

import com.aigreentick.services.storage.report.dto.DailyUploadActivityDto;
import com.aigreentick.services.storage.report.service.DailyUploadActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DailyUploadActivityController {

    private final DailyUploadActivityService service;

    @GetMapping
    public List<DailyUploadActivityDto> report(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(required = false) Long orgId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String mediaType
    ) {
        return service.getActivity(from, to, orgId, userId, mediaType);
    }
}
