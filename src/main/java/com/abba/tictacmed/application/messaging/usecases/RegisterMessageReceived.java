package com.abba.tictacmed.application.messaging.usecases;

import com.abba.tictacmed.application.messaging.command.RegisterMessageReceivedCommand;

public interface RegisterMessageReceived {
    void execute(RegisterMessageReceivedCommand cmd);
}
