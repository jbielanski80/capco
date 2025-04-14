Feature: Books API

  Scenario: Get all Books
    Given I have the base API URL
    When I send a GET request to "/books"
    Then The response status should be 200
    And The response should have at least 6000 books returned

  Scenario: Get specific Book
    When I send a GET request to "/books/3015"
    Then The response status should be 200
    And The response should return 1 specific book with specific values: "The Moving Toyshop", "Jeffrey Ernser", "Berkley Books", "Realistic fiction", 612, 304.44
    And The response id should be "3015"

  Scenario: Create new Book
    When I send a POST request to "/books"
    Then The response status should be 200
    And The response should return 1 specific book with specific values: "Refactoring: Improving the Design of Existing Code", "Martin Fowler", "Addison-Wesley Professional", "Programming", 448, 35.5

  Scenario: Create, Update and Delete Book
    When I send a POST, UPDATE, DELETE requests to "/books/" with response status 200
    And The response should return 1 specific book with specific values: "Totally different name", "Unknown Author", "Garage Production", "Fairy Tale", 448, 12.0