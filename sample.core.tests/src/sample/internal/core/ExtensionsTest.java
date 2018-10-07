package sample.internal.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import sample.core.Book;
import sample.internal.core.Extensions;

public class ExtensionsTest{

  @Test
  @DisplayName("とりあえず動かすだけ。")
  void test(){
    for(Book b:Extensions.loadBooks()){
      System.out.println("title:"+b.title);
      System.out.println("enable:"+b.enable);
    }
  }

}
