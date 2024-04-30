package com.agonkolgeci.nexus_proxy.core.hub.exceptions;

public class AlreadyInHubException extends IllegalStateException {

    public AlreadyInHubException() {
        super("Vous êtes déjà connecté à un Hub.");
    }

}
