package com.incuube.bot.util;

import java.util.Map;
import java.util.Optional;


public class ParamsExtractor {
    public static <T> Optional<T> getParamFromMap(Map<String, Object> params, String paramName) {
        Object result = paramsFinder(params, paramName);
        return (Optional<T>) Optional.ofNullable(result);
    }


    private static Object paramsFinder(Map<String, Object> params, String paramName) {
        int position = paramName.indexOf(".");

        if (position != -1) {
            Object value = params.get(paramName.substring(0, position));
            if (value != null) {
                return paramsFinder(
                        (Map<String, Object>) value,
                        paramName.substring(position + 1));
            }
            return null;
        }
        return params.get(paramName);
    }
}
