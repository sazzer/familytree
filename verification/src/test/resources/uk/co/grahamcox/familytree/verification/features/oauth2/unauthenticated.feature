Feature: Unauthenticated

@wip
Scenario: Not Authenticated
  When I look up who I'm logged in as
  Then I am not authenticated