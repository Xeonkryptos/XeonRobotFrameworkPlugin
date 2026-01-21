package dev.xeonkryptos.xeonrobotframeworkplugin.config;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A custom collator that combines parts of the strength level {@link #PRIMARY} and {@link #TERTIARY}. It considers case-sensitivity, but still ignores accents
 * while working with the provided rules.
 */
public class RuleBasedNormalizerCollator extends RuleBasedCollator {

    private static final Pattern ACCENT_PATTERN = Pattern.compile("\\p{M}");

    private Matcher matcher;

    public RuleBasedNormalizerCollator(String rules) throws ParseException {
        super(rules);
    }

    @Override
    public synchronized int compare(String source, String target) {
        if (super.compare(source, target) == 0) {
            return 0;
        }

        if (matcher == null) {
            matcher = ACCENT_PATTERN.matcher(source);
        }
        String normalizedSource = Normalizer.normalize(source, Form.NFD);
        String normalizedTarget = Normalizer.normalize(target, Form.NFD);

        normalizedSource = matcher.reset(normalizedSource).replaceAll("");
        normalizedTarget = matcher.reset(normalizedTarget).replaceAll("");

        return super.compare(normalizedSource, normalizedTarget);
    }
}
