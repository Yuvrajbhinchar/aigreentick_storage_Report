package com.aigreentick.services.storage.report.controller;

import com.aigreentick.services.storage.report.dto.UploadSummaryDto;
import com.aigreentick.services.storage.report.service.UploadSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/v1/reports/upload-summary")
@RequiredArgsConstructor
public class UploadSummaryController {

    private final UploadSummaryService service;


    @GetMapping
    public List<UploadSummaryDto> getUploadSummary(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return service.getSummary(from, to);
    }
}
