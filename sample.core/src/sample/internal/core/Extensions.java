package sample.internal.core;

import static java.util.stream.Collectors.toList;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

import sample.core.Activator;
import sample.core.Book;
import sample.core.Book.Author;
import sample.core.Book.Page;
import sample.core.IBookPage;

/**
 * sample.coreの拡張ポイントを処理します。
 * 面倒くさいのでnullチェックや不正な要素などの処理はしてません。
 * @author nodamushi
 */
public class Extensions{

  //拡張ポイント名
  private static final String BOOKS_EXTENSION_POINT_ID= Activator.PLUGIN_ID + ".books"; //$NON-NLS-1$

  // 要素名や属性名
  private static final String ELEMENT_BOOK="book"; //$NON-NLS-1$
  private static final String ELEMENT_AUTHOR="author"; //$NON-NLS-1$
  private static final String ELEMENT_ENABLE="enablement"; //$NON-NLS-1$
  private static final String ELEMENT_PAGE="page"; //$NON-NLS-1$
  private static final String ELEMENT_REFERENCE="reference"; //$NON-NLS-1$
  private static final String ATTR_TITLE ="title"; //$NON-NLS-1$
  private static final String ATTR_ISBN="isbn"; //$NON-NLS-1$
  private static final String ATTR_CATEGORY="category"; //$NON-NLS-1$
  private static final String ATTR_NAME="name"; //$NON-NLS-1$
  private static final String ATTR_IMAGE="image"; //$NON-NLS-1$
  private static final String ATTR_NUMBER="number"; //$NON-NLS-1$
  private static final String ATTR_CONTENTS="contents"; //$NON-NLS-1$

  /**
   * ImageDescriptorとしてbundleからリソースを取得する
   * @param bundle 探すbundle.null不可
   * @param path 探すパス.null可
   * @return 見つからない場合はnull
   */
  static ImageDescriptor findImage(Bundle bundle,String path){
    return path == null || path.isEmpty()?null:AbstractUIPlugin.imageDescriptorFromPlugin(bundle.getSymbolicName(),path);
  }
  /**
   * bundleからリソースのURLを検索する
   * @param bundle 探すbundle.null不可
   * @param path 探すパス.null可
   * @return 見つからない場合はnull
   */
  static URL findResource(Bundle bundle,String path){
    return path == null || path.isEmpty()?null:FileLocator.find(bundle,new Path(path));
  }

  /**
   * Bookインスタンスに変換する
   * @param book book要素
   * @return Bookインスタンス
   * @throws CoreException 何らかの例外
   */
  static Book createBook(IConfigurationElement book)throws CoreException{
    IExtension e = book.getDeclaringExtension();
    Bundle bundle=Platform.getBundle(e.getNamespaceIdentifier());
    //TODO 本来ならtitleがnullでないかなど、ちゃんとチェックしましょう。（サボり）
    String title = book.getAttribute(ATTR_TITLE);
    String isbn = book.getAttribute(ATTR_ISBN);
    String cate = book.getAttribute(ATTR_CATEGORY);
    IConfigurationElement[] enablement=book.getChildren(ELEMENT_ENABLE);
    boolean enable = true;
    if(enablement.length==1){
      EvaluationContext ctx = new EvaluationContext(null,"");
      ctx.addVariable("hoge","HOGE"); // hogeという変数を定義
      Expression exp=ExpressionConverter.getDefault().perform(enablement[0]);
      EvaluationResult r = exp.evaluate(ctx);
      enable = r == EvaluationResult.TRUE;
    }

    List<Author> authors=Arrays.stream(book.getChildren(ELEMENT_AUTHOR))
        .map(a->new Book.Author(
            a.getAttribute(ATTR_NAME),
            findImage(bundle,a.getAttribute(ATTR_IMAGE))))
        .collect(toList());
    List<Page> pages=Arrays.stream(book.getChildren(ELEMENT_PAGE))
        .map(p->{
          try{
            int num = Integer.parseInt(p.getAttribute(ATTR_NUMBER));
            IBookPage contents = (IBookPage)p.createExecutableExtension(ATTR_CONTENTS);
            return new Book.Page(num,contents);
          }catch(Exception ex){
            ex.printStackTrace();
            return null;
          }
        }).filter(Objects::nonNull)
        .collect(toList());
    List<String> refs=Arrays.stream(book.getChildren(ELEMENT_REFERENCE))
        .map(r->r.getAttribute(ATTR_ISBN))
        .filter(r->r!=null && !r.isEmpty())
        .collect(toList());
    return new Book(title,isbn,cate,enable,authors,pages,refs);
  }

  /**
   * sample.core.booksを読み取り、不変リストにして返す
   */
  public static List<Book> loadBooks(){
    IExtensionPoint point=Platform.getExtensionRegistry().getExtensionPoint(BOOKS_EXTENSION_POINT_ID);
    if(point == null)return List.of();

    return Collections.unmodifiableList(Arrays.stream(point.getExtensions())
        .flatMap(e->Arrays.stream(e.getConfigurationElements()))
        .map(c->{
          try{
            return createBook(c);
          }catch(Exception e){
            e.printStackTrace();
            return null;
          }
        })
        .filter(Objects::nonNull)
        .collect(toList()));
  }

}
