package com.andy.gstockapi.service;

import com.andy.gstockapi.dto.InvoiceItemResponse;
import com.andy.gstockapi.dto.InvoiceResponse;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.awt.Color;

@Service
public class PdfService {

    public byte[] generateInvoicePdf(InvoiceResponse invoice) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Font styles
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

            // Title
            Paragraph title = new Paragraph("G-STOCK INVOICE", headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Invoice Info Table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20);

            // Company Info
            PdfPCell companyCell = new PdfPCell();
            companyCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            companyCell.addElement(new Paragraph("G-STOCK ERP", subHeaderFont));
            companyCell.addElement(new Paragraph("123 Business Street", normalFont));
            companyCell.addElement(new Paragraph("Business City, 10001", normalFont));
            companyCell.addElement(new Paragraph("support@gstock.com", normalFont));
            infoTable.addCell(companyCell);

            // Invoice Info
            PdfPCell invoiceCell = new PdfPCell();
            invoiceCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            invoiceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            invoiceCell.addElement(new Paragraph("Reference: " + invoice.getReference(), subHeaderFont));
            invoiceCell.addElement(new Paragraph("Date: " + invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), normalFont));
            invoiceCell.addElement(new Paragraph("Type: " + invoice.getType(), normalFont));
            invoiceCell.addElement(new Paragraph("Status: " + invoice.getStatus(), normalFont));
            infoTable.addCell(invoiceCell);
            document.add(infoTable);

            // Client Info
            document.add(new Paragraph("BILL TO:", tableHeaderFont));
            Paragraph clientInfo = new Paragraph();
            clientInfo.add(new Chunk(invoice.getClient().getName() + "\n", subHeaderFont));
            clientInfo.add(new Chunk(invoice.getClient().getAddress() + "\n", normalFont));
            clientInfo.add(new Chunk(invoice.getClient().getPhone() + "\n", normalFont));
            clientInfo.add(new Chunk(invoice.getClient().getEmail(), normalFont));
            clientInfo.setSpacingAfter(20);
            document.add(clientInfo);

            // Items Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 2, 2, 2});

            // Table Headers
            String[] headers = {"Product", "Quantity", "Unit Price", "Total"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, tableHeaderFont));
                cell.setBackgroundColor(Color.GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Table Rows
            for (InvoiceItemResponse item : invoice.getItems()) {
                table.addCell(new PdfPCell(new Phrase(item.getProduct().getName(), normalFont)));
                PdfPCell qtyCell = new PdfPCell(new Phrase(item.getQuantity().toString(), normalFont));
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(qtyCell);
                
                PdfPCell priceCell = new PdfPCell(new Phrase(item.getUnitPrice().toString(), normalFont));
                priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(priceCell);
                
                PdfPCell totalCell = new PdfPCell(new Phrase(item.getTotalPrice().toString(), normalFont));
                totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(totalCell);
            }

            // Summary row for total
            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setColspan(2);
            emptyCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            table.addCell(emptyCell);

            PdfPCell labelCell = new PdfPCell(new Phrase("TOTAL AMOUNT", subHeaderFont));
            labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            labelCell.setPadding(5);
            table.addCell(labelCell);

            PdfPCell amountCell = new PdfPCell(new Phrase(invoice.getTotalAmount().toString(), subHeaderFont));
            amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            amountCell.setPadding(5);
            table.addCell(amountCell);

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
