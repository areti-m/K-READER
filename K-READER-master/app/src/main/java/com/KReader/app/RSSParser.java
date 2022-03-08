package com.KReader.app;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class RSSParser {

    // RSS XML document CHANNEL tag
    private static String TAG_CHANNEL = "channel";
    private static String TAG_TITLE = "title";
    private static String TAG_LINK = "link";
    private static String TAG_DESRIPTION = "description";
    private static String TAG_ITEM = "item";
    private static String TAG_PUB_DATE = "pubDate";
    private static String TAG_GUID = "guid";

    public RSSParser() {
    }

    public List<Article> getRSSFeedItems(String rss_feed_xml) {
        List<Article> itemsList = new ArrayList<Article>();
        if (rss_feed_xml != null) {
            try {
                Document doc = this.getDomElement(rss_feed_xml.toString());
                if (doc == null) return itemsList;
              //  doc= getModifyDocument(doc);

               System.out.println(doc.toString());
//               System.out.println( getStringFromDocument(doc));


                NodeList nodeList = doc.getElementsByTagName(TAG_CHANNEL);
                Element e = (Element) nodeList.item(0);

                NodeList items = e.getElementsByTagName(TAG_ITEM);
                for (int i = 0; i < items.getLength(); i++) {
                    Element e1 = (Element) items.item(i);


                    String title = this.getValue(e1, TAG_TITLE);
                    String link = this.getValue(e1, TAG_LINK);
                    String description = this.getValue(e1, TAG_DESRIPTION);
                    String pubdate = this.getValue(e1, TAG_PUB_DATE);
                    String guid = this.getValue(e1, TAG_GUID);
                   // String imageUrl = this.getValue(e1, TAG_GUID);


                    Article rssItem = new Article(title, link, description, pubdate, guid,0,"");
                    // adding item to list
                    itemsList.add(rssItem);
                }
            } catch (Exception e) {
                // Check log for errors
                e.printStackTrace();
            }
        }
        return itemsList;
    }

    public Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
//            InputSource is = new InputSource();
//            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(new ByteArrayInputStream(xml.getBytes()));
        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        return doc;
    }

    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE || (child.getNodeType() == Node.CDATA_SECTION_NODE)) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public Document getModifyDocument(Document data) throws ParserConfigurationException, IOException, SAXException {
        String modifyData= getStringFromDocument(data);
        return  stringToDocument(modifyData);
    }



    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }

    //method to convert Document to String
    public String getStringFromDocument(Document doc)
    {
        try
        {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            String d_data= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            String stringObj= writer.toString();

            if(stringObj.contains(d_data)){
                return  stringObj;
            }else {
                return d_data+stringObj;
            }

        }
        catch(TransformerException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public Document stringToDocument(String data) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(data));

        Document doc = db.parse(is);
        return  doc;
    }


}