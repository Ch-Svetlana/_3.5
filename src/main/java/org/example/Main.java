package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws TransformerException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        File file = new File("data.csv");
        newFile(file);
        String text = "1,John,Smith,USA,25\n" +
                "2,Inav,Petrov,RU,23";

        String fileName = "data.csv";
        writeString(text, fileName);
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        File fileJson = new File("target.json");
        newFile(fileJson);
        String jsonName = "target.json";
        writeString(json, jsonName);
        //2
        List<Employee> listSecond = parseXML();
        String secondJson = "target2.json";
        writeString(listToJson(listSecond), secondJson);
        //3
        String inJson = readString("target.json");
        List<Employee> list3 = jsonToList(inJson);
        for (int a = 0; a < list3.size(); a++) {
            System.out.println(list3.get(a));
        }


    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();

            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return staff;
    }


    public static void writeString(String text, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write(text);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void newFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    //2

    public static List<Employee> parseXML() throws TransformerException {
        List<Employee> list = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            writeXml(doc);
            Node node = doc.getDocumentElement();
            list = read(node);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Employee> read(Node node) {
        List<Employee> employees = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType() && node_.getNodeName().equals("employee")) {
                Element element = (Element) node_;
                String firstName = element.getChildNodes().item(1).getTextContent();
                String lastName = element.getChildNodes().item(2).getTextContent();
                String country = element.getChildNodes().item(3).getTextContent();
                long id = Long.parseLong(element.getChildNodes().item(0).getTextContent());
                int age = Integer.parseInt(element.getChildNodes().item(4).getTextContent());
                Employee e = new Employee(id, firstName, lastName, country, age);
                employees.add(e);
            }
        }
        return employees;

    }

    public static void writeXml(Document doc) throws TransformerException {
        Element staff = doc.createElement("staff");
        doc.appendChild(staff);
        Element employee = doc.createElement("employee");
        staff.appendChild(employee);
        Element id = doc.createElement("id");
        id.appendChild(doc.createTextNode("1"));
        employee.appendChild(id);
        Element firstName = doc.createElement("firstName");
        firstName.appendChild(doc.createTextNode("John"));
        employee.appendChild(firstName);
        Element lastName = doc.createElement("lastName");
        lastName.appendChild(doc.createTextNode("Smith"));
        employee.appendChild(lastName);
        Element country = doc.createElement("country");
        country.appendChild(doc.createTextNode("USA"));
        employee.appendChild(country);
        Element age = doc.createElement("age");
        age.appendChild(doc.createTextNode("25"));
        employee.appendChild(age);

        Element employeeTwo = doc.createElement("employee");
        staff.appendChild(employeeTwo);
        Element idTwo = doc.createElement("id");
        idTwo.appendChild(doc.createTextNode("2"));
        employeeTwo.appendChild(idTwo);
        Element firstNameTwo = doc.createElement("firstName");
        firstNameTwo.appendChild(doc.createTextNode("Inav"));
        employeeTwo.appendChild(firstNameTwo);
        Element lastNameTwo = doc.createElement("lastName");
        lastNameTwo.appendChild(doc.createTextNode("Petrov"));
        employeeTwo.appendChild(lastNameTwo);
        Element countryTwo = doc.createElement("country");
        countryTwo.appendChild(doc.createTextNode("RU"));
        employeeTwo.appendChild(countryTwo);
        Element ageTwo = doc.createElement("age");
        ageTwo.appendChild(doc.createTextNode("23"));
        employeeTwo.appendChild(ageTwo);


        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File("data.xml"));

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);

    }

    //3

    public static String readString(String nameOfFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(nameOfFile))) {
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Employee> jsonToList(String name) {
        JSONParser jsonParser = new JSONParser();
        List<Employee> list = new ArrayList<>();
        try {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(name);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            for (Object o : jsonArray) {
                Employee employee = gson.fromJson(String.valueOf(o), Employee.class);
                list.add(employee);
            }
            return list;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


}