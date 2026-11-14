import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static com.codeborne.selenide.Selenide.*;


@DisplayName("Домашняя работа по JUnit, параметризованные тесты")
public class SauceDemoTests {
    @BeforeAll
    static void SetUp(){
        Configuration.baseUrl = "https://www.saucedemo.com/";
        Configuration.browserSize = "1920x1080";
    }

    @DisplayName("Проверка ошибки при вводе неверного логина")
    @ParameterizedTest( name = "Логин: {0}")
    @ValueSource(strings = {"locked_out_user ", "invalid_user", "admin"})
    void invalidLoginTest(String username){
        open("/");

        // Вводим неверный логин и правильный пароль
        $("#user-name").setValue(username);
        $("#password").setValue("secret_sauce");
        $("#login-button").click();

        // Проверяем, что появился блок с ошибкой
        $("[data-test='error']").shouldBe(Condition.visible);
    }

    @DisplayName("Проверка успешного входа")
    @Tag("smoke")
    @ParameterizedTest(name = "Пользователь: {0}")
    @CsvSource(value = {
            "standard_user, secret_sauce",
            "problem_user, secret_sauce"
    })
    void successfulLoginTest(String username, String password){
        open("/");
        $("#user-name").setValue("standard_user");
        $("#password").setValue("secret_sauce");
        $("#login-button").click();

        $("[data-test='title']").shouldHave(Condition.text("Products"));
    }
    @DisplayName("Проверка авторизации на данных из файла")
    @ParameterizedTest(name = "Пользователь: {0}")
    @CsvFileSource(resources = "/users/login_data.csv")

    void successfulTextLogins(String username, String password, boolean expectedResult){
        open("/");
        $("#user-name").setValue(username);
        $("#password").setValue(password);
        $("#login-button").click();

        if (expectedResult){
            $("[data-test='title']").shouldHave(Condition.text("Products"));
        }else {
            $("[data-test='error']").shouldBe(Condition.visible);
        }
    }
}
