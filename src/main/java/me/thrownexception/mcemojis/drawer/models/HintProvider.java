package me.thrownexception.mcemojis.drawer.models;

import java.util.Random;

public class HintProvider {
    private static final String[] hints = new String[] {
            "Visit mc-emojis.com for updates!",
            "Right-click any emoji to insert its escaped unicode.",
            "The emojis are open source and copyright free."
    };
    private static final Random RANDOM = new Random();

    public static String next() {
        return hints[RANDOM.nextInt(hints.length)];
    }
}
