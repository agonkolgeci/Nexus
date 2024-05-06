package com.agonkolgeci.nexus.common.commands.exceptions;

public class IllegalCommandExecutorException extends IllegalStateException {

    public IllegalCommandExecutorException() {
        super("Uniquement les joueurs peuvent exécuter cette commande.");
    }

}
