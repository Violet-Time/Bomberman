package com.example.bomberman.util;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class QueryHelper {
    public static Map<String, List<String>> splitQuery(URI uri) {
        if (uri.getQuery() == null || uri.getQuery().isBlank()) {
            return Collections.emptyMap();
        }
        return Arrays.stream(uri.getQuery().split("&"))
                .map(QueryHelper::splitQueryParameter)
                .collect(Collectors.groupingBy(AbstractMap.SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
    }

    public static AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        int idx = it.indexOf("=");
        String key = idx > 0 ? it.substring(0, idx) : it;
        String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        assert value != null;
        return new AbstractMap.SimpleImmutableEntry<>(
                URLDecoder.decode(key, StandardCharsets.UTF_8),
                URLDecoder.decode(value, StandardCharsets.UTF_8)
        );
    }
}
