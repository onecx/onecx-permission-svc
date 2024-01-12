package io.github.onecx.permission.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import io.github.onecx.permission.common.models.TokenConfig;
import io.smallrye.jwt.JsonUtils;

public final class TokenUtil {

    private TokenUtil() {
    }

    public static List<String> findClaimWithRoles(TokenConfig config, Object value, String[] path) {
        JsonValue first = JsonUtils.wrapValue(value);
        JsonValue claimValue = findClaimValue(first, path, 1);

        if (claimValue instanceof JsonArray) {
            return convertJsonArrayToList((JsonArray) claimValue);
        } else if (claimValue != null) {
            if (claimValue.toString().isBlank()) {
                return Collections.emptyList();
            }
            return Arrays.asList(claimValue.toString().split(config.tokenClaimSeparator().orElse(" ")));
        } else {
            return Collections.emptyList();
        }
    }

    static List<String> convertJsonArrayToList(JsonArray claimValue) {
        List<String> list = new ArrayList<>(claimValue.size());
        for (int i = 0; i < claimValue.size(); i++) {
            String claimValueStr = claimValue.getString(i);
            if (claimValueStr.isBlank()) {
                continue;
            }
            list.add(claimValue.getString(i));
        }
        return list;
    }

    private static JsonValue findClaimValue(JsonValue json, String[] pathArray, int step) {
        if (json == null) {
            return null;
        }
        if (step < pathArray.length) {
            if (json instanceof JsonObject) {
                JsonValue claimValue = json.asJsonObject().get(pathArray[step].replace("\"", ""));
                return findClaimValue(claimValue, pathArray, step + 1);
            }
        }
        return json;
    }

}
