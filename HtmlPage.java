package demo;

import java.util.Map;
import java.util.Set;

public class HtmlPage {

	private String url;
	private Map<String, Integer> elementsMap;
	private Set<String> hyperLinks;
	private int pageLength;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, Integer> getElementsMap() {
		return elementsMap;
	}

	public void setElementsMap(Map<String, Integer> elementsMap) {
		this.elementsMap = elementsMap;
	}

	public Set<String> getHyperLinks() {
		return hyperLinks;
	}

	public void setHyperLinks(Set<String> hyperLinks) {
		this.hyperLinks = hyperLinks;
	}

	public int getPageLength() {
		return pageLength;
	}

	public void setPageLength(int pageLength) {
		this.pageLength = pageLength;
	}
}
