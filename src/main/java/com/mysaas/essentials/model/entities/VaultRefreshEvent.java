package com.mysaas.essentials.model.entities;

import org.springframework.context.ApplicationEvent;

public class VaultRefreshEvent extends ApplicationEvent {

    public VaultRefreshEvent(Object source) {
        super(source);
    }
}
