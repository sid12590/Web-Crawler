package demo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * @author krutsdave
 *
 */

class MyThread implements Runnable {

	String url;
	static Integer pageSize, pageLength, currentPage;
	int currentLength;
	static Map<String, Integer> globalHtmlElementMap = new HashMap<>();

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			int currPage;
			synchronized (currentPage) {
				currentPage++;
				currPage = currentPage;
			}
			if (currPage <= pageSize && currentLength <= pageLength) {
				HtmlPage currHtmlPage = parseHtml(url);
				displayHtmlDetails(currHtmlPage);
				for (String link : currHtmlPage.getHyperLinks()) {
					MyThread myThread = new MyThread();
					myThread.currentLength = this.currentLength + 1;
					myThread.url = link;
					Thread t = new Thread(myThread);
					t.start();
				}
			}
		} catch (IOException | BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Displays counts of each element across all page
	 */
	public static void displayGlobalCount() {
		// TODO Auto-generated method stub
		System.out.println("Global Count of elements:");
		Map<String, Integer> treeMap = new TreeMap<String, Integer>(globalHtmlElementMap);
		treeMap.remove("COMMENT");
		treeMap.remove("P-IMPLIED");
		int count = 0;
		for (String element : treeMap.keySet()) {
			System.out.println(element + ": " + treeMap.get(element));
			addElementCountGlobally(element, count);
		}
		System.out.println("Total count of elements:" + treeMap.size() + "\n");
	}

	/**
	 * Parses HTML page from URL
	 * 
	 * @param htmlUrl
	 * @return
	 * @throws IOException
	 * @throws BadLocationException
	 */
	public static HtmlPage parseHtml(String htmlUrl) throws IOException, BadLocationException {
		HtmlPage htmlPage = new HtmlPage();
		Map<String, Integer> elementDetails = new HashMap<>();
		Set<String> urls = new HashSet<>();
		URL url = new URL(htmlUrl);
		HTMLEditorKit kit = new HTMLEditorKit();
		HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
		doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
		Reader HTMLReader = new InputStreamReader(url.openConnection().getInputStream());
		kit.read(HTMLReader, doc, 0);

		// Get an iterator for all HTML tags.
		ElementIterator it = new ElementIterator(doc);
		Element elem;

		while ((elem = it.next()) != null) {
			Integer elementcount = (Integer) elementDetails.get(elem.getName().toUpperCase());
			// System.out.println(elem.getName());
			if (elementcount != null) {
				elementDetails.put(elem.getName().toUpperCase(), elementcount + 1);
			} else {
				elementDetails.put(elem.getName().toUpperCase(), 1);
			}

			AttributeSet sas = elem.getAttributes();

			String link = (String) sas.getAttribute(HTML.Attribute.HREF);
			addLinksToUrlList(link, urls);
			/*
			 * if (link != null && ((link.startsWith("http") ||
			 * link.startsWith("https")) && (link.endsWith(".com") ||
			 * link.endsWith(".html")))) { urls.add(link); }
			 */

		}
		HTMLDocument.Iterator itA = doc.getIterator(HTML.Tag.A);
		Integer aInitialCount = elementDetails.get(HTML.Tag.A);
		int aCount = 0;
		while ((itA.isValid())) {
			aCount++;
			String alink = (String) itA.getAttributes().getAttribute(HTML.Attribute.HREF);
			addLinksToUrlList(alink, urls);
			/*
			 * if (alink != null && ((alink.startsWith("http") ||
			 * alink.startsWith("https")) && (alink.endsWith(".com") ||
			 * alink.endsWith(".html")))) { urls.add(alink); }
			 */
			itA.next();
		}
		if (aCount > 0) {
			if (aInitialCount != null)
				elementDetails.put(HTML.Tag.A.toString().toUpperCase(), aCount + aInitialCount);
			else
				elementDetails.put(HTML.Tag.A.toString().toUpperCase(), aCount);

		}
		htmlPage.setElementsMap(elementDetails);
		htmlPage.setUrl(htmlUrl);
		htmlPage.setHyperLinks(urls);
		return htmlPage;
	}

	/**
	 * Adds links to ursl set
	 * 
	 * @param link
	 * @param urls
	 */
	private static void addLinksToUrlList(String link, Set<String> urls) {
		if (link != null && ((link.startsWith("http") || link.startsWith("https"))
				&& (link.endsWith(".com") || link.endsWith(".html")))) {
			urls.add(link);
		}
	}

	/**
	 * Displays elements for each HTML page
	 * 
	 * @param htmlPage
	 */
	private synchronized static void displayHtmlDetails(HtmlPage htmlPage) {
		System.out.println(htmlPage.getUrl() + "--");
		Map<String, Integer> treeMap = new TreeMap<String, Integer>(htmlPage.getElementsMap());
		treeMap.remove("COMMENT");
		treeMap.remove("P-IMPLIED");
		int count = 0;
		for (String element : treeMap.keySet()) {
			count = htmlPage.getElementsMap().get(element);
			System.out.println(element + ": " + count);
			addElementCountGlobally(element, count);
		}
		System.out.println("Total count of elements:" + treeMap.size() + "\n");
	}

	private static void addElementCountGlobally(String element, Integer count) {
		Integer elementcount = (Integer) globalHtmlElementMap.get(element);
		if (elementcount != null) {
			globalHtmlElementMap.put(element, elementcount + count);
		} else {
			globalHtmlElementMap.put(element, count);
		}
	}
}

public class URLReader {

	static int pageSize = 10;
	static int pageLength = 3;

	/**
	 * Computes pages to visit in tree structure
	 * 
	 * @param pageVisited
	 * @param currentPath
	 * @param hyperlinkToVisit
	 * @throws IOException
	 * @throws BadLocationException
	 */
	/*
	 * private static void commuteHtmlPages(int pageVisited, int currentPath,
	 * Queue<String> hyperlinkToVisit) throws IOException, BadLocationException
	 * { Queue<String> hyperlinksQueue = new LinkedList<>(); while
	 * (hyperlinkToVisit.size() > 0) { if (pageVisited < pageSize && currentPath
	 * < pageLength) { String visitUrl = hyperlinkToVisit.poll(); HtmlPage
	 * currentPage = parseHtml(visitUrl); // displayHtmlDetails(currentPage);
	 * htmlsVisited.add(currentPage); pageVisited++;
	 * hyperlinksQueue.addAll(currentPage.getHyperLinks()); } else { return; } }
	 * pageLength++; commuteHtmlPages(pageVisited, currentPath,
	 * hyperlinksQueue); }
	 */

	/**
	 * Globally counts and displays it for each element.
	 */
	/*
	 * private static void countElementsInAllPages() { System.out.println(
	 * "Global counts of elements"); Map<String, Integer> globalCount = new
	 * HashMap<>(); for (HtmlPage htmlPage : htmlsVisited) { for (String element
	 * : htmlPage.getElementsMap().keySet()) { Integer elementCount =
	 * globalCount.get(element); if (elementCount == null) {
	 * globalCount.put(element, htmlPage.getElementsMap().get(element)); } else
	 * { globalCount.put(element, elementCount +
	 * htmlPage.getElementsMap().get(element)); } } } Map<String, Integer>
	 * treeMap = new TreeMap<String, Integer>(globalCount);
	 * treeMap.remove("COMMENT"); treeMap.remove("P-IMPLIED"); for (String
	 * element : treeMap.keySet()) { System.out.println(element + ": " +
	 * globalCount.get(element)); } System.out.println("\n"); }
	 */

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("Insufficient Argument");
		} else {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("-pages"))
					pageSize = Integer.parseInt(args[i + 1]);
				if (args[i].equalsIgnoreCase("-path"))
					pageLength = Integer.parseInt(args[i + 1]);
			}
			/*
			 * Queue<String> hyperlinksQueue = new LinkedList<>();
			 * hyperlinksQueue.add(args[args.length - 1]); commuteHtmlPages(0,
			 * 0, hyperlinksQueue); countElementsInAllPages();
			 */
			MyThread myThread = new MyThread();
			myThread.url = args[args.length - 1];
			myThread.currentLength = 0;
			MyThread.pageLength = pageLength;
			MyThread.pageSize = pageSize;
			MyThread.currentPage = 0;
			Thread st = new Thread(myThread);
			st.start();
			while (Thread.activeCount() != 1) {

			}
			MyThread.displayGlobalCount();
		}

	}
}