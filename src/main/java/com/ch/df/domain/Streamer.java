package com.ch.df.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Streamer {
    private int id;
    private String name;
    private String adventureName;
    private String serverId;
}