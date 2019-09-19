/*
 * Copyright 2019 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.epam.eco.schemacatalog.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * @author Andrei_Tytsik
 */
public final class SchemaVersionRange {

    private static final String VERSION_REGEX = "^(0|[1-9][0-9]*)$";
    private static final Pattern VERSION_PATTERN = Pattern.compile(VERSION_REGEX);

    private static final String VERSION_RANGE_REGEX = "^(\\[)[ ]*(|0|[1-9][0-9]*)[ ]*\\,[ ]*(|0|[1-9][0-9]*)[ ]*(\\])$";
    private static final Pattern VERSION_RANGE_PATTERN = Pattern.compile(VERSION_RANGE_REGEX);

    public final int earliest;
    public final int latest;

    public SchemaVersionRange(int earliest, int latest) {
        Validate.isTrue(earliest >= 0, "Earliest version is invalid");
        Validate.isTrue(latest >= earliest, "Latest version is invalid");

        this.earliest = earliest;
        this.latest = latest;
    }

    public boolean before(int version) {
        return latest < version;
    }

    public boolean contains(int version) {
        return earliest <= version && version <= latest;
    }

    public boolean after(int version) {
        return version < earliest;
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]", earliest, latest);
    }

    public static SchemaVersionRange with(int earliest, int latest) {
        return new SchemaVersionRange(earliest, latest);
    }

    public static SchemaVersionRange parse(String versionStr) {
        versionStr = StringUtils.stripToNull(versionStr);

        Validate.notNull(versionStr, "Version string is blank");

        if (isVersionStr(versionStr)) {
            return parseVersionStr(versionStr);
        } else if (isVersionRangeStr(versionStr)) {
            return parseVersionRangeStr(versionStr);
        } else {
            throw new IllegalArgumentException(String.format("Invalid version string %s", versionStr));
        }
    }

    private static boolean isVersionStr(String versionStr) {
        return VERSION_PATTERN.matcher(versionStr).matches();
    }

    private static SchemaVersionRange parseVersionStr(String versionStr) {
        int version = Integer.parseInt(versionStr);
        return with(version, version);
    }

    private static boolean isVersionRangeStr(String versionStr) {
        return VERSION_RANGE_PATTERN.matcher(versionStr).matches();
    }

    private static SchemaVersionRange parseVersionRangeStr(String versionStr) {
        Matcher matcher = VERSION_RANGE_PATTERN.matcher(versionStr);
        matcher.find();

        String earliestStr = StringUtils.stripToNull(matcher.group(2));
        int earliest = earliestStr == null ? 0 : Integer.parseInt(earliestStr);

        String latestStr = StringUtils.stripToNull(matcher.group(3));
        int latest = latestStr == null ? Integer.MAX_VALUE : Integer.parseInt(latestStr);

        return with(earliest, latest);
    }

}
