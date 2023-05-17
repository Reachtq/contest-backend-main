package com.group.contestback.common;

import com.group.contestback.models.AppUser;
import com.group.contestback.models.Groups;
import com.group.contestback.models.Scores;
import com.group.contestback.models.TaskCourses;
import com.group.contestback.repositories.AppUserRepo;
import com.group.contestback.repositories.GroupsRepo;
import com.group.contestback.repositories.TaskCoursesRepo;
import com.group.contestback.services.ScoresService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.hibernate.transform.CacheableResultTransformer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class GenerateReport {

    private final AppUserRepo appUserRepo;
    private final TaskCoursesRepo taskCoursesRepo;
    private final GroupsRepo groupsRepo;
    private final ScoresService scoresService;


    public XSSFWorkbook createReport(HttpServletResponse response, Integer groupId, Integer courseId) throws IOException {
        List<Integer> idsTask = getIdsTask(courseId);
        List<AppUser> users = appUserRepo.findAllByGroupId(groupId);
        String group = groupsRepo.getById(groupId).getNumber();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Ведомость группы 9375");
        createHeader(workbook, idsTask);
        createBody(workbook, idsTask, users, group);


        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=report_group_" + group + ".xlsx");
        return workbook;
    }

    private List<Integer> getIdsTask(Integer courseId) {
        List<TaskCourses> taskCourses = taskCoursesRepo.findAllByCourseId(courseId);
        return taskCourses.stream().map(TaskCourses::getTaskId).collect(Collectors.toList());
    }

    private void createHeader(XSSFWorkbook workbook, List<Integer> idsTask) {
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);

        XSSFCellStyle style = createTemplate(workbook);
        style.setFont(font);

        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell;

        cell = row.createCell(0, CellType.STRING);
        cell.setCellStyle(style);
        cell.setCellValue("Группа");

        cell = row.createCell(1, CellType.STRING);
        cell.setCellStyle(style);
        cell.setCellValue("ФИО");


        cell = row.createCell(2, CellType.STRING);
        cell.setCellStyle(style);
        cell.setCellValue("Номер задания");

        CellRangeAddress mergedCell = new CellRangeAddress(0, 1, 0, 0);
        sheet.addMergedRegion(mergedCell);
        mergedCell = new CellRangeAddress(0, 1, 1, 1);
        sheet.addMergedRegion(mergedCell);
        mergedCell = new CellRangeAddress(0, 0, 2, 1 + idsTask.size());
        sheet.addMergedRegion(mergedCell);

        XSSFRow row1 = sheet.createRow(1);
        for (int i = 0; i < idsTask.size(); i++) {
            cell = row1.createCell(i + 2, CellType.STRING);
            cell.setCellStyle(style);
            cell.setCellValue(i + 1);
        }
        cell = row.createCell(idsTask.size() + 2, CellType.STRING);
        cell.setCellStyle(style);
        cell.setCellValue("Итог");
        mergedCell = new CellRangeAddress(0, 1, idsTask.size() + 2, idsTask.size() + 2);
        sheet.addMergedRegion(mergedCell);

        sheet.setColumnWidth(1, 10000);
    }

    private void createBody(XSSFWorkbook workbook, List<Integer> idsTask, List<AppUser> users, String group) {
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);

        XSSFCellStyle style = createTemplate(workbook);
        style.setFont(font);

        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFCell cell;
        for (int i = 0; i < users.size(); i++) {
            XSSFRow row = sheet.createRow(i + 2);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellStyle(style);
            cell.setCellValue(group);
            cell = row.createCell(1, CellType.STRING);
            cell.setCellStyle(style);
            cell.setCellValue(users.get(i).getFio());
            String address = cell.getAddress().toString();
            for (int j = 0; j < idsTask.size(); j++) {
                Integer score = scoresService.getStudentScore(users.get(i).getId(), idsTask.get(j));
                cell = row.createCell(j + 2, CellType.NUMERIC);
                cell.setCellStyle(style);
                cell.setCellValue(score);
                address = cell.getAddress().toString();
            }
            cell = row.createCell(idsTask.size() + 2, CellType.NUMERIC);
            cell.setCellStyle(style);
            cell.setCellFormula("SUM(C" + (i + 3) + ":" + address + ")");
        }

    }


    private XSSFCellStyle createTemplate(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

}
