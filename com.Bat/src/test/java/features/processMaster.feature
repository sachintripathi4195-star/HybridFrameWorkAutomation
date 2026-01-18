@Reg
Feature: Click Master Data

  Background:
    When navigate to application
    When user enters valid credentials
    Then user should be logged in successfully
    When Master Data Should be Displayed

  @Reg
  Scenario: Verify Master Data Options
    And user click MasterData
    Then Process Master Should be displayed

 @Reg
Scenario: Save Process Master Data
  And user click MasterData
  Then Process Master option should be displayed
  When user click on Process Master
  Given user enter process Master Data
  When user click on Save button


  @Reg
  Scenario: Verify error message when Process Rate is missing
    And user click MasterData
    Then Process Master option should be displayed
    When user click on Process Master
    Given user enter process detail information without process rate
    And user enter specific applicability customer information
    Then warning toast message "Rate required.!" should be displayed

  @Reg @Negative
  Scenario: Verify error message when invalid Process Rate is entered
    And user click MasterData
    Then Process Master option should be displayed
    When user click on Process Master
    Given user enter process detail information with invalid process rate
    And user enter specific applicability customer information
    Then warning toast message "Rate required.!" should be displayed

  @Reg @Negative @Efficiency
  Scenario: Verify error message when Efficiency is more than 100
    And user click MasterData
    Then Process Master option should be displayed
    When user click on Process Master
    Given user enter process detail information with efficiency more than 100
    Then warning toast message "Efficiency cannot exceed 100." should be displayed
