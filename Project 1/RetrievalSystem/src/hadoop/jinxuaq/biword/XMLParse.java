package hadoop.jinxuaq.biword;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XMLParse {
	public String getText(String XMLstring) throws JDOMException {

		String textTotal = "";
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		Reader in = new StringReader(XMLstring);

		try {
			doc = builder.build(in);
			Element root = doc.getRootElement();
			if (root == null) {
				System.out.println("@@Wrong: No root when parsing XML.");
				return textTotal;
			}

			Element newsItem = root.getChild("newsItem");
			if (newsItem != null) {
				Element itemId = newsItem.getChild("itemid");
				if (itemId != null)
					textTotal = itemId.getValue();
			}

			Element title = root.getChild("title");
			if (title != null)
				textTotal += title.getValue();

			Element text = root.getChild("text");
			if (text != null)
				textTotal += text.getValue();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return textTotal;
	}
}
