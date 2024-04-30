package com.agonkolgeci.nexus_proxy.core.hub.exceptions;

public class NoneHubAvailableException extends IllegalStateException {

    public NoneHubAvailableException() {
        super("Il n'y a aucun Hub disponible actuellement.");
    }

}
