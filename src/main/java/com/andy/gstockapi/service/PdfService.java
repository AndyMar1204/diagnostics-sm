package com.andy.gstockapi.service;

import com.andy.gstockapi.dto.InvoiceItemResponse;
import com.andy.gstockapi.dto.InvoiceResponse;
import com.andy.gstockapi.entity.InvoiceType;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.awt.Color;

@Service
public class PdfService {

    public byte[] generateInvoicePdf(InvoiceResponse invoice) {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Colors
            Color tealColor = new Color(13, 89, 91); // Dark teal
            Color tableHeaderColor = new Color(209, 217, 230); // Light blue/grey
            Color footerBoxBorder = new Color(13, 89, 91);

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, tealColor);
            Font companyInfoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, tealColor);
            Font infoBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
            Font footerTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, tealColor);
            Font footerTextFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
            Font contactBoxFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);

            // --- HEADER ---
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 1});

            // Logo (Left)
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            try {
                InputStream is = getClass().getResourceAsStream("/static/logo v3 x512.png");
                if (is != null) {
                    byte[] bytes = is.readAllBytes();
                    Image logo = Image.getInstance(bytes);
                    logo.scaleToFit(100, 100);
                    logoCell.addElement(logo);
                }
            } catch (Exception e) {
                logoCell.addElement(new Paragraph("360 DIAGNOSTICS", companyInfoFont));
            }
            headerTable.addCell(logoCell);

            // Company Details (Right)
            PdfPCell companyDetailsCell = new PdfPCell();
            companyDetailsCell.setBorder(Rectangle.NO_BORDER);
            companyDetailsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph companyName = new Paragraph("360 Diagnostics By Athalie & Co BV", companyInfoFont);
            companyName.setAlignment(Element.ALIGN_RIGHT);
            companyDetailsCell.addElement(companyName);
            
            Paragraph detail1 = new Paragraph("Numéro d'entreprise : 1031.017.750", companyInfoFont);
            detail1.setAlignment(Element.ALIGN_RIGHT);
            companyDetailsCell.addElement(detail1);
            
            Paragraph detail2 = new Paragraph("Imeldalaan 16, 2820 Bonheiden, Belgique", companyInfoFont);
            detail2.setAlignment(Element.ALIGN_RIGHT);
            companyDetailsCell.addElement(detail2);
            
            Paragraph detail3 = new Paragraph("Succursale Kinshasa, R.D.Congo", companyInfoFont);
            detail3.setAlignment(Element.ALIGN_RIGHT);
            companyDetailsCell.addElement(detail3);
            
            Paragraph detail4 = new Paragraph("RCCM : CD/KNG/RCCM/26-B-01019", companyInfoFont);
            detail4.setAlignment(Element.ALIGN_RIGHT);
            companyDetailsCell.addElement(detail4);
            
            headerTable.addCell(companyDetailsCell);
            document.add(headerTable);

            // --- TITLE ---
            String titleText = invoice.getType() == InvoiceType.PROFORMA ? "FACTURE PROFORMA" : "FACTURE";
            Paragraph title = new Paragraph(titleText, titleFont);
            title.setSpacingBefore(20);
            title.setSpacingAfter(10);
            document.add(title);

            // --- INFO SECTION ---
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 1});
            infoTable.setSpacingAfter(20);

            // Invoice Info (Left)
            PdfPCell invInfoCell = new PdfPCell();
            invInfoCell.setBorder(Rectangle.NO_BORDER);
            invInfoCell.addElement(new Phrase("Numéro de facture : " + (invoice.getReference() != null ? invoice.getReference() : ""), infoBoldFont));
            invInfoCell.addElement(new Phrase("\nDate de facturation : " + (invoice.getCreatedAt() != null ? invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""), infoBoldFont));
            infoTable.addCell(invInfoCell);

            // Client Info (Right)
            PdfPCell clientInfoCell = new PdfPCell();
            clientInfoCell.setBorder(Rectangle.NO_BORDER);
            clientInfoCell.addElement(new Paragraph("Facturé à :", labelFont));
            clientInfoCell.addElement(new Paragraph(invoice.getClient().getName(), infoBoldFont));
            if (invoice.getClient().getAddress() != null) {
                clientInfoCell.addElement(new Paragraph(invoice.getClient().getAddress(), normalFont));
            }
            if (invoice.getClient().getPhone() != null) {
                clientInfoCell.addElement(new Paragraph("Tel : " + invoice.getClient().getPhone(), infoBoldFont));
            }
            infoTable.addCell(clientInfoCell);
            document.add(infoTable);

            // --- ITEMS TABLE ---
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 4, 2, 2});

            // Table Headers
            String[] headers = {"QTE", "DESIGNATION", "PRIX UNIT ($)", "MONTANT"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, tableHeaderFont));
                cell.setBackgroundColor(tableHeaderColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                cell.setBorderWidth(1);
                table.addCell(cell);
            }

            // Table Rows
            for (InvoiceItemResponse item : invoice.getItems()) {
                PdfPCell qteCell = new PdfPCell(new Phrase(item.getQuantity().toString(), normalFont));
                qteCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                qteCell.setPadding(5);
                table.addCell(qteCell);

                PdfPCell descCell = new PdfPCell(new Phrase(item.getProduct().getName(), normalFont));
                descCell.setPadding(5);
                table.addCell(descCell);

                PdfPCell puCell = new PdfPCell(new Phrase(item.getUnitPrice().toString(), normalFont));
                puCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                puCell.setPadding(5);
                table.addCell(puCell);

                PdfPCell totalItemCell = new PdfPCell(new Phrase(item.getTotalPrice().toString(), normalFont));
                totalItemCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalItemCell.setPadding(5);
                table.addCell(totalItemCell);
            }

            // Total Row
            PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total", tableHeaderFont));
            totalLabelCell.setColspan(3);
            totalLabelCell.setBackgroundColor(tableHeaderColor);
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalLabelCell.setPadding(5);
            table.addCell(totalLabelCell);

            PdfPCell totalValueCell = new PdfPCell(new Phrase(invoice.getTotalAmount().toString(), tableHeaderFont));
            totalValueCell.setBackgroundColor(tableHeaderColor);
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValueCell.setPadding(5);
            table.addCell(totalValueCell);

            document.add(table);

            // --- FOOTER ---
            document.add(new Paragraph("\n"));
            
            // Delivery and Payment info
            PdfPTable footerInfoTable = new PdfPTable(1);
            footerInfoTable.setWidthPercentage(100);
            
            PdfPCell deliveryCell = new PdfPCell();
            deliveryCell.setBorder(Rectangle.NO_BORDER);
            deliveryCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            Paragraph delTitle = new Paragraph("Délai de Livraison :", footerTitleFont);
            delTitle.setAlignment(Element.ALIGN_CENTER);
            deliveryCell.addElement(delTitle);
            Paragraph delText = new Paragraph("7 à 27 Jours après l'achat", footerTextFont);
            delText.setAlignment(Element.ALIGN_CENTER);
            deliveryCell.addElement(delText);
            footerInfoTable.addCell(deliveryCell);
            
            PdfPCell payCell = new PdfPCell();
            payCell.setBorder(Rectangle.NO_BORDER);
            payCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            payCell.setPaddingTop(10);
            Paragraph payTitle = new Paragraph("Mode Paiement :", footerTitleFont);
            payTitle.setAlignment(Element.ALIGN_CENTER);
            payCell.addElement(payTitle);
            
            // Sub-table for icons and text
            PdfPTable payIconsTable = new PdfPTable(3);
            payIconsTable.setWidthPercentage(60); // Narrower to center it more
            payIconsTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            // Mobile Money
            PdfPCell mmCell = new PdfPCell();
            mmCell.setBorder(Rectangle.NO_BORDER);
            mmCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            Paragraph mmText = new Paragraph("Mobile Money\nespèce", footerTextFont);
            mmText.setAlignment(Element.ALIGN_CENTER);
            mmCell.addElement(mmText);
            try {
                InputStream is = getClass().getResourceAsStream("/static/orange money.png");
                if (is != null) {
                    Image img = Image.getInstance(is.readAllBytes());
                    img.scaleToFit(30, 30);
                    img.setAlignment(Image.ALIGN_CENTER);
                    mmCell.addElement(img);
                }
            } catch (Exception e) {}
            payIconsTable.addCell(mmCell);

            // Virement Bancaire
            PdfPCell vbCell = new PdfPCell();
            vbCell.setBorder(Rectangle.NO_BORDER);
            vbCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            Paragraph vbText = new Paragraph("Virement Bancaire", footerTextFont);
            vbText.setAlignment(Element.ALIGN_CENTER);
            vbCell.addElement(vbText);
            try {
                InputStream is = getClass().getResourceAsStream("/static/ecobank logo.png");
                if (is != null) {
                    Image img = Image.getInstance(is.readAllBytes());
                    img.scaleToFit(50, 30);
                    img.setAlignment(Image.ALIGN_CENTER);
                    vbCell.addElement(img);
                }
            } catch (Exception e) {}
            payIconsTable.addCell(vbCell);

            // Paiement en...
            PdfPCell peCell = new PdfPCell();
            peCell.setBorder(Rectangle.NO_BORDER);
            peCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            Paragraph peText = new Paragraph("Paiement en", footerTextFont);
            peText.setAlignment(Element.ALIGN_CENTER);
            peCell.addElement(peText);
            try {
                InputStream is = getClass().getResourceAsStream("/static/paiement cash.jpg");
                if (is != null) {
                    Image img = Image.getInstance(is.readAllBytes());
                    img.scaleToFit(30, 30);
                    img.setAlignment(Image.ALIGN_CENTER);
                    peCell.addElement(img);
                }
            } catch (Exception e) {}
            payIconsTable.addCell(peCell);

            payCell.addElement(payIconsTable);
            footerInfoTable.addCell(payCell);
            
            document.add(footerInfoTable);

            // Signatory
            Paragraph signatory = new Paragraph("\nJulie Bokoli Nsono", infoBoldFont);
            signatory.setAlignment(Element.ALIGN_RIGHT);
            document.add(signatory);
            Paragraph position = new Paragraph("Directrice Générale 360 Diagnostics by\nAthalie and Co RDCongo", footerTextFont);
            position.setAlignment(Element.ALIGN_RIGHT);
            document.add(position);

            // Contact Boxes
            PdfPTable contactTable = new PdfPTable(3);
            contactTable.setWidthPercentage(100);
            contactTable.setSpacingBefore(30);
            contactTable.setWidths(new float[]{1, 1, 1});

            // Phone box
            PdfPCell phoneCell = new PdfPCell();
            phoneCell.setBorderColor(footerBoxBorder);
            phoneCell.setBorderWidth(1.5f);
            phoneCell.setPadding(5);
            phoneCell.addElement(new Paragraph("+243822828808   +243856469791\n+32465513982   +243999676349", contactBoxFont));
            contactTable.addCell(phoneCell);

            // Email box
            PdfPCell emailCell = new PdfPCell();
            emailCell.setBorderColor(footerBoxBorder);
            emailCell.setBorderWidth(1.5f);
            emailCell.setPadding(5);
            emailCell.addElement(new Paragraph("gedmangwala@gmail.com\nkongoloroland@gmail.com", contactBoxFont));
            contactTable.addCell(emailCell);

            // Address box
            PdfPCell addrCell = new PdfPCell();
            addrCell.setBorderColor(footerBoxBorder);
            addrCell.setBorderWidth(1.5f);
            addrCell.setPadding(5);
            addrCell.addElement(new Paragraph("Imeldalaan 16, 2820 Bonheiden\nBelgique", contactBoxFont));
            contactTable.addCell(addrCell);

            document.add(contactTable);

            // Thank you message
            Paragraph thanks = new Paragraph("\nMerci de nous faire confiance", labelFont);
            thanks.setAlignment(Element.ALIGN_CENTER);
            document.add(thanks);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}

