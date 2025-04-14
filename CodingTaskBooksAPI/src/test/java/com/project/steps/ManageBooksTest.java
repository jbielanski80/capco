package com.project.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.http.ContentType.JSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ManageBooksTest {

    private Response response, responseDelete;

    private final String username = "user4";
    private final String password = "hlB5U1rA";

    private String createdBookId;

    @Given("I have the base API URL")
    public void givenBaseApiUrl() {
        RestAssured.baseURI = "http://77.102.250.113:17354/api/v1/";
    }

    @When("I send a GET request to {string}")
    public void sendGetRequest(String endpoint) {

        //get
        response = RestAssured.given()
                .auth().basic(username, password)
                .when()
                .get(endpoint);
    }

    @When("I send a POST request to {string}")
    public void sendPostRequest(String endpoint) {

        String requestBody = "{\"name\":\"Refactoring: Improving the Design of Existing Code\",\"author\":\"Martin Fowler\",\"publication\":\"Addison-Wesley Professional\",\"category\":\"Programming\",\"pages\":448,\"price\":35.50}";

        //create
        response = RestAssured.given()
                .auth().basic(username, password)
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    @When("I send a POST, UPDATE, DELETE requests to {string} with response status {int}")
    public void sendPostUpdateDeleteRequests(String endpoint, int statusCode) {

        String requestBody = "{\"name\":\"Refactoring: Improving the Design of Existing Code\",\"author\":\"Martin Fowler\",\"publication\":\"Addison-Wesley Professional\",\"category\":\"Programming\",\"pages\":448,\"price\":35.50}";

        //create
        response = RestAssured.given()
                .auth().basic(username, password)
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post(endpoint);

        assertEquals(statusCode, response.getStatusCode());
        createdBookId = response.jsonPath().getString("id");


        //ToDo - BUG mentioned in BUG Test Report (should be 32, is 448 number of pages - when API bug is fixed, change in books.feature file to 32 value)
        requestBody = "{\"id\":" + createdBookId + ",\"name\":\"Totally different name\",\"author\":\"Unknown Author\",\"publication\":\"Garage Production\",\"category\":\"Fairy Tale\",\"pages\":32,\"price\":12}";

        //update
        response = RestAssured.given()
                .auth().basic(username, password)
                .contentType(JSON)
                .body(requestBody)
                .when()
                .put(endpoint + createdBookId);

        assertEquals(statusCode, response.getStatusCode());


        //delete
        responseDelete = RestAssured.given()
                .auth().basic(username, password)
                .when()
                .delete(endpoint + createdBookId);

        assertEquals(statusCode, responseDelete.getStatusCode());
    }

    @Then("The response status should be {int}")
    public void checkStatusCode(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @And("The response id should be {string}")
    public void checkId(String id) {
        assertEquals(id, response.jsonPath().getString("id"));
    }

    @And("The response should have at least {int} books returned")
    public void countBooksInResponse(int numOfBooks) {
        JsonPath jsonPath = response.jsonPath();

        int numberOfBooks = jsonPath.getList("books").size();
        assertTrue(numberOfBooks > numOfBooks);
    }

    @And("The response should return 1 specific book with specific values: {string}, {string}, {string}, {string}, {int}, {float}")
    public void checkBookDetailsInResponse(String name, String author, String publication, String category, Integer pages, Float price) {
        JsonPath jsonPath = response.jsonPath();

        assertEquals("Value name does NOT match", name, jsonPath.getString("name"));
        assertEquals("Value author does NOT match", author, jsonPath.getString("author"));

        assertEquals("Value publication does NOT match", publication, jsonPath.getString("publication"));
        assertEquals("Value category does NOT match", category, jsonPath.getString("category"));

        assertEquals("Value publication does NOT match", pages.toString(), jsonPath.getString("pages"));
        assertEquals("Value category does NOT match", price.toString(), jsonPath.getString("price"));
    }

}
