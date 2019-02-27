package com.incuube.bot.services.util;

import org.springframework.core.Ordered;

public class HandlerOrderConstants {
    public static final int USER_HANDLER_VALUE = Ordered.HIGHEST_PRECEDENCE;
    public static final int ACTION_HANDLER_VALUE = 0;
    public static final int VALIDATION_HANDLER_VALUE = 100;
    public static final int PARAMS_DB_HANDLER_VALUE = 200;
    public static final int NEXT_ACTION_HANDLER_VALUE = Ordered.LOWEST_PRECEDENCE;
}
