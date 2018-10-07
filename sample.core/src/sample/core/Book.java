package sample.core;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * sample.core.booksのbook要素を表すクラス
 * @author nodamushi
 */
public class Book{
  /**
   * author要素
   */
  public static class Author{
    public final String name;
    public final ImageDescriptor image;
    public Author(String name,ImageDescriptor image){
      this.name=name;  this.image=image;
    }
  }

  /**
   * page要素
   */
  public static class Page{
    public final int page;
    public final IBookPage contents;
    public Page(int page,IBookPage contents){
      this.page=page;  this.contents=contents;
    }
  }

  public final String title;
  public final String isbn;
  public final String category;
  public final boolean enable;
  public final List<Author> authors;
  public final List<Page> pages;
  public final List<String> references;

  public Book(String title,String isbn,String category,boolean enable,
              List<Author> authors,List<Page> pages,List<String> references){
    this.title = title;   this.isbn = isbn;   this.category = category;
    this.enable = enable;
    this.authors = List.of(authors.toArray(new Author[authors.size()]));
    this.pages   = List.of(pages.toArray(new Page[pages.size()]));
    this.references = List.of(references.toArray(new String[references.size()]));
  }
}
