package com.student.management.platform.global;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HtmlParser {


  public String parseHtml(String html) {
    String[] tags = {"script", "html", "head", "body"};
    String newHtml = html;
    for (String tag : tags) {
      String regex = "(?i)<" + tag + ".*?>.*?</" + tag + ">";
      Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
      Matcher matcher = pattern.matcher(newHtml);
      newHtml = matcher.replaceAll("");
    }
    return newHtml;
  }

}
