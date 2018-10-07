package sample.core;

import java.util.List;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import sample.internal.core.Extensions;

/**
 * アクティベーター
 * @author nodamushi
  */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "sample.core"; //$NON-NLS-1$
	private static Activator plugin;
	public static Activator getDefault() {return plugin;}

	private List<Book> extension;
	/**
	 * 拡張ポイントのbook要素を返します
	 * @return 不変リスト
	 */
  public List<Book> getBooks(){return extension;}

	@Override
  public void start(BundleContext context) throws Exception {
		super.start(context);
		extension = Extensions.loadBooks();
		plugin = this;
	}

	@Override
  public void stop(BundleContext context) throws Exception {
	  extension = null;
		plugin = null;
		super.stop(context);
	}
}