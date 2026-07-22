package com.g4fpt.sms.voucher;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateVoucherTest {

    static WebDriver driver;

    @BeforeAll
    public static void setUp() throws InterruptedException {
        // Tự động tải ChromeDriver tương thích
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Đăng nhập
        driver.get("http://localhost:8080/login");
        driver.findElement(By.id("email")).sendKeys("owner@gmail.com");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        Thread.sleep(1500); // Chờ đăng nhập xong

        // Tạo mồi VOUCHER_AUTO_01 để test duplicate pass
        try {
            driver.get("http://localhost:8080/vouchers/create");
            Thread.sleep(500);
            driver.findElement(By.id("code")).sendKeys("VOUCHER_AUTO_01");
            driver.findElement(By.id("name")).sendKeys("Mồi test trùng");
            new Select(driver.findElement(By.id("discountType"))).selectByValue("AMOUNT");
            driver.findElement(By.id("discountValue")).sendKeys("1000");
            
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.getElementById('startAt').value = '2026-05-08T00:00';");
            js.executeScript("document.getElementById('endAt').value = '2026-10-08T00:00';");
            
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("Could not seed VOUCHER_AUTO_01: " + e.getMessage());
        }
    }

    @ParameterizedTest(name = "Test {index}: Code={0}, ExpectedSuccess={7}")
    @CsvSource({
            // Các test case mong đợi THÀNH CÔNG (Ngày bắt đầu phải ở TƯƠNG LAI, chuyển sang tháng 08/2026)
            "VOUCHER_TEST_01, Giảm 10%, PERCENT, 10, 100000, 08-05-2026, 08-10-2026, true",
            "VOUCHER_TEST_02, Giảm 50K, AMOUNT, 50000, 200000, 08-05-2026, 08-10-2026, true",
            "VOUCHER_TEST_03, Tặng VIP, PERCENT, 15, 300000, 08-05-2026, 08-10-2026, true",
            "VOUCHER_TEST_04, Không đơn tối thiểu, AMOUNT, 20000, '', 08-05-2026, 08-10-2026, true",
            "VOUCHER_TEST_05, Sale Flash trong ngày, PERCENT, 5, 50000, 08-10-2026, 08-10-2026, true",

            // Lỗi: Mã trùng (Cố tình set false để check validation mã trùng)
            "VOUCHER_AUTO_01, Trùng mã, AMOUNT, 10000, 50000, 08-05-2026, 08-10-2026, false",

            // Các test case mong đợi THẤT BẠI (Validation Error)
            "'', Bỏ trống mã, PERCENT, 10, 100000, 08-05-2026, 08-10-2026, false",
            "VOUCHER_ERR_01, '', PERCENT, 10, 100000, 08-05-2026, 08-10-2026, false",
            "VOUCHER_ERR_02, Bỏ trống loại giảm, '', 10, 100000, 08-05-2026, 08-10-2026, false",
            "VOUCHER_ERR_03, Bỏ trống giá trị, PERCENT, '', 100000, 08-05-2026, 08-10-2026, false",
            "VOUCHER_ERR_04, Bỏ trống ngày bắt đầu, AMOUNT, 10000, 50000, '', 08-10-2026, false",
            "VOUCHER_ERR_05, Bỏ trống ngày kết thúc, AMOUNT, 10000, 50000, 08-05-2026, '', false",

            "VOUCHER_ERR_06, Giảm giá âm, AMOUNT, -10000, 50000, 08-05-2026, 08-10-2026, false",
            "VOUCHER_ERR_07, Phần trăm lớn hơn 100, PERCENT, 110, 100000, 08-05-2026, 08-10-2026, false",
            "VOUCHER_ERR_08, Ngày kết thúc trước bắt đầu, AMOUNT, 20000, 50000, 08-10-2026, 08-05-2026, false"
    })
    public void testCreateVoucher(String code, String name, String type, String value, String minAmt, String start,
                                  String end, boolean isSuccessExpected) throws InterruptedException {
        System.out.println("RUNNING TEST: code=" + code + ", name=" + name + ", type=" + type);
        
        // Mở trang tạo voucher
        driver.get("http://localhost:8080/vouchers/create");
        Thread.sleep(500); // Đợi form render xong

        if (code != null && !code.trim().isEmpty()) {
            if (isSuccessExpected) {
                code = code + "_" + System.currentTimeMillis();
            }
            driver.findElement(By.id("code")).sendKeys(code);
        }
        if (name != null && !name.trim().isEmpty()) {
            driver.findElement(By.id("name")).sendKeys(name);
        }
        if (type != null && !type.trim().isEmpty()) {
            new Select(driver.findElement(By.id("discountType"))).selectByValue(type.trim());
        }

        if (value != null && !value.trim().isEmpty()) {
            driver.findElement(By.id("discountValue")).sendKeys(value);
        }
        if (minAmt != null && !minAmt.trim().isEmpty()) {
            driver.findElement(By.id("minOrderAmount")).sendKeys(minAmt);
        }

        // Xử lý ngày tháng bằng JS cho input datetime-local
        if (start != null && !start.trim().isEmpty()) {
            WebElement startDate = driver.findElement(By.id("startAt"));
            String formattedStart = start.substring(6, 10) + "-" + start.substring(0, 2) + "-" + start.substring(3, 5)
                    + "T10:00";
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = '" + formattedStart + "';", startDate);
        }

        if (end != null && !end.trim().isEmpty()) {
            WebElement endDate = driver.findElement(By.id("endAt"));
            String formattedEnd = end.substring(6, 10) + "-" + end.substring(0, 2) + "-" + end.substring(3, 5)
                    + "T10:00";
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = '" + formattedEnd + "';", endDate);
        }

        // Click nút tạo voucher
        driver.findElement(By.id("btn-form-submit")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        
        // Kiểm tra kết quả
        if (isSuccessExpected) {
            try {
                wait.until(ExpectedConditions.urlToBe("http://localhost:8080/vouchers"));
            } catch (Exception e) {
                // Sẽ assert ở dưới
            }
            // Yêu cầu chuyển hướng về trang danh sách
            if (!driver.getCurrentUrl().endsWith("/vouchers")) {
                java.util.List<WebElement> errors = driver.findElements(By.className("field-error"));
                for (WebElement err : errors) {
                    System.out.println("VALIDATION ERROR FOUND: " + err.getText());
                }
                java.util.List<WebElement> alerts = driver.findElements(By.className("alert"));
                for (WebElement alert : alerts) {
                    System.out.println("ALERT ERROR FOUND: " + alert.getText());
                }
                System.out.println("PAGE SOURCE: " + driver.getPageSource());
            }
            assertTrue(driver.getCurrentUrl().endsWith("/vouchers"),
                    "Phải chuyển hướng về trang danh sách sau khi lưu thành công");
        } else {
            // Vẫn phải ở nguyên trang form => Có lỗi báo đỏ
            boolean hasError = !driver.findElements(By.className("field-error")).isEmpty()
                    || !driver.findElements(By.className("input-error")).isEmpty()
                    || !driver.findElements(By.className("alert")).isEmpty();
                    
            assertTrue(hasError, "Hệ thống phải báo lỗi (Validation Error hoặc Alert) khi nhập dữ liệu không hợp lệ");
        }
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
