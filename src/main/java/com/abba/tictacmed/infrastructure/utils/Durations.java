package com.abba.tictacmed.infrastructure.utils;

import java.time.Duration;
import java.util.Locale;

public final class Durations {

    public static Duration parseFriendlyDurationToSeconds(String text) {
        if (text == null || text.isBlank()) throw new IllegalArgumentException("frequency is required");
        String raw = text.trim();

        // 1) Accept ISO-8601 duration strings like PT8H, P1D (case-insensitive)
        String iso = raw.toUpperCase(Locale.ROOT);
        if (iso.startsWith("P")) {
            try {
                return Duration.parse(iso);
            } catch (IllegalArgumentException ignored) {
                // fall through to friendly parser
            }
        }

        String s = raw.toLowerCase(Locale.ROOT);
        // 2) Backward compatibility: if it is purely digits, treat as seconds
        if (s.chars().allMatch(Character::isDigit)) {
            return Duration.ofSeconds(Long.parseLong(s));
        }

        // 3) Friendly short formats: e.g., 8h, 12h, 30m, 1d, combinations like 1d12h
        long total = 0;
        int i = 0;
        int n = s.length();
        while (i < n) {
            // read number
            int j = i;
            while (j < n && Character.isDigit(s.charAt(j))) j++;
            if (j == i) throw new IllegalArgumentException("Invalid duration segment at position " + i + ": " + s);
            long value = Long.parseLong(s.substring(i, j));
            if (j >= n) throw new IllegalArgumentException("Missing unit after number in duration: " + s);
            char unit = s.charAt(j);
            long factor = switch (unit) {
                case 's' -> 1;
                case 'm' -> 60;
                case 'h' -> 3600;
                case 'd' -> 86400;
                default -> throw new IllegalArgumentException("Unsupported duration unit '" + unit + "' in: " + s);
            };
            total += value * factor;
            j++;
            i = j;
        }
        if (total <= 0) throw new IllegalArgumentException("frequency must be positive");
        return Duration.ofSeconds(total);
    }

}
