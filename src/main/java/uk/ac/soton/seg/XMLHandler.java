package uk.ac.soton.seg;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.ac.soton.seg.event.ExportXML;
import uk.ac.soton.seg.event.ImportXML;
import uk.ac.soton.seg.model.Airport;
import uk.ac.soton.seg.model.Obstacle;
import uk.ac.soton.seg.model.Runway;
import uk.ac.soton.seg.util.LogManager;
import uk.ac.soton.seg.util.Logger;

public class XMLHandler implements ImportXML, ExportXML {
    private static Logger log = LogManager.getLog(XMLHandler.class.getName());
    private final String airtag = "airport";
    private final String obstag = "obstacle";

    public XMLHandler() {
    }

    public static void saveObjectAsXML(Object o, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file);
                XMLEncoder enc = new XMLEncoder(fos);) {
            enc.setExceptionListener(e -> e.printStackTrace());
            enc.writeObject(o);
        }
    }

    public static void saveObjectAsXML(Object o, String filename) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            saveObjectAsXML(o, fos);
        }
    }

    public static void saveObjectAsXML(Object o, FileOutputStream fos) throws IOException {
        try (XMLEncoder enc = new XMLEncoder(fos)) {
            enc.setExceptionListener(e -> e.printStackTrace());
            enc.writeObject(o);
        }
    }

    protected static Object loadObject(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename)) {
            return loadObject(fis);
        }
    }

    protected static Object loadObject(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        return loadObject(fis);
    }

        protected static Object loadObject(InputStream fis) throws IOException {
            try (XMLDecoder xmlDec = new XMLDecoder(fis)) {
                Object decoded = xmlDec.readObject();
                return decoded;
            }
    }

    public static List<Obstacle> loadObstacles(InputStream in) throws IOException {
        return (ArrayList<Obstacle>) XMLHandler.loadObject(in);
    }

    public static Airport loadAirport(InputStream in) throws IOException {
        return (Airport) XMLHandler.loadObject(in);
    }

    public static ArrayList<Airport> loadAirports(InputStream in) throws IOException {
        if (in == null) return null;
        return (ArrayList<Airport>) XMLHandler.loadObject(in);
    }

    public static void saveAirport(Airport airport) {
            var x = new XMLHandler();
            x.exportAirport(airport, new File("data/xml_new/" + airport.getPrettyName().replace("/", "") + ".xml"));
    }

    /**
     * Import Airport data from html files downloaded from NATS
     * (https://nats-uk.ead-it.com/cms-nats/opencms/en/Publications/AIP/).
     * looks for .html files stored in ./data/html/.
     * 
     * @return All the airports, one per file
     * @throws IOException
     */

    /*
    public static ArrayList<Airport> importData() throws IOException {
        Function<org.jsoup.nodes.Element, String> getStr = (element) -> {
            var spans = element.getElementsByTag("span").not(".sdParams");
            return spans.text();
            // return (spans.first() != null) ? spans.first().text() : spans.text();
        };
        Function<String, Integer> safeParseInt = (string) -> {
            try {
                return Integer.parseInt(string.replace(" ", "").replace("M", ""));
            } catch (Exception e) {
            }
            return 0;
        };
        Function<String, Double> safeParseDouble = (string) -> {
            try {
                return Double.parseDouble(string.replace("°", ""));
            } catch (Exception e) {
            }
            return 0d;
        };
        Function<org.jsoup.nodes.Element, Integer> getStrSafeParseInt = safeParseInt.compose(getStr);
        Function<org.jsoup.nodes.Element, Double> getStrSafeParseDouble = safeParseDouble.compose(getStr);
        var airports = new ArrayList<Airport>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("./data/html/"))) {
            for (Path path : stream) {
                if (Files.isRegularFile(path) && path.toString().endsWith(".html")) {
                    File input = path.toFile();
                    org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(input, "UTF-8");

                    try {
                        // airport name and ICAO code
                        String[] splitted = doc.getElementsByTag("h3").first().text().split(" — ");
                        var airport = new Airport(splitted[0], splitted[1]);
                        airports.add(airport);

                        // runway characteristsics
                        var rwTable = doc.getElementsContainingOwnText("RUNWAY PHYSICAL CHARACTERISTICS").first()
                                .nextElementSibling();
                        var rwTable2 = rwTable.nextElementSibling();

                        var rwRows = rwTable.getElementsByTag("tbody").first().getElementsByTag("tr");
                        var rwRows2 = rwTable2.getElementsByTag("tbody").first().getElementsByTag("tr");

                        var rwRowsIt = rwRows.iterator();
                        var rwRowsIt2 = rwRows2.iterator();

                        Map<String, RunwayData> m = new HashMap<String, RunwayData>();

                        while (rwRowsIt.hasNext()) {
                            var row = rwRowsIt.next();
                            var row2 = rwRowsIt2.next();
                            var vals = row.getElementsByTag("td");
                            var vals2 = row2.getElementsByTag("td");
                            var runwayData = new RunwayData(
                                    getStr.apply(vals.get(0)),
                                    getStrSafeParseDouble.apply(vals.get(1)),
                                    getStr.apply(vals.get(2)),
                                    getStr.apply(vals.get(3)),
                                    getStr.apply(vals.get(4)),
                                    getStr.apply(vals.get(5)),
                                    getStr.apply(vals.get(6)),
                                    getStr.apply(vals2.get(0)),
                                    getStr.apply(vals2.get(1)),
                                    getStr.apply(vals2.get(2)),
                                    getStr.apply(vals2.get(3)),
                                    getStr.apply(vals2.get(4)),
                                    getStr.apply(vals2.get(5)),
                                    getStr.apply(vals2.get(6)));
                            m.put(runwayData.designator, runwayData);
                        }

                        // declared distances
                        var table = doc.getElementsContainingOwnText("DECLARED DISTANCES").first().nextElementSibling();
                        var rows = table.getElementsByTag("tbody").first().getElementsByTag("tr");
                        for (var row : rows) {
                            var vals = row.getElementsByTag("td");
                            var runway = new Runway(
                                    getStr.apply(vals.get(0)),
                                    getStrSafeParseInt.apply(vals.get(1)),
                                    getStrSafeParseInt.apply(vals.get(2)),
                                    getStrSafeParseInt.apply(vals.get(3)),
                                    getStrSafeParseInt.apply(vals.get(4)));

                            runway.setRemarks(getStr.apply(vals.get(5)));
                            runway.setBearing(m.get(runway.getDesignator()).trueBearing);
                            airport.addRunway(runway);
                        }
                    } catch (NullPointerException e) {
                        log.error("error in: " + path);
                        e.printStackTrace();
                    }
                }
            }
        }
        return airports;
    }*/
    

    /**
     * @param f File reference to the XML File
     * @return the imported Airport object, or null if the import failed
     */
    @Override
    public Airport importAirport (File f) {
        Airport air = null;
        try {
            DocumentBuilderFactory docBuilderFactory =  DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(f);
            doc.getDocumentElement().normalize();
            NodeList airportList = doc.getElementsByTagName(airtag);
            if (airportList.getLength() != 1 || airportList==null)
                throw new IOException();
            if (airportList.item(0).getNodeType() == Node.ELEMENT_NODE) {
                Element elementAir = (Element) airportList.item(0);
                String identifier = elementAir.getElementsByTagName("identifier").item(0).getTextContent();

                air = new Airport(identifier, elementAir.getAttribute("name"));

            }

            NodeList runwayList = doc.getElementsByTagName("runway");
            for (int i = 0; i < runwayList.getLength(); i++){
                Element runway = (Element) runwayList.item(i);
                String designator = runway.getElementsByTagName("designator").item(0).getTextContent();
                Double bearing = Double.valueOf(runway.getElementsByTagName("bearing").item(0).getTextContent());
                int tora = Integer.parseInt(runway.getElementsByTagName("TORA").item(0).getTextContent());
                int toda = Integer.parseInt(runway.getElementsByTagName("TODA").item(0).getTextContent());
                int asda = Integer.parseInt(runway.getElementsByTagName("ASDA").item(0).getTextContent());
                int lda = Integer.parseInt(runway.getElementsByTagName("LDA").item(0).getTextContent());
                String remarks = runway.getElementsByTagName("remarks").item(0).getTextContent();

                var run = new Runway(designator, tora, toda, asda, lda);
                run.setBearing(bearing);
                run.setRemarks(remarks);

                air.addRunway(run);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("error in importing of airports");
            //e.printStackTrace();

        }
        return air;
    }

    /**
     * @param f File reference to the XML File
     * @return the imported Airport object, or null if the import failed
     */
    // @Override
    public Airport importAirport (InputStream f) {

        Airport air = null;

        try {
            DocumentBuilderFactory docBuilderFactory =  DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(f);
            doc.getDocumentElement().normalize();
            NodeList airportList = doc.getElementsByTagName(airtag);
            if (airportList.getLength() != 1 || airportList==null)
                throw new IOException();
            if (airportList.item(0).getNodeType() == Node.ELEMENT_NODE) {
                Element elementAir = (Element) airportList.item(0);
                String identifier = elementAir.getElementsByTagName("identifier").item(0).getTextContent();

                air = new Airport(identifier, elementAir.getAttribute("name"));

            }

            NodeList runwayList = doc.getElementsByTagName("runway");
            for (int i = 0; i < runwayList.getLength(); i++){
                Element runway = (Element) runwayList.item(i);
                String designator = runway.getElementsByTagName("designator").item(0).getTextContent();
                Double bearing = Double.valueOf(runway.getElementsByTagName("bearing").item(0).getTextContent());
                int tora = Integer.parseInt(runway.getElementsByTagName("TORA").item(0).getTextContent());
                int toda = Integer.parseInt(runway.getElementsByTagName("TODA").item(0).getTextContent());
                int asda = Integer.parseInt(runway.getElementsByTagName("ASDA").item(0).getTextContent());
                int lda = Integer.parseInt(runway.getElementsByTagName("LDA").item(0).getTextContent());
                String remarks = runway.getElementsByTagName("remarks").item(0).getTextContent();

                if (runway != null) {
                    var run = new Runway(designator, tora, toda, asda, lda);
                    run.setBearing(bearing);
                    run.setRemarks(remarks);

                    air.addRunway(run);
                } else {
                    throw new NullPointerException();
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            //System.out.println("error in importing of airports");
            e.printStackTrace();

        }
        return air;
    }

    @Override
    public boolean exportAirport (Airport air, File f){
        try {
            DocumentBuilderFactory docBuilderFactory =  DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElem = doc.createElement(airtag);
            doc.appendChild(rootElem);

            Attr name = doc.createAttribute("name");
            name.setValue(air.getName());
            rootElem.setAttributeNode(name);

            Element identifier = doc.createElement("identifier");
            identifier.appendChild(doc.createTextNode(air.getIdentifier()));
            rootElem.appendChild(identifier);

            Element runways = doc.createElement("runways");
            rootElem.appendChild(runways);

            for (Runway runList : air.getRunways()) {
                Element runway = doc.createElement("runway");
                runways.appendChild(runway);

                Element designator = doc.createElement("designator");
                designator.appendChild(doc.createTextNode(runList.getRunway().getDesignator()));
                runway.appendChild(designator);

                Element bearing = doc.createElement("bearing");
                bearing.appendChild(doc.createTextNode(String.valueOf(runList.getRunway().getBearing())));
                runway.appendChild(bearing);

                Element tora = doc.createElement("TORA");
                tora.appendChild(doc.createTextNode(String.valueOf(runList.getRunway().getTora())));
                runway.appendChild(tora);

                Element toda = doc.createElement("TODA");
                toda.appendChild(doc.createTextNode(String.valueOf(runList.getRunway().getToda())));
                runway.appendChild(toda);

                Element asda = doc.createElement("ASDA");
                asda.appendChild(doc.createTextNode(String.valueOf(runList.getRunway().getAsda())));
                runway.appendChild(asda);

                Element lda = doc.createElement("LDA");
                lda.appendChild(doc.createTextNode(String.valueOf(runList.getRunway().getLda())));
                runway.appendChild(lda);

                Element remarks = doc.createElement("remarks");
                remarks.appendChild(doc.createTextNode(runList.getRunway().getRemarks()));
                runway.appendChild(remarks);
            }

            DOMSource src = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(f);

            try{
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.transform(src, streamResult);
                return true;

            } catch (TransformerException e) {
                e.printStackTrace();
                return false;
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Obstacle> importObstacle (File f){

        List<Obstacle> obstacles = new ArrayList<Obstacle>();

        try {
            DocumentBuilderFactory docBuilderFactory =  DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(f);
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName(obstag);

            for (int i = 0; i < nodes.getLength(); i++){
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE){
                    Element elem = (Element) node;
                    String name = elem.getElementsByTagName("name").item(0).getTextContent();
                    int height = Integer.parseInt(elem.getElementsByTagName("height").item(0).getTextContent());
                    int width = Integer.parseInt(elem.getElementsByTagName("width").item(0).getTextContent());
                    int length = Integer.parseInt(elem.getElementsByTagName("length").item(0).getTextContent());

                    obstacles.add(new Obstacle(name, height, width, length));
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return obstacles;
    }

    @Override
    public boolean exportObstacle(Obstacle obs, File f) {
        try {
            DocumentBuilderFactory docBuilderFactory =  DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element root = doc.createElement("obstacles");
            doc.appendChild(root);

            Element obstacle = doc.createElement(obstag);
            root.appendChild(obstacle);

            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode(obs.getName()));
            obstacle.appendChild(name);

            Element height = doc.createElement("height");
            height.appendChild(doc.createTextNode(String.valueOf(obs.getHeight())));
            obstacle.appendChild(height);

            Element width = doc.createElement("width");
            width.appendChild(doc.createTextNode(String.valueOf(obs.getWidth())));
            obstacle.appendChild(width);

            Element length = doc.createElement("length");
            length.appendChild(doc.createTextNode(String.valueOf(obs.getLength())));
            obstacle.appendChild(length);

            DOMSource src = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(f);

            try{
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.transform(src, streamResult);
                return true;

            } catch (TransformerException e) {
                e.printStackTrace();
                return false;
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        }

    }

}
