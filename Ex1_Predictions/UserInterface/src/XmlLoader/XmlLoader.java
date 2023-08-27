package XmlLoader;

import XmlLoader.schema.PRDWorld;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class XmlLoader {
    private String xmlFilePath;
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "XmlLoader.schema";
    public XmlLoader(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }
    public PRDWorld loadXmlData() {
        try {
            String fullPath = new File(xmlFilePath).getAbsolutePath();
            //System.out.println(fullPath);
            InputStream inputStream = new FileInputStream(new File(fullPath));
            PRDWorld world = deserializeFrom(inputStream);
            // System.out.println("entity" +world.getPRDEntities());
            return world;


        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PRDWorld deserializeFrom(InputStream in) throws JAXBException{
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (PRDWorld) u.unmarshal(in);
    }
}
