package com.group.contestback.common;

import com.group.contestback.models.AppUser;
import com.group.contestback.models.Groups;
import com.group.contestback.repositories.GroupsRepo;
import com.group.contestback.services.AppUserService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RequiredArgsConstructor
public class GenerateUsersPasswordAndLoginXLSX {
    private final AppUserService userService;
    private final GroupsRepo groupsRepo;

    public XSSFWorkbook readFromWorkbookAndSaveNewUser(MultipartFile file) {
        XSSFWorkbook workbook = createWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        createHeader(workbook);
        sheet.setColumnWidth(5, 5000);
        sheet.setColumnWidth(6, 5000);
        for (int numRow = 1; numRow <= sheet.getLastRowNum(); numRow++) {
            XSSFRow row = sheet.getRow(numRow);

            Integer idGroup;
            String firstName, lastName, middleName, email;
            String password, login;

            String groupNumberString = String.valueOf(row.getCell(0).getNumericCellValue());
            String groupNumberWithoutPoint = groupNumberString.substring(0, groupNumberString.indexOf("."));

            if (groupsRepo.findGroupsByNumber(groupNumberWithoutPoint) == null) {
                groupsRepo.save(new Groups(groupNumberWithoutPoint,  LocalDate.now().getYear()));
            }

            idGroup = groupsRepo.findGroupsByNumber(groupNumberWithoutPoint).getId();
            lastName = row.getCell(1).getStringCellValue();
            firstName = row.getCell(2).getStringCellValue();
            middleName = row.getCell(3).getStringCellValue();
            email = row.getCell(4).getStringCellValue();
            login = LoginUtil.generateRandomLogin();
            password = PasswordUtil.generateRandomPassword();

            addLoginInRow(workbook, numRow, login);
            addPasswordInRow(workbook, numRow, password);

            userService.saveAppUser(new AppUser(firstName, lastName, middleName, login, password, "", email, 2, idGroup));
        }
        return workbook;
    }

    private XSSFWorkbook createWorkbook(MultipartFile file) {
        XSSFWorkbook workbook;
        try {
            workbook = new XSSFWorkbook(file.getInputStream());
            return workbook;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return new XSSFWorkbook();
    }

    private void createHeader(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);

        XSSFCellStyle style = createTemplate(workbook);
        style.setFont(font);

        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.getRow(0);
        XSSFCell cell;

        cell = row.createCell(row.getLastCellNum(), CellType.STRING);
        cell.setCellStyle(style);
        cell.setCellValue("Логин");

        cell = row.createCell(row.getLastCellNum(), CellType.STRING);
        cell.setCellStyle(style);
        cell.setCellValue("Пароль");

    }

    private void addPasswordInRow(XSSFWorkbook workbook, int rowNum, String password) {
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);

        XSSFCellStyle style = createTemplate(workbook);

        style.setFont(font);

        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.getRow(rowNum);

        XSSFCell cellPassword = row.createCell(row.getLastCellNum(), CellType.STRING);
        cellPassword.setCellStyle(style);
        cellPassword.setCellValue(password);
    }

    private void addLoginInRow(XSSFWorkbook workbook, int rowNum, String login) {
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);

        XSSFCellStyle style = createTemplate(workbook);

        style.setFont(font);

        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.getRow(rowNum);

        XSSFCell cellLogin = row.createCell(row.getLastCellNum(), CellType.STRING);
        cellLogin.setCellStyle(style);
        cellLogin.setCellValue(login);
    }

    private XSSFCellStyle createTemplate(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();

        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }
}

