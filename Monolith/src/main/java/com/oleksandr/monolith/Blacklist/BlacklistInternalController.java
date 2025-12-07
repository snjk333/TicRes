package com.oleksandr.monolith.Blacklist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/blacklist")
@RequiredArgsConstructor
@Slf4j
public class BlacklistInternalController {

    private final BlacklistService blacklistService;

    @PostMapping("/add")
    public ResponseEntity<Void> addToBlacklist(@RequestBody AddToBlacklistRequest request) {
        log.info("Received blacklist request from RegisterMS");
        
        if (request.getToken() == null || request.getToken().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        blacklistService.addToBlacklist(request.getToken());
        return ResponseEntity.ok().build();
    }
}
