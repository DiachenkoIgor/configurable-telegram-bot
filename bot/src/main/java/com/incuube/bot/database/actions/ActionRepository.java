package com.incuube.bot.database.actions;

import com.incuube.bot.model.common.Action;
import com.incuube.bot.model.common.Button;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//TODO(igordiachenko): Create logic for action table

@Repository
public interface ActionRepository {

    Optional<Action> getActionById(String id);

    void saveAction(Action action);

}
