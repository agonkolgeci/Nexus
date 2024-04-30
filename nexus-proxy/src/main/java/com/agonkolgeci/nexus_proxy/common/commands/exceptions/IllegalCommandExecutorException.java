package com.agonkolgeci.nexus_proxy.common.commands.exceptions;

public class IllegalCommandExecutorException extends IllegalStateException {

    public IllegalCommandExecutorException() {
        super("Uniquement les joueurs peuvent exécuter cette commande.");
    }

}
