package com.agonkolgeci.nexus.api.commands.exceptions;

public class IllegalCommandExecutorException extends IllegalStateException {

    public IllegalCommandExecutorException() {
        super("Uniquement les joueurs peuvent exécuter cette commande.");
    }

}
