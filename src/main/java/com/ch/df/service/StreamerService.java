package com.ch.df.service;

import com.ch.df.domain.Streamer;
import com.ch.df.mapper.StreamerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StreamerService {
    @Autowired
    private StreamerMapper streamerMapper;

    public void addStreamer(Streamer streamer) {
        streamerMapper.insertStreamer(streamer);
    }

    public Streamer findStreamerByName(String name) {
        return streamerMapper.findByName(name);
    }
}
