@Reg
Feature: Click Login button

 
  
  @Reg
  Scenario: Verify Login button Is Working Or Not
   
    
    When navigate to application
    When user enters valid credentials
    Then user should be logged in successfully


    
    