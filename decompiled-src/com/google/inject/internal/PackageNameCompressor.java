package com.google.inject.internal;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class PackageNameCompressor {

    static final String LEGEND_HEADER = "\n\n======================\nFull classname legend:\n======================\n";
    static final String LEGEND_FOOTER = "========================\nEnd of classname legend:\n========================\n";
    private static final ImmutableSet<String> PACKAGES_SKIPPED_IN_LEGEND = ImmutableSet.of("java.lang.", "java.util.");
    private static final Splitter PACKAGE_SPLITTER = Splitter.on('.');
    private static final Joiner PACKAGE_JOINER = Joiner.on('.');
    private static final Pattern CLASSNAME_PATTERN = Pattern.compile("[\\W](([a-z_0-9]++[.]){2,}+[A-Z][\\w$]*)");
    private static final Pattern QUOTED_PATTERN = Pattern.compile("([^\\\"]+)((\\\")?[^\\\"\\r\\n]*\\\")?");

    static String compressPackagesInMessage(String input) {
        Matcher matcher = PackageNameCompressor.CLASSNAME_PATTERN.matcher(input);
        Set<String> names = new HashSet();

        while (matcher.find()) {
            String name = matcher.group(1);

            names.add(name);
        }

        Map<String, String> replacementMap = shortenNames(names);

        if (replacementMap.isEmpty()) {
            return input;
        } else {
            StringBuilder output = new StringBuilder();
            Set<String> replacedShortNames = replaceFullNames(input, replacementMap, output);

            if (replacedShortNames.isEmpty()) {
                return input;
            } else {
                Objects.requireNonNull(replacedShortNames);
                String classNameLegend = buildClassNameLegend(Maps.filterKeys(replacementMap, replacedShortNames::contains));

                return output.append(classNameLegend).toString();
            }
        }
    }

    private static ImmutableSet<String> replaceFullNames(String input, Map<String, String> replacementMap, StringBuilder output) {
        ImmutableSet.Builder<String> replacedShortNames = ImmutableSet.builder();
        List<String> shortNames = (List) replacementMap.keySet().stream().sorted(Ordering.natural().reverse()).collect(Collectors.toList());
        Matcher matcher = PackageNameCompressor.QUOTED_PATTERN.matcher(input);

        while (matcher.find()) {
            String replaced = matcher.group(1);

            for (String shortName : shortNames) {
                String fullName = (String) replacementMap.get(shortName);
                int beforeLen = replaced.length();

                replaced = replaced.replace(fullName, shortName);
                if (replaced.length() < beforeLen) {
                    replacedShortNames.add(shortName);
                }
            }

            output.append(replaced);
            String quoted = matcher.group(2);

            if (quoted != null) {
                output.append(quoted);
            }
        }

        return replacedShortNames.build();
    }

    private static String buildClassNameLegend(Map<String, String> replacementMap) {
        StringBuilder legendBuilder = new StringBuilder();
        int longestKey = ((String) replacementMap.keySet().stream().max(Comparator.comparing(String::length)).get()).length();

        for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
            String shortName = (String) entry.getKey();
            String fullName = (String) entry.getValue();
            String prefix = fullName.substring(0, fullName.length() - shortName.length());

            if (!PackageNameCompressor.PACKAGES_SKIPPED_IN_LEGEND.contains(prefix) || shortName.contains(".")) {
                legendBuilder.append(shortName).append(": ").append(Strings.repeat(" ", longestKey - shortName.length())).append('"').append(fullName).append('"').append("\n");
            }
        }

        return legendBuilder.length() == 0 ? "" : Messages.bold("\n\n======================\nFull classname legend:\n======================\n") + Messages.faint(legendBuilder.toString()) + Messages.bold("========================\nEnd of classname legend:\n========================\n");
    }

    private static Map<String, String> shortenNames(Collection<String> names) {
        HashMultimap<String, List<String>> shortNameToPartsMap = HashMultimap.create();

        for (String name : names) {
            List<String> parts = new ArrayList(PackageNameCompressor.PACKAGE_SPLITTER.splitToList(name));
            String className = (String) parts.remove(parts.size() - 1);

            shortNameToPartsMap.put(className, parts);
        }

        while (true) {
            List<String> conflictingShortNames = new ArrayList();

            for (Map.Entry<String, Collection<List<String>>> entry : shortNameToPartsMap.asMap().entrySet()) {
                if (((Collection) entry.getValue()).size() > 1) {
                    conflictingShortNames.add((String) entry.getKey());
                }
            }

            if (conflictingShortNames.isEmpty()) {
                conflictingShortNames = new TreeMap();

                for (Map.Entry<String, Collection<List<String>>> entry : shortNameToPartsMap.asMap().entrySet()) {
                    conflictingShortNames.put((String) entry.getKey(), PackageNameCompressor.PACKAGE_JOINER.join((Iterable) Iterables.getOnlyElement((Iterable) entry.getValue())) + "." + (String) entry.getKey());
                }

                return conflictingShortNames;
            }

            for (String conflictingShortName : conflictingShortNames) {
                for (List<String> parts : shortNameToPartsMap.removeAll(conflictingShortName)) {
                    String newShortName = (String) parts.remove(parts.size() - 1) + "." + conflictingShortName;

                    if (!parts.isEmpty()) {
                        shortNameToPartsMap.put(newShortName, parts);
                    }
                }
            }
        }
    }

    private PackageNameCompressor() {
        super();
    }
}
