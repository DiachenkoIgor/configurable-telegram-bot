package com.incuube.bot.controllers;

import com.incuube.bot.database.actions.ActionRepository;
import com.incuube.bot.model.common.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/configuration")
public class ConfigController {

    private ActionRepository actionRepository;

    @Autowired
    public ConfigController(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    @PostMapping(value = "/actions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleActionsUpload(@RequestBody List<Action> actions) {
        actions.forEach(actionRepository::saveAction);
        return ResponseEntity.ok().build();
    }
}
