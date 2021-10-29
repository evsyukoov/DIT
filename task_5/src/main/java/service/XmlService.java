package service;

import entity.Employee;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class XmlService {

    Document document;

    File workingFile;

    XPath xPath;

    public XmlService() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        Path resourceDirectory = Paths.get("src", "main", "resources");
        workingFile = new File(resourceDirectory + File.separator + "input.xml");
        document = builderFactory.newDocumentBuilder().parse(workingFile);
        XPathFactory xpathFactory = XPathFactory.newInstance();
        xPath = xpathFactory.newXPath();
        document.getDocumentElement().normalize();
    }

    private Employee createEmployeeFromXml(Element eElement, boolean allFields) {
        return allFields ? new Employee(Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent()),
                eElement.getElementsByTagName("name").item(0).getTextContent(),
                eElement.getElementsByTagName("surname").item(0).getTextContent(),
                eElement.getElementsByTagName("position").item(0).getTextContent(),
                new BigDecimal(eElement.getElementsByTagName("salary").item(0).getTextContent()))
                :
                new Employee(Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent()),
                            eElement.getElementsByTagName("name").item(0).getTextContent(),
                            eElement.getElementsByTagName("surname").item(0).getTextContent());
    }

    public NodeList initNodeList() {
        document.getDocumentElement().normalize();
        return document.getElementsByTagName("employee");
    }

    /**
     * Поиск сотрудника по фамилии
     * @param surname
     */
    public List<Employee> getEmployeesByName(String surname) {
        List<Employee> employees = new ArrayList<>();
        NodeList nList = initNodeList();
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String emplSurname = eElement.getElementsByTagName("surname").item(0).getTextContent();
                if (emplSurname.equals(surname)) {
                    employees.add(createEmployeeFromXml(eElement, true));
                }
            }
        }
        return employees;
    }

    /**
     * Поиск сотрудника по id
     * @param id
     */
    public Employee getEmployeeById(Integer id) {
        NodeList nList = initNodeList();
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                int currentId = Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent());
                if (id == currentId) {
                    return createEmployeeFromXml(eElement, true);
                }
            }
        }
        return null;
    }

    /**
     * Поиск всех сотрудников, отадем только id, имя и фамилию
     */
    public List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<>();
        NodeList nList = initNodeList();
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                employees.add(createEmployeeFromXml(eElement, false));
            }
        }
        return employees;
    }

    /**
     * редактировать зарплату
     * @param id - получить сотрудника по id
     * @param newSalary - новая зарплата
     * @throws XPathExpressionException
     * @throws TransformerException
     */
    public void editSalary(Integer id, BigDecimal newSalary) throws XPathExpressionException, TransformerException {
        document.getDocumentElement().normalize();
        XPathExpression xPathExpression = xPath.compile(
                "/office/employee[id='" + id +"']/salary/text()");
        NodeList nodeList = (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);
        Node node = nodeList.item(0);
        node.setNodeValue(newSalary.toString());

        deleteLineBreaks(document.getFirstChild());
        refreshFile();
    }

    public int addNewEmployee(String name, String surname, String position, BigDecimal salary) throws XPathExpressionException, TransformerException {
        document.getDocumentElement().normalize();
        int newId = generateId();
        NodeList nodes = document.getElementsByTagName("office");
        Element employee = document.createElement("employee");

        Element idEl = document.createElement("id");
        idEl.appendChild(document.createTextNode(String.valueOf(newId)));
        employee.appendChild(idEl);

        Element nameEl = document.createElement("name");
        nameEl.appendChild(document.createTextNode(name));
        employee.appendChild(nameEl);

        Element surnameEl = document.createElement("surname");
        surnameEl.appendChild(document.createTextNode(surname));
        employee.appendChild(surnameEl);

        Element positionEl = document.createElement("position");
        positionEl.appendChild(document.createTextNode(position));
        employee.appendChild(positionEl);

        Element salaryEl = document.createElement("salary");
        salaryEl.appendChild(document.createTextNode(String.valueOf(salary)));
        employee.appendChild(salaryEl);

        nodes.item(0).appendChild(employee);
        deleteLineBreaks(document.getFirstChild());
        refreshFile();
        return newId;
    }

    /**
     * Рекурсивно проходим по всем нодам от корня и удаляем пустые строки и переносы,
     * Чтобы при добавлении новой ноды не увеличивать их число
     * @param root
     */
    private void deleteLineBreaks(Node root) {
        NodeList children = root.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(child.getNodeType() == Node.TEXT_NODE) {
                child.setTextContent(child.getTextContent().trim());
                if (child.getTextContent().isEmpty()) {
                    child.getParentNode().removeChild(child);
                    i--;
                }
            }
            deleteLineBreaks(child);
        }
    }

    private void refreshFile() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(workingFile);
        transformer.transform(source, result);
    }


    /**
     * Генерируем уникальный Id
     * @return
     * @throws XPathExpressionException
     */
    private int generateId() throws XPathExpressionException {
        XPathExpression xPathExpression = xPath.compile(
                "/office/employee/id/text()");
        NodeList nodeList = (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < nodeList.getLength(); i++) {
            int currId;
            if ((currId = Integer.parseInt(nodeList.item(i).getTextContent())) > max) {
                max = currId;
            }
        }
        return max + 1;
    }

}
