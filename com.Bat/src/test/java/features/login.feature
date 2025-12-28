@smoke
Feature: Login to site

Background:
  
    When navigate to application


  Scenario: Launch URL and login
   
    When user enters valid credentials
    Then user should be logged in successfully
 
 
 
 
 
 
 