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
    
    
    
    
    