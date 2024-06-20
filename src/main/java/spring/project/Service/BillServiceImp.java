package spring.project.Service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import spring.project.JWT.JwtFilter;
import spring.project.Models.Bill;
import spring.project.Repository.BillRepository;
import spring.project.Utils.CafeUtils;
import spring.project.constants.Cafeconstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class BillServiceImp implements BillService{

    @Autowired
    BillRepository billRepository;

    @Autowired
    JwtFilter jwtFilter;

    private boolean validateResquestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("totalAmount");
    }

    private void insertBill(Map<String, Object> requestMap) {
        try {
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUsername());
            billRepository.save(bill);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setRectaangleInPdf(Document document) throws DocumentException {
        log.info("Inside setRectaangleInPdf.");
        Rectangle rectangle = new Rectangle(577, 825, 18, 15);
//        (577, 825) = coordinates of upperright corner
//        (18, 15) = coordinates of lowerleft corner
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
//        1, 2, 4, 8 are the enabling the bottom, left, top, right sides
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
//        setting border color and width
        document.add(rectangle);
    }

    private Font getFont(String type) {
        log.info("Inside getFont");
        switch (type) {
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dareFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dareFont.setStyle(Font.BOLD);
                return dareFont;
            default:
                return new Font();
        }
    }

    private void addTableHeader(PdfPTable table) {
        log.info("Inside addTableHeader");
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBorderColor(BaseColor.BLACK);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(BaseColor.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, Map<String, Object> data) {
        log.info("Inside addRows");
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Insert generateReport");
        try {
            String filename;
            if (validateResquestMap(requestMap)) {

                if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
                    filename = (String) requestMap.get("uuid");
                } else {
                    filename = CafeUtils.getUUID();    //generate a filename using currnt time
                    requestMap.put("uuid", filename);   //add this filename into the requestmap
                    insertBill(requestMap);  //save the bill in the database.
                }

                //generate a new empty document
                Document document = new Document();
                //later when we write anything inside the doccument, pdfwriter will automatically write this inside the path specified pdf
                PdfWriter.getInstance(document, new FileOutputStream(Cafeconstants.STORE_LOCATION + "\\" + filename + ".pdf"));

                //opening the document
                document.open();

                //setting the rectangular boarder for the pdf
                setRectaangleInPdf(document);

                //create pdf header by applying Header font and align this to centre and add it to the document.
                Paragraph chunk = new Paragraph("Cafe Management System", getFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);

                //defining the data to be written
                String data = "Name: " + requestMap.get("name") + "\n" + "Contact Number: " + requestMap.get("contactNumber") +
                        "\n" + "Email: " + requestMap.get("email") + "\n" + "Payment Method: " + requestMap.get("paymentMethod");
                //create pdf data by applying Data font and add it to the document.
                Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data"));
                document.add(paragraph);

                // Create table with 5 columns
                PdfPTable table = new PdfPTable(5);
                //The method setWidthPercentage is used to define how much of the available width in the document should be occupied by the table.
                table.setWidthPercentage(100);

                //adding table header in the table
                addTableHeader(table);

                //convert value of ProductDetails key which is string into a JSON array
                //and then convert each element in the JSON array to a map and add each map as a row in the table
                JSONArray jsonArray = new JSONArray((String) requestMap.get("productDetails"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    addRows(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }

                //table added inside the document
                document.add(table);

                //create pdf footer by applying Data font and add it to the document.
                Paragraph footer = new Paragraph("Total : " + requestMap.get("totalAmount") + "\n"
                        + "Thank you for visiting our website.", getFont("Data"));
                document.add(footer);

                //After calling document.close(), no further modifications or additions can be made to the document.
                //It essentially seals the document, making it ready for saving, viewing, or further processing.
                document.close();


                return new ResponseEntity<>("{\"uuid\":\"" + filename + "\"}", HttpStatus.OK);

            }
            return CafeUtils.getResponeEntity("Required data not found", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponeEntity(Cafeconstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<java.util.List<Bill>> getBills() {
        List<Bill> list = new ArrayList<>();
        if (jwtFilter.isAdmin()) {
            list = billRepository.getAllBills();
        } else {
            list = billRepository.getBillByUserName(jwtFilter.getCurrentUsername());
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    private byte[] getByteArray(String filepath) throws Exception {
        File initalFile = new File(filepath);
        InputStream targetStream = new FileInputStream(initalFile);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

//output will be a byte array and for valid output the types of input can be
//    a)for files with filepath exists just uuid is enough
//    b)for uuid that doesn't exist in file explorer the required information should be provided to generate the pdf.
// we must provide the filename i.e uuid
//if that corresponding file doesn't exist at that appropriate place it file explorer, it will normally
//generate the pdf and add it at appropriate place in file explorer without saving again inside the database
    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("Inside getPdf : requestMap {}", requestMap);
        try {
            byte[] byteArray = new byte[0];
            //doesn't understand why validateRequestMap condition is there here
            if (!requestMap.containsKey("uuid") && validateResquestMap(requestMap)) {
                return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
            }
            String filepath = Cafeconstants.STORE_LOCATION + "\\" + (String) requestMap.get("uuid") + ".pdf";

            if (CafeUtils.isFileExist(filepath)) {
                log.info("file path matched");
                byteArray = getByteArray(filepath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            } else {
//if that file doesn't exist at that appropriate place it file explorer, it will normally
//generate the pdf and add it at appropriate place in file explorer without saving again inside the database
                requestMap.put("isGenerate", false);
                generateReport(requestMap);
                byteArray = getByteArray(filepath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }
//if that file doesn't even exist in our database also, we will be getting a pdf in the output according to the
//provided details and added into appropriate place in file explorer without saving inside the database
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    //can't delete from file explorer
    @Override
    public ResponseEntity<String> delete(Integer id) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional optional = billRepository.findById(id);
                if (!optional.isEmpty()) {
                    billRepository.deleteById(id);
                    return CafeUtils.getResponeEntity("Bill is deleted successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponeEntity("Bill id doesn't exist", HttpStatus.OK);
            } else {
                return CafeUtils.getResponeEntity(Cafeconstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponeEntity(Cafeconstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



}
