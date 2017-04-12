// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.codingchallenge;

import java.io.IOException;

final class MyJSONParser implements JSONParser {

    @Override
    public JSON parse(String in) throws IOException {
        in = in.trim();
        return recursiveParseObject(in);
    }

    public JSON recursiveParseObject(String in) {
        if (!in.startsWith("{") || !in.endsWith("}"))
            throw new IllegalArgumentException("Invalid JSON (Missing start or end curly brace)");

        JSON object = new MyJSON();

        in = in.substring(1, in.length() - 1).trim();

        boolean hasMoreKeys = in.startsWith("\"");
        keyFinder:
        while (hasMoreKeys) {
            // Get the key from within the quotation marks.
            int keyEndIndex = in.indexOf('"', 1);
            if (keyEndIndex == -1)
                throw new IllegalArgumentException("Could not parse object: missing closing quotation mark on key.");

            String key = in.substring(1, keyEndIndex);

            // Find the value
            int delimiterIndex = in.indexOf(':', keyEndIndex + 1);
            if (delimiterIndex == -1)
                throw new IllegalArgumentException("Could not parse object: missing colon delimiter between key and value.");

            // Search for either a quotation mark (") or opening brace ({) to signify start of value.
            int valueEnd = 0;
            for (int index = delimiterIndex + 1; index < in.length(); index++) {
                char currentChar = in.charAt(index);
                if (currentChar == '"') {
                    // We have a string value, so we need to find the end of it and add it to the object.
                    valueEnd = in.indexOf('"', index + 1) + 1;
                    if (valueEnd == -1)
                        throw new IllegalArgumentException("Could not parse object: missing closing quotation mark on value.");

                    String value = in.substring(index + 1, valueEnd - 1);

                    // Add the value as a string to the object.
                    object.setString(key, value);
                    break;
                } else if (currentChar == '{') {
                    // We have an object value, so we need to find the end of it and then recursively parse it.
                    valueEnd = findEndOfObject(in, index);
                    if (valueEnd == -1)
                        throw new IllegalArgumentException("Could not parse object: opening and closing curly braces do not add up.");

                    String value = in.substring(index, valueEnd);

                    // Recursively parse the value, and assign it as an object to our own object.
                    object.setObject(key, recursiveParseObject(value));
                    break;
                }
            }

            // Search for a comma, signifying that we have more keys.
            for (int index = valueEnd; index < in.length(); index++) {
                if (in.charAt(index) == ',') {
                    // We found a comma, so there is probably another key.
                    hasMoreKeys = true;
                    // Trim down the input so that it starts at the next key.
                    in = in.substring(index + 1).trim();
                    continue keyFinder;
                }
            }

            // If we got this far, there are no more keys.
            hasMoreKeys = false;
        }

        return object;
    }

    /**
     * Finds the end index of an object enclosed in curly braces.
     *
     * @param in       The input string.
     * @param startPos The position at which to begin searching.
     * @return The index after the final closing brace of the object, or -1 if the end could not be found.
     */
    private int findEndOfObject(String in, int startPos) {
        int endIndex = -1;

        // Keeps track of how many opening curly braces we need to close before we are at the end.
        int bracesLeft = 0;

        for (int index = startPos; index < in.length(); index++) {
            // Increase the counter when finding an opening curly brace.
            if (in.charAt(index) == '{')
                bracesLeft++;

                // Decrease the counter when finding a closing curly brace.
            else if (in.charAt(index) == '}')
                bracesLeft--;

            // Counter = 0 means we have reached the end.
            if (bracesLeft == 0) {
                endIndex = index + 1;
                break;
            }
        }

        return endIndex;
    }

}
