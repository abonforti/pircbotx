package com.abonforti.service.impl;

import com.abonforti.service.NoticeService;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.NoticeEvent;

public class DefaultNoticeChannelService implements NoticeService {

    @Override
    public void process(final NoticeEvent event, final String text) {
        final String user = StringUtils.split(text, " ")[0];
        final String kickMessage = StringUtils.removeStart(text, user);
        event.getBot().sendRaw().rawLine(buildKickCommand(event.getChannel().getName(), user, kickMessage));
    }

    private String buildKickCommand(final String channel, final String user, final String message) {
        return "KICK " + channel + " " + user + " :" + StringUtils.trim(message);
    }
}
