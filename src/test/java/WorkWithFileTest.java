import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;


@Tag("WorkWithFileTest")
public class WorkWithFileTest {

    private ClassLoader cl = WorkWithFileTest.class.getClassLoader();

    @DisplayName("Проверка файла в PDF формате")
    @Test
    void checkPDFFile() throws IOException {
        PDF pdf = new PDF(new File("src/test/resources/files/file-example_PDF_1MB.pdf").getAbsoluteFile());

        assertThat(pdf.text)
                .as("Content must contain \"Lorem ipsum\"").contains("Lorem ipsum");
        assertThat(pdf.numberOfPages)
                .as("numberOfPages must be equal to \"30\"").isEqualTo(30);
        assertThat(pdf.creator)
                .as("Creator must contain \"Writer\"").contains("Writer");
    }

    @DisplayName("Проверка файла в XLS формате")
    @Test
    void checkExcelFile() {
        XLS xls = new XLS(new File("src/test/resources/files/ExcelFile2004.xls"));

        assertThat(xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue())
                .as("Field must be \"Имя\"").isEqualTo("Имя");
        assertThat(xls.excel.getSheetAt(0).getRow(0).getCell(1).getStringCellValue())
                .as("Field must be \"Фамилия\"").isEqualTo("Фамилия");
    }

    @DisplayName("Проверка файла в XLSX формате")
    @Test
    void checkXLSXFile() {
        XLS xls = new XLS(new File("src/test/resources/files/ExcelFile.xlsx"));

        assertThat(xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue())
                .as("Field must be \"Имя\"").isEqualTo("Имя");
        assertThat(xls.excel.getSheetAt(0).getRow(0).getCell(1).getStringCellValue())
                .as("Field must be \"Фамилия\"").isEqualTo("Фамилия");
    }

    @DisplayName("Проверка файлов файла в WinZip формате")
    @Test
    void checkWinZipFile() throws Exception {
        ZipFile zipFile = new ZipFile(new File("src/test/resources/files/Untitled.zip"));
        try (InputStream stream = cl.getResourceAsStream("files/Untitled.zip");
             ZipInputStream zis = new ZipInputStream(stream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                assertThat(zipEntry.getName()).isSubstringOf("ExcelFile.xlsx, ExcelFile2004.xls, file-example_PDF_1MB.pdf ");
                String fileName = zipEntry.getName();
                String [] fileWithExtension = fileName.split("\\.");
                switch (fileWithExtension[1]) {
                    case "xlsx":
                    case "xls": {
                        ZipEntry xlsxEntry = zipFile.getEntry(zipEntry.getName());
                        try (InputStream xlsSream = zipFile.getInputStream(xlsxEntry)){
                            XLS xlsx = new XLS(xlsSream);
                            assertThat(xlsx.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue())
                                    .as("Field must be \"Имя\"").isEqualTo("Имя");
                            assertThat(xlsx.excel.getSheetAt(0).getRow(0).getCell(1).getStringCellValue())
                                    .as("Field must be \"Фамилия\"").isEqualTo("Фамилия");
                        }
                    }break;
                    case "pdf":ZipEntry xlsxEntry = zipFile.getEntry(zipEntry.getName());
                        try (InputStream xlsSream = zipFile.getInputStream(xlsxEntry)){
                            PDF pdf = new PDF(xlsSream);
                            assertThat(pdf.text)
                                    .as("Content must contain \"Lorem ipsum\"").contains("Lorem ipsum");
                            assertThat(pdf.numberOfPages)
                                    .as("numberOfPages must be equal to \"30\"").isEqualTo(30);
                            assertThat(pdf.creator)
                                    .as("Creator must contain \"Writer\"").contains("Writer");
                        }break;
                    default: throw new Exception();
                }
            }
        }
    }
}
