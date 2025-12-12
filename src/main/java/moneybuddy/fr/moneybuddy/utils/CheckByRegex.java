/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.utils;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class CheckByRegex {

  public boolean validate(String string, String regexPattern) {
    return Pattern.compile(regexPattern).matcher(string).matches();
  }
}
