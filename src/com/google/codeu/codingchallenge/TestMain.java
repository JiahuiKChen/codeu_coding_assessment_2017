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

import java.util.Collection;
import java.util.HashSet;

final class TestMain {

  public static void main(String[] args) {

    final Tester tests = new Tester();

    tests.add("Empty Object", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ }");

        final Collection<String> strings = new HashSet<>();
        obj.getStrings(strings);

        Asserts.isEqual(strings.size(), 0);

        final Collection<String> objects = new HashSet<>();
        obj.getObjects(objects);

        Asserts.isEqual(objects.size(), 0);
      }
    });

    tests.add("String Value", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ \"name\":\"sam doe\" }");

        Asserts.isEqual("sam doe", obj.getString("name"));
     }
    });

    tests.add("Object Value", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {

        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ \"name\":{\"first\":\"sam\", \"last\":\"doe\" } }");

        final JSON nameObj = obj.getObject("name");

        Asserts.isNotNull(nameObj);
        Asserts.isEqual("sam", nameObj.getString("first"));
        Asserts.isEqual("doe", nameObj.getString("last"));
      }
    });

    /**
     * The JSON being tested is:
     * {
     *   "firstObj" : {"firstOfFirstObj" : {"1-1":"yay" , "1-2":"secondOfFirst"},
     *                 "secondOfFirstObj": {"1-3":"thirdItem" , "1-4":"SS"}
     *                },
     *   "secondObj" : {"firstOfSecondObj": {"2-1":"food" , "2-2":"moo"},
     *                   "secondOfSecondObj": {2-3":"yikes" , "2-4":"complex"}
     *                  }
     * }
     */
    tests.add("Multiple Nested Objects", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {
        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{ \"firstObj\" : { \"firstOfFirstObj\" :{ \"1-1\" : \"yay \" " +
                ", \"1-2\" : \" secondOfFirst\"}, \"secondOfFirstObj\": {\"1-3\" : \"thirdItem\", \"1-4\" :\"SS\"}} , \"secondObj\" :" +
                "{\"firstOfSecondObj\" : {\"2-1\":\"food\" ,\"2-2\":\"moo\"}, \"secondOfSecondObj\" : {\"2-3\":\"yikes\", \"2-4\":\"complex\"}}}");

        final JSON firstObject = obj.getObject("firstObj");
        Asserts.isNotNull(firstObject);
        final JSON secondObject = obj.getObject("secondObj");
        Asserts.isNotNull(secondObject);

        //2 Objects and no strings in firstObj and secondObj
        Collection<String> inFirstObject = new HashSet<>();
        firstObject.getObjects(inFirstObject);
        Asserts.isEqual(inFirstObject.size(), 2);
        Collection<String> inSecondObject = new HashSet<>();
        firstObject.getObjects(inSecondObject);
        Asserts.isEqual(inSecondObject.size(), 2);

        //testing each key and value
        Asserts.isEqual("yay ", firstObject.getObject("firstOfFirstObj").getString("1-1"));
        Asserts.isEqual(" secondOfFirst", firstObject.getObject("firstOfFirstObj").getString("1-2"));
        Asserts.isEqual("thirdItem", firstObject.getObject("secondOfFirstObj").getString("1-3"));
        Asserts.isEqual("SS", firstObject.getObject("secondOfFirstObj").getString("1-4"));
        Asserts.isEqual("food", secondObject.getObject("firstOfSecondObj").getString("2-1"));
        Asserts.isEqual("moo", secondObject.getObject("firstOfSecondObj").getString("2-2"));
        Asserts.isEqual("yikes", secondObject.getObject("secondOfSecondObj").getString("2-3"));
        Asserts.isEqual("complex", secondObject.getObject("secondOfSecondObj").getString("2-4"));
      }
    });

    tests.add("Strange Spacing", new Test() {
      @Override
      public void run(JSONFactory factory) throws Exception {

        final JSONParser parser = factory.parser();
        final JSON obj = parser.parse("{\n \"name\":{\"first\":\"sam\"\n,      \"last\"\n:\"doe\" } }");

        final JSON nameObj = obj.getObject("name");

        Asserts.isNotNull(nameObj);
        Asserts.isEqual("sam", nameObj.getString("first"));
        Asserts.isEqual("doe", nameObj.getString("last"));
      }
    });

    tests.run(new JSONFactory(){
      @Override
      public JSONParser parser() {
        return new MyJSONParser();
      }

      @Override
      public JSON object() {
        return new MyJSON();
      }
    });
  }
}
