import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {
    public static void main(String[] args) {
        try {
           
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document studentDoc = docBuilder.parse(new File("student.xml"));
            studentDoc.getDocumentElement().normalize();

            NodeList studentList = studentDoc.getElementsByTagName("student");

            
            Thread2 thread2 = new Thread2();
            Thread3 thread3 = new Thread3();

            thread2.start();
            thread3.start();

            for (int i = 0; i < studentList.getLength(); i++) {
                Node studentNode = studentList.item(i);

                if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) studentNode;
                    String dateOfBirth = studentElement.getElementsByTagName("dateOfBirth").item(0).getTextContent();

                    
                    thread2.processDateOfBirth(dateOfBirth);
                    thread3.processDateOfBirth(dateOfBirth);
                }
            }

            
            thread2.join();
            thread3.join();

            
            String age = thread2.getEncodedAge();
            String sum = thread3.getSum();
            boolean isPrime = thread3.isPrime();

            createResultFile(age, sum, isPrime);
        } catch (InterruptedException | IOException | ParserConfigurationException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private static void createResultFile(String age, String sum, boolean isPrime)
            throws IOException, ParserConfigurationException, TransformerException {
        
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        
        Element studentElement = doc.createElement("Student");
        doc.appendChild(studentElement);

       
        Element ageElement = doc.createElement("age");
        ageElement.setTextContent(age);
        studentElement.appendChild(ageElement);

        
        Element sumElement = doc.createElement("sum");
        sumElement.setTextContent(sum);
        studentElement.appendChild(sumElement);

        Element isDigitElement = doc.createElement("isDigit");
        isDigitElement.setTextContent(String.valueOf(isPrime));
        studentElement.appendChild(isDigitElement);

        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("kq.xml"));

        transformer.transform(source, result);

        System.out.println("Kết quả đã được lưu vào kq.xml thành công.");
    }

    static class Thread2 extends Thread {
        private static String dateOfBirth;
        private static String encodedAge;

        public static void processDateOfBirth(String dob) {
            dateOfBirth = dob;
        }

        public static String getEncodedAge() {
            return encodedAge;
        }

        @Override
        public void run() {
            
            int age = calculateAge(dateOfBirth);
            encodedAge = encodeAge(age);
        }

        private int calculateAge(String dob) {
            
            String[] parts = dob.split("/");
            int dayOfBirth = Integer.parseInt(parts[0]);
            int monthOfBirth = Integer.parseInt(parts[1]);
            int yearOfBirth = Integer.parseInt(parts[2]);

            
            java.util.Date currentDate = new java.util.Date();
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(currentDate);
            int currentYear = calendar.get(java.util.Calendar.YEAR);
            int currentMonth = calendar.get(java.util.Calendar.MONTH) + 1;
            int currentDay = calendar.get(java.util.Calendar.DAY_OF_MONTH);

            
            int age = currentYear - yearOfBirth;
            if (currentMonth < monthOfBirth || (currentMonth == monthOfBirth && currentDay < dayOfBirth)) {
                age--; 
            }
            return age;
        }

        private String encodeAge(int age) {
            
            return new StringBuilder(String.valueOf(age)).reverse().toString();
        }
    }

    static class Thread3 extends Thread {
        private static String dateOfBirth;
        private static String sum;
        private static boolean isPrime;

        public static void processDateOfBirth(String dob) {
            dateOfBirth = dob;
        }

        public static String getSum() {
            return sum;
        }

        public static boolean isPrime() {
            return isPrime;
        }

        @Override
        public void run() {
           
            int sum = calculateSumOfDigits(dateOfBirth);
            Thread3.sum = String.valueOf(sum);

            
            isPrime = checkIfPrime(sum);
        }

        private int calculateSumOfDigits(String dob) {
           
            String[] parts = dob.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            int sum = sumOfDigits(day) + sumOfDigits(month) + sumOfDigits(year);
            return sum;
        }

        private int sumOfDigits(int number) {
            
            int sum = 0;
            while (number > 0) {
                sum += number % 10;
                number /= 10;
            }
            return sum;
        }

        private boolean checkIfPrime(int number) {
           
            if (number <= 1) {
                return false;
            }
            for (int i = 2; i <= Math.sqrt(number); i++) {
                if (number % i == 0) {
                    return false;
                }
            }
            return true;
        }
    }}