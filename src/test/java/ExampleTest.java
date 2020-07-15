import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class ExampleTest {

    private static final String targetURL = "https://www.rgs.ru";
    private static WebDriver driver;
    private static Wait<WebDriver> wait;

    @Before
    public void initialization() {
        System.setProperty("webdriver.chrome.driver", "webdrivers/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.get(targetURL);
    }

    @Test
    public void startTest() throws InterruptedException {
        //нажатие кнопки "Меню"
        WebElement menuButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id='main-navbar']//a[@data-toggle='dropdown' and contains(text(), 'Меню')]")));
        menuButton.click();

        //переход по ссылке "ДМС"
        WebElement dmsButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(text(),'ДМС')]")));
        dmsButton.click();

        //Проверка наличия заголовка "ДМС — добровольное медицинское страхование"
        WebElement headerDms = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[@class='content-document-header']")));
        Assert.assertEquals("Заголовок не соответствет",
                "ДМС — добровольное медицинское страхование", headerDms.getText());

        WebElement sendRequestButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(text(), 'Отправить заявку')]")));
        sendRequestButton.click();

        //проверка открытия формы заявки
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h4//b[text()='Заявка на добровольное медицинское страхование']")));
        } catch (org.openqa.selenium.NoSuchElementException e) {
            System.out.println("Форма заявки не открылась");
        }

        //создание веб-элементов полей
        WebElement lastNameInput = driver.findElement(By.xpath("//input[@name='LastName']"));
        WebElement firstNameInput = driver.findElement(By.xpath("//input[@name='FirstName']"));
        WebElement middleNameInput = driver.findElement(By.xpath("//input[@name='MiddleName']"));
        WebElement selectRegion = driver.findElement(By.xpath("//select[@name='Region']"));
        WebElement telephoneNumber = driver.findElement(By.xpath("//label[text()='Телефон']//..//input"));
        WebElement email = driver.findElement(By.xpath("//input[@name='Email']"));
        WebElement date = driver.findElement(By.xpath("//label[text()='Предпочитаемая дата контакта']//..//input"));
        WebElement comment = driver.findElement(By.xpath("//textarea[@name='Comment']"));
        WebElement checkbox = driver.findElement(By.xpath("//input[@type='checkbox']"));
        WebElement sendButton = driver.findElement(By.xpath("//button[@id='button-m']"));

        //заполнение формы тестовыми значениями
        lastNameInput.sendKeys("Иванов");
        firstNameInput.sendKeys("Иван");
        middleNameInput.sendKeys("Иванович");
        new Select(selectRegion).selectByIndex(1);

        telephoneNumber.click();
        int[] telNumbsSeq = {9,9,9,0,0,0,1,1,2,2};
        for (int s : telNumbsSeq) {
            telephoneNumber.sendKeys(String.valueOf(s));
            Thread.sleep(50);
        }

        email.sendKeys("qwertyqwerty");

        date.click();
        int[] dateNumbsSeq = {2,0,0,8,2,0,2,0};
        for (int s : dateNumbsSeq) {
            date.sendKeys(String.valueOf(s));
            Thread.sleep(50);
        }

        comment.sendKeys("йцукен");
        checkbox.click();

        //проверка корректности заполнения формы тестовыми значениями
        Assert.assertEquals("Ошибка ввода фамилии", "Иванов", lastNameInput.getAttribute("value"));
        Assert.assertEquals("Ошибка ввода имени", "Иван", firstNameInput.getAttribute("value"));
        Assert.assertEquals("Ошибка ввода отчества", "Иванович", middleNameInput.getAttribute("value"));
        Assert.assertEquals("Ошибка выбора региона", "77", selectRegion.getAttribute("value"));
        Assert.assertEquals("Ошибка ввода номера телефона", "+7 (999) 000-11-22", telephoneNumber.getAttribute("value"));
        Assert.assertEquals("Ошибка ввода email", "qwertyqwerty", email.getAttribute("value"));
        Assert.assertEquals("Ошибка ввода даты", "20.08.2020", date.getAttribute("value"));
        Assert.assertEquals("Ошибка ввода комментария", "йцукен", comment.getAttribute("value"));

        //отправка формы
        sendButton.click();

        //проверка сообщения об ошибке при некорректном email
        try {
            driver.findElement(By.xpath("//span[text()='Введите адрес электронной почты']"));
        } catch (org.openqa.selenium.NoSuchElementException e) {
            System.out.println("Сообщение об ошибке не выводится");
        }

        Thread.sleep(5000);
    }

    @After
    public void endTest() {
        driver.quit();
    }
}
