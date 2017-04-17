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

import java.util.*;

final class MyJSON implements JSON {

  Map<String, Object> data = new HashMap<>();

  @Override
  public JSON getObject(String name) {
    return (JSON) data.get(name);
  }

  @Override
  public JSON setObject(String name, JSON value) {
    data.put(name, value);
    return this;
  }

  @Override
  public String getString(String name) {
    return (String) data.get(name);
  }

  @Override
  public JSON setString(String name, String value) {
    data.put(name, value);
    return this;
  }

  @Override
  public void getObjects(Collection<String> names) {
    //puts entries of the backing map into a set, and all the entries
    //with objects as values have their keys or names added to the parameter collection
    for (Map.Entry<String, Object> entry : data.entrySet()){
      if (entry.getValue() instanceof JSON){
        names.add(entry.getKey());
      }
    }
  }

  @Override
  public void getStrings(Collection<String> names) {
    //puts entries of the backing map into a set, and all the entries
    //with Strings as values have their keys or names added to the parameter collection
    for (Map.Entry<String, Object> entry : data.entrySet()){
      if (entry.getValue() instanceof String){
        names.add(entry.getKey());
      }
    }
  }

}
