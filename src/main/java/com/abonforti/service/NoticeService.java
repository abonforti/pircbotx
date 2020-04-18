package com.abonforti.service;

import org.pircbotx.hooks.events.NoticeEvent;

public interface NoticeService {

    void process(final NoticeEvent event, final String text);
}
