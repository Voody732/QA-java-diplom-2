package ru.yandex.praktikum.steps;


import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.dto.CreateOrderRequest;

import static io.restassured.RestAssured.given;
import static ru.yandex.praktikum.StaticUrls.*;


public class CreatingOrderSteps {
    @Step("Создание заказа с авторизацией и валидными ингредиентами")
    public ValidatableResponse createOrder(CreateOrderRequest request, String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASEURL)
                .header("Authorization", accessToken)
                .body(request)
                .when()
                .post(CREATEORDERHANDLER)
                .then();
    }

    @Step("Создание заказа с без авторизации и валидными ингредиентами")
    public ValidatableResponse createOrder(CreateOrderRequest request) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASEURL)
                .body(request)
                .when()
                .post(CREATEORDERHANDLER).
                then();
    }

    @Step("получение всех списка ингредиентов")
    public ValidatableResponse getIngredients() {
        return given()
                .baseUri(BASEURL)
                .when()
                .get(INGREDIENTS)
                .then();
    }
}


