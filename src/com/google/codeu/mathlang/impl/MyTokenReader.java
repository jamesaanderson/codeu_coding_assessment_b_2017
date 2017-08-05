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

package com.google.codeu.mathlang.impl;

import java.io.IOException;

import com.google.codeu.mathlang.core.tokens.*;
import com.google.codeu.mathlang.parsing.TokenReader;

// MY TOKEN READER
//
// This is YOUR implementation of the token reader interface. To know how
// it should work, read src/com/google/codeu/mathlang/parsing/TokenReader.java.
// You should not need to change any other files to get your token reader to
// work with the test of the system.
public final class MyTokenReader implements TokenReader {

  String source;
  private int pos = 0;

  public MyTokenReader(String source) {
    // Your token reader will only be given a string for input. The string will
    // contain the whole source (0 or more lines).
    this.source = source;
    pos = 0;
  }

  @Override
  public Token next() throws IOException {
    // Most of your work will take place here. For every call to |next| you should
    // return a token until you reach the end. When there are no more tokens, you
    // should return |null| to signal the end of input.

    // If for any reason you detect an error in the input, you may throw an IOException
    // which will stop all execution.

    String substr = source.substring(pos); 

    if (substr.length() == 0) {
      return null;
    }

    int start = 0;
    int end = start;

    char startChar = substr.charAt(start);

    if (startChar == '\n' || startChar == ' ') {
      pos++;
      return next();
    }

    // SYMBOL
    if (startChar == '-' || startChar == '+' || startChar == '=' || startChar == ';') {
      pos++;
      return new SymbolToken(startChar);
    }

    // STRING
    if (startChar == '"') {
      // start quote is not a part of the string
      start++;
      end = start;

      while (substr.charAt(end) != '"') {
        end++;

        if (end > (substr.length() - 1)) {
          throw new IOException("Error: Unclosed string");
        }
      }

      // inclusive to exclusive (end char will be the end quote)
      String string = substr.substring(start, end);

      // position will be the current position plus the length of the string plus 1 to land on the next token type
      pos = pos + end + 1;

      return new StringToken(string);
    }

    // NAME
    if (Character.isLetter(startChar)) {
      while (Character.isLetter(substr.charAt(end)) || Character.isDigit(substr.charAt(end))) {
        end++; 

        if (end > (substr.length() - 1)) {
          throw new IOException("Error: Invalid name");
        }
      }

      String name = substr.substring(start, end);

      pos = pos + end;

      return new NameToken(name);
    }

    // NUMBER
    if (Character.isDigit(startChar) || startChar == '.') {
      // a double can only contain one decimal point
      int decimals = 0;

      if (startChar == '.') {
        decimals++;
      }

      while (Character.isDigit(substr.charAt(end)) || substr.charAt(end) == '.') {
        end++;

        if (substr.charAt(end) == '.') {
          decimals++;
        }

        if (end > (substr.length() - 1)) {
          throw new IOException("Error: Invalid number");
        }
      }

      if (decimals > 1) {
        throw new IOException("Error: Invalid number");
      }

      double number = Double.parseDouble(substr.substring(start, end));

      pos = pos + end;

      return new NumberToken(number);
    }

    throw new IOException("Syntax Error");
  }
}
