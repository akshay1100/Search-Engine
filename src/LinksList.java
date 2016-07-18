import java.util.LinkedList;

public final class LinksList {

	/**
	 * Max number of links stored in this list.
	 */
	private static final int LIMIT_NUMBER_LINKS = 100000;

	/**
	 * Holds the unique instance of this class.
	 */
	private static LinksList linksList;

	/**
	 * Internal list of links controlled by the implemented mutex.
	 */
	private LinkedList<String> links;
	
	/**
	 * Amount of links stored in this list. 
	 */
	private int numberOfLinks = 0;

	/**
	 * Protect the constructor to avoid new instances.
	 */
	protected LinksList() {
		links = new LinkedList<String>();
	}

	/**
	 * Returns the unique instance of this class.
	 * 
	 * @return The singleton instance.
	 */
	public static LinksList getInstance() {
		if (linksList == null) {
			linksList = new LinksList();
		}
		return linksList;
	}

	/**
	 * Pop a link from the list.
	 * 
	 * @return a link.
	 */
	public synchronized String get() {
		while (numberOfLinks <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		numberOfLinks--;
		notifyAll();
		return links.pop();
	}

	/**
	 * Push a link to the list.
	 * 
	 * @param link
	 */
	public synchronized void put(String link) {
		while (numberOfLinks >= LIMIT_NUMBER_LINKS) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		links.push(link);
		numberOfLinks++;
		notifyAll();
	}
}