package ru.ystu.util;

import jakarta.servlet.http.HttpServletRequest;

public class PathParser {
    public static Long extractId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }
        try {
            return Long.parseLong(pathInfo.substring(1).split("/")[0]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}