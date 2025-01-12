package com.ch.df.controller;

import com.ch.df.domain.Streamer;
import com.ch.df.service.StreamerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/streamer")
public class StreamerController {
    @Autowired
    private StreamerService streamerService;

    @GetMapping("/search")
    public ResponseEntity<Streamer> searchStreamer(@RequestParam String name) {
        return ResponseEntity.ok(streamerService.findStreamerByName(name));
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addStreamer(@RequestBody Streamer streamer) {
        streamerService.addStreamer(streamer);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Streamer> getStreamerByName(@PathVariable String name) {
        return ResponseEntity.ok(streamerService.findStreamerByName(name));
    }
}
