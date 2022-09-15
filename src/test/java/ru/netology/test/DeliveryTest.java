package ru.netology.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.github.javafaker.Faker;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.Value;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.data.DataGenerator;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Locale;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class DeliveryTest {
    @BeforeAll
    static void setUpAll(){
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    LocalDate today = LocalDate.now();

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 5;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 10;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        $("input[placeholder='Город']").setValue(validUser.getCity());
        $("input[placeholder='Дата встречи']").sendKeys(Keys.CONTROL + "a");
        $("input[placeholder='Дата встречи']").sendKeys(Keys.DELETE);
        $("input[placeholder='Дата встречи']").setValue(firstMeetingDate);
        $("input[name='name']").setValue(validUser.getName());
        $("input[name='phone']").setValue(validUser.getPhone());
        $("label[data-test-id='agreement']").click();
        $x("//span[contains(text(),'Запланировать')]//ancestor::button").click();
        $x("//*[contains(text(),'Успешно')]").shouldBe(visible, Duration.ofSeconds(30));
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate), Duration.ofSeconds(30))
                .shouldBe(visible);

        $("input[placeholder='Дата встречи']").sendKeys(Keys.CONTROL + "a");
        $("input[placeholder='Дата встречи']").sendKeys(Keys.DELETE);
        $("input[placeholder='Дата встречи']").setValue(secondMeetingDate);
        $x("//span[contains(text(),'Запланировать')]//ancestor::button").click();
        $("div[data-test-id='replan-notification']")
                .shouldBe(visible, Duration.ofSeconds(30));
        $x("//span[contains(text(),'Перепланировать')]//ancestor::button").click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate), Duration.ofSeconds(30))
                .shouldBe(visible);
        $x("//*[contains(text(),'Успешно')]").shouldBe(visible, Duration.ofSeconds(30));

    }
}


