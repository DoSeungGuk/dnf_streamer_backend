package com.ch.df.controller;

import com.ch.df.service.CharacterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CharacterController {

    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @GetMapping("/character/search")
    public Map<String, Object> searchCharacterInAllServers(@RequestParam String characterName) {
        return characterService.fetchCharacterFromAllServers(characterName);
    }
}
